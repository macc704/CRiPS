package gs.serverclient;

import gs.connection.Connection;
import gs.connection.ConnectionPool;
import gs.connection.DivideRoom;
import gs.connection.SendObject;
import gs.frame.GSFrame;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class SyncServer {
	public static void main(String[] args) {
		new SyncServer().run();
	}

	private GSFrame frame = new GSFrame();
	private ConnectionPool connectionPool = new ConnectionPool();
	private List<ConnectionPool> pools = new ArrayList<ConnectionPool>();
	private DivideRoom divideRoom = new DivideRoom();
	private List<String> sendList = new ArrayList<String>();
	private List<String> users = new ArrayList<String>();

	public void run() {

		// テキストエリア初期か
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 300, 500);
		frame.setTitle("SyncServer");
		frame.open();

		sendList.add("");
		sendList.add("");

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
			Object obj;
			int roomNum = 0;
			while (conn.established()) {
				obj = conn.read();
				if (obj instanceof /* Integer */SendObject) { // Integerなら部屋番号かスクロールバーのvalue

					String user = ((SendObject) obj).getUserName();
					users.add(user);
					frame.println(user + " add list.");
					connectionPool.broadcastAll(users, conn);

					// roomNum = (int) obj;
					// roomNum = ((SendObject) obj).getRoomNum();
					// 新しい部屋ならConnectionPoolを新規作成
					// if (!divideRoom.checkRoomNum(roomNum)) {
					// pools.add(new ConnectionPool());
					// frame.println("create room No." + roomNum);
					// }

					// if (users.indexOf(((SendObject) obj).getUserName()) ==
					// -1) {

					// pools.get(divideRoom.countRoom(roomNum))
					// .addConnection(conn);
					// pools.get(divideRoom.countRoom(roomNum)).addUser(
					// ((SendObject) obj).getUserName());
					// frame.println("'" + ((SendObject) obj).getUserName() +
					// "'"
					// + " join room No." + roomNum);
					// users =
					// pools.get(divideRoom.countRoom(roomNum)).getUsers();
					// pools.get(divideRoom.countRoom(roomNum)).broadcastAll(
					// users, conn);
					// }
					// pools.get(divideRoom.countRoom(roomNum)).addUser(
					// ((SendObject) obj).getUserName());
					// showUsers(roomNum);

					// String text = ((SendObject) obj).getSource();
					// frame.println(((SendObject) obj).getSource());
					// frame.println(((SendObject) obj).getSource());
					// if (((SendObject) obj).getSource() != null) {
					// pools.get(divideRoom.countRoom(roomNum)).broadcast(obj,
					// conn);
					// }

				} else if (obj instanceof String) { // Stringなら送りたい文字列
					String text = (String) obj;
					pools.get(divideRoom.countRoom(roomNum)).broadcast(text,
							conn);
				} else if (obj instanceof File) {
					File file = (File) obj;
					pools.get(divideRoom.countRoom(roomNum)).broadcast(file,
							conn);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			connectionPool.close(conn);
			frame.println("one connection killed");
		}
	}
}
