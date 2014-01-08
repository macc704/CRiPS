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
					case CHPacket.SOURCE:
						typeSource(recivedCHPacket, conn);
						break;
					case CHPacket.LOGUOT:
						typeLogout(recivedCHPacket, conn);
						break;
					case CHPacket.FILE:
						typeFile(recivedCHPacket, conn);
						break;
					case CHPacket.SAVE_FILE:
						typeSaveFile(recivedCHPacket);
						break;
					case CHPacket.FILE_SEND_REQUEST:
						typeFileSendRequest(recivedCHPacket, conn);
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
		chPacket.setCommand(CHPacket.LOGIN_MEMBER);
		connectionPool.broadcast(chPacket, conn);
	}

	private void typeSource(CHPacket recivedCHPacket, CHConnection conn) {
		CHPacket chPacket = new CHPacket();
		chPacket.setMyName(recivedCHPacket.getMyName());
		chPacket.setSource(recivedCHPacket.getSource());
		chPacket.setCurrentFileName(recivedCHPacket.getCurrentFileName());
		chPacket.setCommand(CHPacket.RECIVE_SOURCE);
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

	private void typeFile(CHPacket recivedCHPacket, CHConnection conn) {
		CHPacket chPacket = new CHPacket();
		chPacket.setMyName(recivedCHPacket.getMyName());
		chPacket.setBytes(recivedCHPacket.getBytes());
		chPacket.setFileNames(recivedCHPacket.getFileNames());
		chPacket.setCommand(CHPacket.RECIVE_FILE);
		connectionPool.broadcast(chPacket, conn);
	}

	private void typeSaveFile(CHPacket recivedCHPacket) {
		String myName = recivedCHPacket.getMyName();
		List<String> fileNames = new ArrayList<String>();
		List<byte[]> bytes = new ArrayList<byte[]>();
		fileNames = recivedCHPacket.getFileNames();
		bytes = recivedCHPacket.getBytes();
		for (String fileName : fileNames) {
			if (fileName.endsWith(".java")) {
				saveFile(myName, fileName,
						bytes.get(fileNames.indexOf(fileName)));
			}
		}
	}

	private void typeFileSendRequest(CHPacket recivedCHPacket, CHConnection conn) {
		sendFiles(searchMenbersDir(recivedCHPacket.getAdressee()),
				recivedCHPacket, conn);
	}

	/************
	 * ファイル操作
	 ************/

	private void createMembersDir(String name) {
		File membersDir = new File(Integer.toString(port), name);
		memberDirs.add(membersDir);
		membersDir.mkdir();
	}

	private void saveFile(String parent, String fileName, byte[] bytes) {
		File file = new File(Integer.toString(port) + "/" + parent, fileName);
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

	private void sendFiles(File directory, CHPacket recivedPacket,
			CHConnection conn) {
		List<File> files = new ArrayList<File>();
		files = Arrays.asList(directory.listFiles());

		List<byte[]> bytes = new ArrayList<byte[]>();
		List<String> fileNames = new ArrayList<String>();
		for (File aFile : files) {
			if (aFile.isFile()) {
				bytes.add(convertFileToByte(aFile));
				fileNames.add(aFile.getName());
			}
		}

		CHPacket chPacket = new CHPacket();
		chPacket.setMyName(recivedPacket.getMyName());
		chPacket.setBytes(bytes);
		chPacket.setFileNames(fileNames);
		chPacket.setCommand(CHPacket.RECIVE_FILE);
		connectionPool.sendToOne(chPacket, conn);
	}

	public File searchMenbersDir(String name) {
		for (File aFile : memberDirs) {
			if (name.equals(aFile.getName())) {
				return aFile;
			}
		}
		return null;
	}

	public byte[] convertFileToByte(File file) {

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
