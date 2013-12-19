package ch.app;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import ch.connection.CHConnection;
import ch.connection.CHConnectionPool;
import ch.connection.CHPacket;
import ch.view.CHFrame;

public class CHServer {
	public static void main(String[] args) {
		new CHServer().run();
	}

	private CHFrame frame = new CHFrame();
	private CHConnectionPool connectionPool = new CHConnectionPool();
	private List<String> members = new ArrayList<String>();

	public void run() {

		// テキストエリア初期化
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 300, 500);
		frame.setTitle("SyncServer");
		frame.open();

		connectionPool.setFrame(frame);

		try (ServerSocket serverSock = new ServerSocket(10000)) {
			while (true) {
				frame.println("waiting new client..");
				final Socket sock = serverSock.accept();
				frame.println("accepted..");
				Thread th = new Thread() {
					public void run() {
						CHConnection conn = connectionPool.newConnection(sock);
						if (conn != null) {
							frame.println("one connection established.");
							connectionPool.addConnection(conn);
							loopForOneClient(conn);
						}
					}
				};
				th.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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
			frame.println("one connection killed");
		}
	}

	private void typeLogin(CHPacket recivedCHPacket, CHConnection conn) {
		String myName = recivedCHPacket.getMyName();

		CHPacket chPacket = new CHPacket();

		// 名前が被った場合
		if (members.contains(myName)) {
			myName = myName + "*";
			chPacket.setExist(true);
		}

		members.add(myName);
		frame.println("name: " + myName + " add list.");

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
		chPacket.setCommand(CHPacket.RECIVE_FILE);
		connectionPool.broadcast(chPacket, conn);
	}

}
