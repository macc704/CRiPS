package ch.app;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ch.connection.CHConnection;
import ch.connection.CHConnectionPool;
import ch.packets.CHFile;
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
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.filesystem.sync.CFileList;
import clib.common.filesystem.sync.CFileListDifference;
import clib.common.filesystem.sync.CFileListUtils;

public class CHServer {

	private static PrintStream out = System.out;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("argument is not found");
			System.exit(0);
		} else {
			int port = Integer.parseInt(args[0]);
			CHServer server = new CHServer(port);
			server.run(args);
		}
	}

	private int port;

	private CHConnectionPool connectionPool = new CHConnectionPool();
	private List<String> members = new ArrayList<String>();

	public CHServer(int port) {
		this.port = port;
	}

	public void run(String[] args) {

		ServerSocket serverSock = null;
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
					processLogin((CHLogin) obj, conn);
				} else if (obj instanceof CHSourcesendRequest) {
					processSourcesendRequest((CHSourcesendRequest) obj, conn);
				} else if (obj instanceof CHLogout) {
					processLogout((CHLogout) obj, conn);
				} else if (obj instanceof CHFilegetResponse) {
					processFilegetResponse((CHFilegetResponse) obj);
				} else if (obj instanceof CHFilegetRequest) {
					processFilegetRequest((CHFilegetRequest) obj, conn);
				} else if (obj instanceof CHFilelistRequest) {
					processFilelistRequest((CHFilelistRequest) obj, conn);
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

	private void processLogin(CHLogin login, CHConnection conn) {
		String myName = login.getUser();

		// 名前が被った場合
		// if (members.contains(myName)) {
		// myName = myName + "*";
		// chPacket.setExist(true);
		// }

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

	private void processSourcesendRequest(CHSourcesendRequest request,
			CHConnection conn) {

		connectionPool.broadcast(new CHSourcesendResponse(request.getUser(),
				request.getSource(), request.getCurrentFileName()), conn);
	}

	private void processLogout(CHLogout logout, CHConnection conn) {
		members.remove(logout.getMyName());

		connectionPool.broadcastAll(new CHLogoutResult(logout.getMyName()));
		connectionPool.close(conn);
	}

	private void processFilegetResponse(CHFilegetResponse response) {

		CDirectory userDir = getUserDir(response.getUser());

		for (CHFile aFile : response.getFiles()) {
			CFile file = userDir.findOrCreateFile(aFile.getPath());
			file.saveAsByte(aFile.getBytes());
		}

	}

	private void processFilegetRequest(CHFilegetRequest request,
			CHConnection conn) {
		// sendFile(Arrays.asList(searchMenbersDir(chFilegetReq.getAdressee())
		// .listFiles()), conn, chFilegetReq, chFilegetReq.getMember());
	}

	private void processFilelistRequest(CHFilelistRequest request,
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
		File dir = getUserDir(response.getUser()).toJavaFile();
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

	private CDirectory getBaseDir() {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				"CH/" + port);
	}

	private CDirectory getUserDir(String user) {
		return getBaseDir().findOrCreateDirectory(user);
	}
}