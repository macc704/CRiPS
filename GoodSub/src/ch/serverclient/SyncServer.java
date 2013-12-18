package ch.serverclient;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import ch.connection.Connection;
import ch.connection.ConnectionPool;
import ch.datas.SendDatas;
import ch.frame.CHFrame;

public class SyncServer {
	public static void main(String[] args) {
		new SyncServer().run();
	}

	private CHFrame frame = new CHFrame();
	private ConnectionPool connectionPool = new ConnectionPool();
	private List<String> members = new ArrayList<String>();

	public void run() {

		// テキストエリア初期か
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
						Connection conn = connectionPool.newConnection(sock);
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

	private void loopForOneClient(Connection conn) {
		try {
			while (conn.established()) {
				Object obj = conn.read();

				if (obj instanceof SendDatas) {
					SendDatas recivedData = (SendDatas) obj;
					int command = recivedData.getCommand();
					switch (command) {
					case SendDatas.LOGIN:
						typeLogin(recivedData);
						break;
					case SendDatas.SOURCE:
						typeSource(recivedData, conn);
						break;
					}
				}

				// if (obj instanceof LoginData) {
				// } else if (obj instanceof SourceData) {
				// connectionPool.broadcast(obj, conn);
				// } else if (obj instanceof FileData) {
				// connectionPool.broadcast(obj, conn);
				// } else if (obj instanceof String) {
				// members.remove(obj);
				// connectionPool.broadcast(members, conn);
				// }
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			connectionPool.close(conn);
			frame.println("one connection killed");
		}
	}

	private void typeLogin(SendDatas recivedData) {
		String myName = recivedData.getMyName();

		if (!members.contains(myName)) {
			members.add(myName);
			frame.println("name: " + myName + " add list.");
		}

		SendDatas sendData = new SendDatas();
		sendData.setMembers(members);
		sendData.setCommand(SendDatas.LOGIN_RESULT);

		connectionPool.broadcastAll(sendData);
	}

	private void typeSource(SendDatas recivedData, Connection conn) {
		SendDatas sendData = new SendDatas();
		sendData.setSource(recivedData.getSource());
		sendData.setCommand(SendDatas.SOURCE_RESULT);
		connectionPool.broadcast(sendData, conn);
	}

}
