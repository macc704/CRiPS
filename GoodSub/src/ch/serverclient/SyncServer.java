package ch.serverclient;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import ch.connection.Connection;
import ch.connection.ConnectionPool;
import ch.datas.CommandAndDatas;
import ch.frame.CHFrame;

public class SyncServer {
	public static void main(String[] args) {
		new SyncServer().run();
	}

	private CHFrame frame = new CHFrame();
	private ConnectionPool connectionPool = new ConnectionPool();
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

				if (obj instanceof CommandAndDatas) {
					CommandAndDatas recivedData = (CommandAndDatas) obj;
					int command = recivedData.getCommand();
					switch (command) {
					case CommandAndDatas.LOGIN:
						typeLogin(recivedData, conn);
						break;
					case CommandAndDatas.SOURCE:
						typeSource(recivedData, conn);
						break;
					case CommandAndDatas.LOGUOT:
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

	private void typeLogin(CommandAndDatas recivedData, Connection conn) {
		String myName = recivedData.getMyName();

		CommandAndDatas sendData = new CommandAndDatas();

		// 名前が被った場合
		if (members.contains(myName)) {
			myName = myName + "*";
			sendData.setExist(true);
		}

		members.add(myName);
		frame.println("name: " + myName + " add list.");

		sendData.setMyName(myName);
		sendData.setMembers(members);
		sendData.setCommand(CommandAndDatas.LOGIN_RESULT);

		if (sendData.isExist()) {
			connectionPool.sendMyself(sendData, conn);
			sendData.setExist(false);
			connectionPool.broadcast(sendData, conn);
		} else {
			connectionPool.broadcastAll(sendData);
		}
	}

	private void typeSource(CommandAndDatas recivedData, Connection conn) {
		CommandAndDatas sendData = new CommandAndDatas();
		sendData.setMyName(recivedData.getMyName());
		sendData.setSource(recivedData.getSource());
		sendData.setCommand(CommandAndDatas.RECIVE_SOURCE);
		connectionPool.broadcast(sendData, conn);
	}

	private void typeLogout(CommandAndDatas recivedData, Connection conn) {
		members.remove(recivedData.getMyName());

		CommandAndDatas sendData = new CommandAndDatas();
		sendData.setMyName(recivedData.getMyName());
		sendData.setCommand(CommandAndDatas.LOGOUT_RESULT);
		connectionPool.broadcast(sendData, conn);
	}

}
