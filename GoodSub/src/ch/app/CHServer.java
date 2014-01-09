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
import java.util.HashMap;
import java.util.List;

import ch.connection.CHConnection;
import ch.connection.CHConnectionPool;
import ch.packets.CHFilegetRequest;
import ch.packets.CHFilegetResponse;
import ch.packets.CHFilelistRequest;
import ch.packets.CHFilelistResponse;
import ch.packets.CHLogin;
import ch.packets.CHLoginMemberStatus;
import ch.packets.CHLogout;
import ch.packets.CHLogoutResult;
import ch.packets.CHSourcesendRequest;
import ch.packets.CHSourcesendResponse;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.filesystem.sync.CFileList;
import clib.common.filesystem.sync.CFileListDifference;
import clib.common.filesystem.sync.CFileListUtils;

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

				if (obj instanceof CHLogin) {
					typeLogin((CHLogin) obj, conn);
				} else if (obj instanceof CHSourcesendRequest) {
					typeSource((CHSourcesendRequest) obj, conn);
				} else if (obj instanceof CHLogout) {
					typeLogout((CHLogout) obj, conn);
				} else if (obj instanceof CHFilegetResponse) {
					typeFileGetRes((CHFilegetResponse) obj);
				} else if (obj instanceof CHFilegetRequest) {
					typeFileGetReq((CHFilegetRequest) obj, conn);
				} else if (obj instanceof CHFilelistRequest) {
					typeFileListReq((CHFilelistRequest) obj, conn);
				} else if (obj instanceof CHFilelistResponse) {
					processFilelistResponse((CHFilelistResponse) obj, conn);
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

	private void typeLogin(CHLogin chLogin, CHConnection conn) {
		String myName = chLogin.getUser();

		connMap.put(myName, conn);

		// 名前が被った場合
		// if (members.contains(myName)) {
		// myName = myName + "*";
		// chPacket.setExist(true);
		// }

		createMembersDir(myName);

		members.add(myName);
		out.println("name: " + myName + " add list.");

		// chPacket.setMyName(myName);
		// chPacket.setMembers(members);
		// chPacket.setCommand(CHPacket.LOGIN_RESULT);
		//
		// connectionPool.sendToOne(chPacket, conn);
		// connectionPool.sendToOne(new CHLogoutResult(CHPacket.LOGIN_RESULT,
		// myName), conn);

		// chPacket.setExist(false);
		// chPacket.setCommand(CHPacket.LOGIN_MEMBER_STATUS);
		// connectionPool.broadcast(chPacket, conn);

		connectionPool.broadcastAll(new CHLoginMemberStatus(members));

		// タイミング検討
		connectionPool.sendToOne(new CHFilelistRequest(null), conn);
	}

	private void typeSource(CHSourcesendRequest chSourcesemdReq,
			CHConnection conn) {

		connectionPool.broadcast(
				new CHSourcesendResponse(chSourcesemdReq.getUser(),
						chSourcesemdReq.getSource(), chSourcesemdReq
								.getCurrentFileName()), conn);
	}

	private void typeLogout(CHLogout chLogout, CHConnection conn) {
		members.remove(chLogout.getMyName());
		connMap.remove(chLogout.getMyName());

		connectionPool.broadcastAll(new CHLogoutResult(chLogout.getMyName()));
		connectionPool.close(conn);
	}

	private void typeFileGetRes(CHFilegetResponse recivedCHPacket) {

		// File directory = searchMenbersDir(recivedCHPacket.getMember());
		// List<File> files = Arrays.asList(directory.listFiles());
		// for (File aFile : files) {
		// if (recivedCHPacket.getRemovedFiles().contains(aFile.getName())) {
		// aFile.delete();
		// }
		// }
		//
		// byte[] bytes = recivedCHPacket.getBytes();
		// String myName = recivedCHPacket.getMyName();
		// saveFile(myName, recivedCHPacket.getFile(), bytes);
	}

	private void typeFileGetReq(CHFilegetRequest chFilegetReq, CHConnection conn) {
		// sendFile(Arrays.asList(searchMenbersDir(chFilegetReq.getAdressee())
		// .listFiles()), conn, chFilegetReq, chFilegetReq.getMember());
	}

	private void typeFileListReq(CHFilelistRequest chFilelistReq,
			CHConnection conn) {
		// File directory = searchMenbersDir(chFilelistReq.getAdressee());
		// List<File> files = Arrays.asList(directory.listFiles());
		// List<String> fileNames = new ArrayList<String>();
		// for (File aFile : files) {
		// fileNames.add(aFile.getName());
		// }
		//
		// connectionPool.sendToOne(
		// new CHFilelistResponse(chFilelistReq.getMember(), fileNames),
		// conn);

	}

	private void processFilelistResponse(CHFilelistResponse response,
			CHConnection conn) {

		CFileList fileListClient = response.getFileList();
		File dir = searchMenbersDir(response.getUser());
		CDirectory cDir = CFileSystem.findDirectory(dir.getAbsolutePath());
		CFileList fileListServer = new CFileList(cDir);

		List<CFileListDifference> differences = CFileListUtils.compare(
				fileListClient, fileListServer);

		List<String> requestFilePaths = new ArrayList<String>();
		for (CFileListDifference aDifference : differences) {
			switch (aDifference.getKind()) {
			case CREATED:
			case UPDATED:
				requestFilePaths.add(aDifference.getPath());
				break;
			case REMOVED:
				cDir.findChild(new CPath(aDifference.getPath())).delete();
				break;
			default:
				throw new RuntimeException();
			}
		}

		connectionPool.sendToOne(new CHFilegetRequest(null, requestFilePaths),
				conn);
	}

	/************
	 * ファイル操作
	 ************/

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
			CHFilegetRequest recivedCHPacket, String member) {
		//
		// for (File aFile : files) {
		// if (recivedCHPacket.getAddedFiles().contains(aFile.getName())) {
		// byte[] bytes = convertFileToByte(aFile);
		// connectionPool.sendToOne(new CHFilegetResponse(member, aFile,
		// bytes), conn);
		// }
		// if (aFile.isDirectory() && !aFile.getName().startsWith(".")) {
		// sendFile(Arrays.asList(aFile.listFiles()), conn,
		// recivedCHPacket, member);
		// }
		// }
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