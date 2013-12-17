package ch.serverclient;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import ch.connection.Connection;
import ch.connection.ConnectionPool;
import ch.datas.LoginData;
import ch.datas.MemberData;
import ch.datas.SourceData;
import ch.frame.GSFrame;

public class SyncServer {
	public static void main(String[] args) {
		new SyncServer().run();
	}

	private GSFrame frame = new GSFrame();
	private ConnectionPool connectionPool = new ConnectionPool();
	private List<String> members = new ArrayList<String>();
	private List<MemberData> datas = new ArrayList<MemberData>();
	private List<Integer> groups = new ArrayList<Integer>();
	private List<ConnectionPool> connectionPools = new ArrayList<ConnectionPool>();

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
				if (obj instanceof LoginData) {
					LoginData loginData = (LoginData) obj;
					typeLogin(loginData, conn);

				} else if (obj instanceof SourceData) {
					connectionPool.broadcast(obj, conn);
				} else if (obj instanceof File) {
					connectionPool.broadcast(obj, conn);
				} else if (obj instanceof String) {
					connectionPool.broadcast(obj, conn);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			connectionPool.close(conn);
			frame.println("one connection killed");
		}
	}

	private void typeLogin(LoginData loginData, Connection conn) {
		String myName = loginData.getMyName();
		int groupNumber = loginData.getGroupNumber();

		if (!members.contains(myName)) {
			members.add(myName);
			MemberData data = new MemberData();
			datas.add(data);
			frame.println("name: " + myName + " add list.");
		}

		ConnectionPool myConnectionPool = null;
		if (!groups.contains(groupNumber)) {
			groups.add(groupNumber);
			myConnectionPool = new ConnectionPool();
			myConnectionPool.addConnection(conn);
			connectionPools.add(myConnectionPool);
			frame.println("group: " + groupNumber + " add list.");
		} else {
			int i = 0;
			for (int aGroup : groups) {
				if (aGroup == groupNumber) {
					myConnectionPool = connectionPools.get(i);
					myConnectionPool.addConnection(conn);
				}
				i++;
			}
		}

		frame.println(myName + " join the group No." + groupNumber);

		if (myConnectionPool != null) {
			myConnectionPool.setFrame(frame);
			myConnectionPool.broadcastAll(members);
		}
	}

}
