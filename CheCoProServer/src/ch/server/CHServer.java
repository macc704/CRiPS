package ch.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ch.conn.framework.CHConnection;
import ch.conn.framework.CHFile;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHFileRequest;
import ch.conn.framework.packets.CHFileResponse;
import ch.conn.framework.packets.CHFilelistRequest;
import ch.conn.framework.packets.CHFilelistResponse;
import ch.conn.framework.packets.CHLoginMemberChanged;
import ch.conn.framework.packets.CHLoginRequest;
import ch.conn.framework.packets.CHLoginResult;
import ch.conn.framework.packets.CHLogoutRequest;
import ch.conn.framework.packets.CHSourceChanged;
import ch.conn.framework.packets.CHSourcesendResponse;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.filesystem.sync.CFileList;
import clib.common.filesystem.sync.CFileListDifference;
import clib.common.filesystem.sync.CFileListUtils;

public class CHServer {

	public static CHLogger out = new CHLogger();

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

	private CHServerConnectionManager connectionPool = new CHServerConnectionManager();

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
						CHConnection conn = newConnection(sock);
						if (conn != null) {
							out.println("one connection established.");
							Object request = conn.read();
							if (!(request instanceof CHLoginRequest)) {
								conn.close();
								out.println("the first request is not CHLoginRequest");
								return;
							}

							String user = processLogin(
									(CHLoginRequest) request, conn);
							if (user == null) {
								conn.close();
								out.println("failed to login user=" + user);
								return;
							}

							loopForOneClient(user, conn);
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

	public CHConnection newConnection(Socket sock) {
		CHConnection conn = new CHConnection(sock);
		boolean success = conn.shakehandForServer();
		if (!success) {
			return null;
		}
		return conn;
	}

	private List<String> getAllUsers() {
		return connectionPool.getAllUsers();
	}

	private void loopForOneClient(String user, CHConnection conn) {
		try {
			while (conn.established()) {
				Object obj = conn.read();
				out.println("received by user: " + user + ", msssage: " + obj);

				if (obj instanceof CHSourceChanged) {
					processSourceChanged((CHSourceChanged) obj);
				} else if (obj instanceof CHLogoutRequest) {
					processLogoutRequest(((CHLogoutRequest) obj), conn);
				} else if (obj instanceof CHFileResponse) {
					processFileResponse((CHFileResponse) obj);
				} else if (obj instanceof CHFileRequest) {
					processFileRequest((CHFileRequest) obj, conn);
				} else if (obj instanceof CHFilelistRequest) {
					processFilelistRequest((CHFilelistRequest) obj, conn);
				} else if (obj instanceof CHFilelistResponse) {
					processFilelistResponse((CHFilelistResponse) obj);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			logout(conn);
		}
	}

	private String processLogin(CHLoginRequest request, CHConnection conn) {
		String user = request.getUser();
		out.println(user);
		// login process
		if (login(user, conn) == false) {
			connectionPool.sendToOne(new CHLoginResult(false), user);
			return null;
		}

		connectionPool.sendToOne(new CHLoginResult(true), user);

		List<CHUserState> userStates = new ArrayList<CHUserState>();
		for (String aUser : getAllUsers()) {
			userStates.add(new CHUserState(aUser, true));
		}

		connectionPool.broadCast(new CHLoginMemberChanged(userStates));
		connectionPool.sendToOne(new CHFilelistRequest(null), user);
		return user;
	}

	private boolean login(String user, CHConnection conn) {
		boolean result = connectionPool.login(user, conn);
		return result;
	}

	private void processLogoutRequest(CHLogoutRequest request, CHConnection conn) {
		logout(conn);
	}

	private boolean logout(CHConnection conn) {
		boolean result = connectionPool.logout(conn);
		if (result == true) {
			List<CHUserState> userStates = new ArrayList<CHUserState>();
			for (String aUser : getAllUsers()) {
				userStates.add(new CHUserState(aUser, true));
			}
			connectionPool.broadCast(new CHLoginMemberChanged(userStates));
		}
		return result;
	}

	private void processSourceChanged(CHSourceChanged request) {
		connectionPool.broadCastExceptSender(
				new CHSourcesendResponse(request.getUser(),
						request.getSource(), request.getCurrentFileName()),
				request.getUser());
	}

	private void processFileResponse(CHFileResponse response) {
		CDirectory userDir = getUserDir(response.getUser());
		for (CHFile aFile : response.getFiles()) {
			CFile file = userDir.findOrCreateFile(aFile.getPath());
			file.saveAsByte(aFile.getBytes());
		}

	}

	private void processFileRequest(CHFileRequest request, CHConnection conn) {
		String user = request.getUser();
		CDirectory userDir = getUserDir(user);

		List<CHFile> files = new ArrayList<CHFile>();
		for (String path : request.getRequestFilePaths()) {
			CFile file = userDir.findFile(path);
			byte[] byteArray = file.loadAsByte();
			files.add(new CHFile(path, byteArray));
		}

		connectionPool.sendToOne(new CHFileResponse(user, files),
				connectionPool.getUser(conn));
	}

	private void processFilelistRequest(CHFilelistRequest request,
			CHConnection conn) {

		CDirectory userDir = getUserDir(request.getUser());
		CFileList fileList = new CFileList(userDir);
		connectionPool.sendToOne(new CHFilelistResponse(request.getUser(),
				fileList), connectionPool.getUser(conn));
	}

	private void processFilelistResponse(CHFilelistResponse response) {
		String user = response.getUser();

		CFileList fileListClient = response.getFileList();
		File dir = getUserDir(user).toJavaFile();
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

		connectionPool.sendToOne(new CHFileRequest(null, requestFilePaths),
				user);
	}

	private CDirectory getBaseDir() {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				"CH/" + port);
	}

	private CDirectory getUserDir(String user) {
		return getBaseDir().findOrCreateDirectory(user);
	}
}