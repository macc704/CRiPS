package ch.server;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ch.conn.framework.CHConnection;
import ch.conn.framework.CHFile;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHEntryRequest;
import ch.conn.framework.packets.CHEntryResult;
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
import ch.library.CHFileSystem;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.sync.CFileList;

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

				if (obj instanceof CHEntryRequest) {
					processEntryRequest((CHEntryRequest) obj);
				} else if (obj instanceof CHSourceChanged) {
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
		String password = request.getPassword();
		Color color = request.getColor();
		// login process
		if (login(new CHUserState(user, true, color), conn) == false) {
			connectionPool.sendToOne(new CHLoginResult(CHLoginCheck.FAILURE),
					user);
			return null;
		}

		CHLoginCheck loginCheck = new CHLoginCheck(user, password);
		int result = loginCheck.checkPattern(port);

		connectionPool.sendToOne((new CHLoginResult(result)), user);
		if (result == CHLoginCheck.NEW_ENTRY) {
			return user;
		}

		List<CHUserState> userStates = connectionPool.getUserStates();
		// for (String aUser : getAllUsers()) {
		// colorが全て同じになる原因
		// connectionManagerにCHUserStateのリストを追加する？
		// userStates.add(new CHUserState(aUser, true, color));
		// }

		connectionPool.broadCast(new CHLoginMemberChanged(userStates));
		connectionPool.sendToOne(new CHFilelistRequest(null), user);
		return user;
	}

	private boolean login(CHUserState userState, CHConnection conn) {
		boolean result = connectionPool.login(userState, conn);
		return result;
	}

	private void processEntryRequest(CHEntryRequest request) {
		CHLoginCheck loginCheck = new CHLoginCheck(request.getUser(),
				request.getPassword());
		connectionPool.sendToOne(
				new CHEntryResult(loginCheck.entryNewUser(port)),
				request.getUser());
	}

	private void processLogoutRequest(CHLogoutRequest request, CHConnection conn) {
		logout(conn);
	}

	private boolean logout(CHConnection conn) {
		boolean result = connectionPool.logout(conn);
		if (result == true) {
			List<CHUserState> userStates = new ArrayList<CHUserState>();
			for (String aUser : getAllUsers()) {
				userStates.add(new CHUserState(aUser, true, null));
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
		CHFileSystem.saveFiles(response.getFiles(),
				CHFileSystem.getUserDirForServer(response.getUser(), port));
	}

	private void processFileRequest(CHFileRequest request, CHConnection conn) {

		List<CHFile> files = CHFileSystem.getCHFiles(
				request.getRequestFilePaths(),
				CHFileSystem.getUserDirForServer(request.getUser(), port));

		connectionPool.sendToOne(new CHFileResponse(request.getUser(), files),
				connectionPool.getUser(conn));
	}

	private void processFilelistRequest(CHFilelistRequest request,
			CHConnection conn) {

		CFileList fileList = CHFileSystem.getServerFileList(request.getUser(),
				port);

		connectionPool.sendToOne(new CHFilelistResponse(request.getUser(),
				fileList), connectionPool.getUser(conn));
	}

	private void processFilelistResponse(CHFilelistResponse response) {
		String user = response.getUser();
		CDirectory copyDir = CHFileSystem.getUserDirForServer(user, port);

		List<String> requestFilePaths = CHFileSystem.getRequestFilePaths(
				response.getFileList(), copyDir);

		connectionPool.sendToOne(new CHFileRequest(null, requestFilePaths),
				user);
	}
}