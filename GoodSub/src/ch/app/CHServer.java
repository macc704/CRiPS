package ch.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ch.connection.CHConnection;
import ch.connection.CHConnectionPool;
import ch.connection.CHPacket;
import ch.packets.CHFilelistRequest;

public class CHServer {
	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("argument is not found");
		} else {
			port = Integer.parseInt(args[0]);
			new CHServer().run();
		}
	}

	private static int port;
	private CHConnectionPool connectionPool = new CHConnectionPool();
	private List<String> members = new ArrayList<String>();
	private HashMap<String, CHConnection> connMap = new HashMap<String, CHConnection>();
	private List<File> memberDirs = new ArrayList<File>();

	private static PrintStream out = System.out;

	public void run() {

		ServerSocket serverSock = null;
		File groupDir = new File(Integer.toString(port));
		groupDir.mkdir();
		try {
			serverSock = new ServerSocket(port);
			while (true) {
				out.println("waiting new client..(port:" + port + ")");
				final Socket sock = serverSock.accept();
				out.println("accepted..");
				Thread th = new Thread() {
					public void run() {
						CHConnection conn = connectionPool.newConnection(sock);
						if (conn != null) {
							out.println("one connection established.");
							connectionPool.addConnection(conn);
							loopForOneClient(conn);
						}
					}
				};
				th.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				serverSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loopForOneClient(CHConnection conn) {
		try {
			while (conn.established()) {
				Object obj = conn.read();

				if (obj instanceof CHPacket) {
					CHPacket recivedCHPacket = (CHPacket) obj;
					int command = recivedCHPacket.getCommand();
					switch (command) {
					case CHPacket.LOGIN:
						typeLogin(recivedCHPacket, conn);
						break;
					case CHPacket.SOURCESEND_REQ:
						typeSource(recivedCHPacket, conn);
						break;
					case CHPacket.LOGUOT:
						typeLogout(recivedCHPacket, conn);
						break;
					case CHPacket.FILEGET_RES:
						typeFileGetRes(recivedCHPacket);
						break;
					case CHPacket.FILEGET_REQ:
						typeFileGetReq(recivedCHPacket, conn);
						break;
					case CHPacket.FILELIST_REQ:
						typeFileListReq(recivedCHPacket, conn);
						break;
					case CHPacket.FILELIST_RES:
						typeFileListRes(recivedCHPacket, conn);
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			connectionPool.close(conn);
			out.println("one connection killed");
		}
	}

	/**************
	 * コマンド別処理
	 **************/

	private void typeLogin(CHPacket recivedCHPacket, CHConnection conn) {
		String myName = recivedCHPacket.getMyName();

		connMap.put(myName, conn);

		CHPacket chPacket = new CHPacket();

		// 名前が被った場合
		if (members.contains(myName)) {
			myName = myName + "*";
			chPacket.setExist(true);
		}

		createMembersDir(myName);

		members.add(myName);
		out.println("name: " + myName + " add list.");

		chPacket.setMyName(myName);
		chPacket.setMembers(members);
		chPacket.setCommand(CHPacket.LOGIN_RESULT);

		connectionPool.sendToOne(chPacket, conn);
		chPacket.setExist(false);
		chPacket.setCommand(CHPacket.LOGIN_MEMBER_STATUS);
		connectionPool.broadcast(chPacket, conn);

		// タイミング検討
		connectionPool.sendToOne(new CHFilelistRequest(CHPacket.FILELIST_REQ,
				null), conn);
	}

	private void typeSource(CHPacket recivedCHPacket, CHConnection conn) {
		CHPacket chPacket = new CHPacket();
		chPacket.setMyName(recivedCHPacket.getMyName());
		chPacket.setSource(recivedCHPacket.getSource());
		chPacket.setCurrentFileName(recivedCHPacket.getCurrentFileName());
		chPacket.setCommand(CHPacket.SOURCESEND_RES);
		connectionPool.broadcast(chPacket, conn);
	}

	private void typeLogout(CHPacket recivedCHPacket, CHConnection conn) {
		members.remove(recivedCHPacket.getMyName());
		connMap.remove(recivedCHPacket.getMyName());

		CHPacket chPacket = new CHPacket();
		chPacket.setMyName(recivedCHPacket.getMyName());
		chPacket.setCommand(CHPacket.LOGOUT_RESULT);
		connectionPool.broadcastAll(chPacket);
		connectionPool.close(conn);
	}

	private void typeFileGetRes(CHPacket recivedCHPacket) {

		File directory = searchMenbersDir(recivedCHPacket.getMyName());
		List<File> files = Arrays.asList(directory.listFiles());
		for (File aFile : files) {
			if (recivedCHPacket.getRemovedFiles().contains(aFile.getName())) {
				aFile.delete();
			}
		}

		byte[] bytes = recivedCHPacket.getBytes();
		String myName = recivedCHPacket.getMyName();
		saveFile(myName, recivedCHPacket.getFile(), bytes);
	}

	private void typeFileGetReq(CHPacket recivedCHPacket, CHConnection conn) {
		sendFile(Arrays.asList(searchMenbersDir(recivedCHPacket.getAdressee())
				.listFiles()), conn, recivedCHPacket);
	}

	private void typeFileListReq(CHPacket recivedCHPacket, CHConnection conn) {
		File directory = searchMenbersDir(recivedCHPacket.getAdressee());
		List<File> files = Arrays.asList(directory.listFiles());
		List<String> fileNames = new ArrayList<String>();
		for (File aFile : files) {
			fileNames.add(aFile.getName());
		}

		CHPacket chPacket = new CHPacket();
		chPacket.setFileNames(fileNames);
		chPacket.setAdressee(recivedCHPacket.getAdressee());
		chPacket.setCommand(CHPacket.FILELIST_RES);
		connectionPool.sendToOne(chPacket, conn);

	}

	private void typeFileListRes(CHPacket recivedCHPacket, CHConnection conn) {
		List<String> clientFileNames = recivedCHPacket.getFileNames();

		List<File> files = Arrays.asList(searchMenbersDir(recivedCHPacket
				.getMyName()));
		List<String> serverFileNames = new ArrayList<String>();
		for (File aFile : files) {
			serverFileNames.add(aFile.getName());
		}

		CHPacket chPacket = setDiffToPacket(serverFileNames, clientFileNames,
				recivedCHPacket);
		chPacket.setCommand(CHPacket.FILEGET_REQ);
		connectionPool.sendToOne(chPacket, conn);

	}

	/************
	 * ファイル操作
	 ************/

	private CHPacket setDiffToPacket(List<String> serverFileNames,
			List<String> clientFileNames, CHPacket recivedCHPacket) {

		List<String> addedFiles = new ArrayList<String>();
		List<String> removedFiles = new ArrayList<String>();

		for (String aServerFileName : serverFileNames) {
			if (!clientFileNames.contains(aServerFileName)) {
				removedFiles.add(aServerFileName);
			}
		}

		for (String aClientFileName : clientFileNames) {
			if (!serverFileNames.contains(aClientFileName)) {
				addedFiles.add(aClientFileName);
			}
		}

		CHPacket chPacket = recivedCHPacket;
		chPacket.setRemovedFiles(removedFiles);
		chPacket.setAddedFiles(addedFiles);

		return chPacket;
	}

	private void createMembersDir(String name) {
		File membersDir = new File(Integer.toString(port), name);
		memberDirs.add(membersDir);
		membersDir.mkdir();
	}

	private void saveFile(String parent, File recivedFile, byte[] bytes) {
		String path = (recivedFile.getPath()).replace("MyProjects/final", "");

		File file = new File(Integer.toString(port) + "/" + parent + path);
		if (bytes == null) {
			file.mkdir();
		} else {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file, false);
				fos.write(bytes);
				file.createNewFile();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public File searchMenbersDir(String name) {
		for (File aFile : memberDirs) {
			if (name.equals(aFile.getName())) {
				return aFile;
			}
		}
		return null;
	}

	private void sendFile(List<File> files, CHConnection conn,
			CHPacket recivedCHPacket) {
		CHPacket chPacket = recivedCHPacket;
		chPacket.setCommand(CHPacket.FILEGET_RES);

		for (File aFile : files) {
			if (recivedCHPacket.getAddedFiles().contains(aFile.getName())) {
				byte[] bytes = convertFileToByte(aFile);
				chPacket.setBytes(bytes);
				chPacket.setFile(aFile);
				connectionPool.sendToOne(chPacket, conn);
			}
			if (aFile.isDirectory() && !aFile.getName().startsWith(".")) {
				sendFile(Arrays.asList(aFile.listFiles()), conn,
						recivedCHPacket);
			}
		}
	}

	public byte[] convertFileToByte(File file) {

		if (file.isDirectory()) {
			return null;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int i = 0;
		try {
			while ((i = fis.read()) != -1) {
				baos.write(i);
			}
			baos.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}
}