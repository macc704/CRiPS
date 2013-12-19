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
					CHPacket recivedData = (CHPacket) obj;
					int command = recivedData.getCommand();
					switch (command) {
					case CHPacket.LOGIN:
						typeLogin(recivedData, conn);
						break;
					case CHPacket.SOURCE:
						typeSource(recivedData, conn);
						break;
					case CHPacket.LOGUOT:
						typeLogout(recivedData, conn);
						break;
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

	private void typeLogin(CHPacket recivedData, CHConnection conn) {
		String myName = recivedData.getMyName();

		CHPacket sendData = new CHPacket();

		// 名前が被った場合
		if (members.contains(myName)) {
			myName = myName + "*";
			sendData.setExist(true);
		}

		members.add(myName);
		frame.println("name: " + myName + " add list.");

		sendData.setMyName(myName);
		sendData.setMembers(members);
		sendData.setCommand(CHPacket.LOGIN_RESULT);

		if (sendData.isExist()) {
			connectionPool.sendMyself(sendData, conn);
			sendData.setExist(false);
			connectionPool.broadcast(sendData, conn);
		} else {
			connectionPool.broadcastAll(sendData);
		}
	}

	private void typeSource(CHPacket recivedData, CHConnection conn) {
		CHPacket sendData = new CHPacket();
		sendData.setMyName(recivedData.getMyName());
		sendData.setSource(recivedData.getSource());
		sendData.setCommand(CHPacket.RECIVE_SOURCE);
		connectionPool.broadcast(sendData, conn);
	}

	private void typeLogout(CHPacket recivedData, CHConnection conn) {
		members.remove(recivedData.getMyName());

		CHPacket sendData = new CHPacket();
		sendData.setMyName(recivedData.getMyName());
		sendData.setCommand(CHPacket.LOGOUT_RESULT);
		connectionPool.broadcast(sendData, conn);
	}

}
