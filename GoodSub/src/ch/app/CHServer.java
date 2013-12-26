package ch.app;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ch.connection.CHConnection;
import ch.connection.CHConnectionPool;
import ch.connection.CHPacket;

public class CHServer {
	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("argument is not found");
		} else {
			new CHServer().run(Integer.parseInt(args[0]));
		}
	}

	private CHConnectionPool connectionPool = new CHConnectionPool();
	private List<String> members = new ArrayList<String>();

	private static PrintStream out = System.out;

	public void run(int port) {

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

	private void typeLogin(CHPacket recivedCHPacket, CHConnection conn) {
		String myName = recivedCHPacket.getMyName();

		CHPacket chPacket = new CHPacket();

		// ñºëOÇ™îÌÇ¡ÇΩèÍçá
		if (members.contains(myName)) {
			myName = myName + "*";
			chPacket.setExist(true);
		}

		members.add(myName);
		out.println("name: " + myName + " add list.");

		chPacket.setMyName(myName);
		chPacket.setMembers(members);
		chPacket.setCommand(CHPacket.LOGIN_RESULT);

		if (chPacket.isExist()) {
			connectionPool.sendMyself(chPacket, conn);
			chPacket.setExist(false);
			connectionPool.broadcast(chPacket, conn);
		} else {
			connectionPool.broadcastAll(chPacket);
		}
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

		CHPacket chPacket = new CHPacket();
		chPacket.setMyName(recivedCHPacket.getMyName());
		chPacket.setCommand(CHPacket.LOGOUT_RESULT);
		connectionPool.broadcast(chPacket, conn);
	}

	private void typeFile(CHPacket recivedCHPacket, CHConnection conn) {
		CHPacket chPacket = new CHPacket();
		chPacket.setMyName(recivedCHPacket.getMyName());
		chPacket.setBytes(recivedCHPacket.getBytes());
		chPacket.setFileNames(recivedCHPacket.getFileNames());
		chPacket.setCommand(CHPacket.RECIVE_FILE);
		connectionPool.broadcast(chPacket, conn);
	}

}
