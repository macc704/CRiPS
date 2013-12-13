package gs.serverclient;

import gs.connection.Connection;
import gs.connection.ConnectionPool;
import gs.connection.LoginData;
import gs.connection.MemberData;
import gs.connection.SourceData;
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
	private List<String> members = new ArrayList<String>();
	private List<MemberData> datas = new ArrayList<MemberData>();

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

	public void loopForOneClient(Connection conn) {
		try {
			while (conn.established()) {
				Object obj = conn.read();
				if (obj instanceof LoginData) {
					LoginData loginData = (LoginData) obj;
					typeLogin(loginData);

				} else if (obj instanceof String) { // Stringなら送りたい文字列
					String text = (String) obj;
					connectionPool.broadcastAll(text);
				} else if (obj instanceof SourceData) {
					System.out.println("name : "
							+ ((SourceData) obj).getMyName());
					System.out.println(((SourceData) obj).getSource());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			connectionPool.close(conn);
			frame.println("one connection killed");
		}
	}

	public void typeLogin(LoginData loginData) {
		String myName = loginData.getMyName();
		if (!members.contains(myName)) {
			members.add(myName);
			MemberData data = new MemberData();
			datas.add(data);
			frame.println("name: " + myName + " add list.");
		}
		connectionPool.broadcastAll(members);
	}

}
