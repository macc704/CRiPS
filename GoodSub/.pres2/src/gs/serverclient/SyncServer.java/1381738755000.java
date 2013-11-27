package gs.serverclient;

import gs.connection.Connection;
import gs.connection.ConnectionPool;
import gs.connection.DivideRoom;
import gs.connection.SendObjectList;
import gs.frame.GSFrame;

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

	public void run() {

		// テキストエリア初期か
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 300, 500);
		frame.setTitle("SyncServer");
		frame.open();

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
			SendObjectList sendList;
			int roomNum = 0;
			while (conn.established()) {
				obj = conn.read();
				sendList = (SendObjectList) obj;
				if (obj instanceof Integer) { // Integerなら部屋番号かスクロールバーのvalue
					if ((int) obj < 0) { // マイナスなら部屋番号
						roomNum = (int) obj;
						// 新しい部屋ならConnectionPoolを新規作成
						if (!divideRoom.checkRoomNum(roomNum * (-1))) {
							pools.add(new ConnectionPool());
						}
						pools.get(divideRoom.countRoom(roomNum * (-1)))
								.addConnection(conn);
						frame.println("room number is [" + roomNum * (-1) + "]");
					} else if ((int) obj >= 0) { // 0以上ならスクロールバーのvalue
						int value = (int) obj;
						pools.get(divideRoom.countRoom(roomNum * (-1)))
								.broadcast(value, conn);
					}
				} else if (obj instanceof String) { // Stringなら送りたい文字列
					String text = (String) obj;
					pools.get(divideRoom.countRoom(roomNum * (-1))).broadcast(
							text, conn);
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
