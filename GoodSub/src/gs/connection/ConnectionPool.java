package gs.connection;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {

	private List<Connection> connections = new ArrayList<Connection>();
	private Object lock = new Object();
	private List<String> users = new ArrayList<String>();

	public Connection newConnection(Socket sock) {
		synchronized (lock) {
			Connection conn = new Connection(sock);
			boolean success = conn.shakehandForServer();
			if (!success) {
				return null;
			}
			// connections.add(conn);
			return conn;
		}
	}

	public void close(Connection conn) {
		synchronized (lock) {
			conn.close();
			connections.remove(conn);
		}
	}

	public void broadcast(Object obj, Connection sender) {
		synchronized (lock) {
			for (Connection aClient : connections) {
				if (aClient != sender
				/* && aClient.getRoomNum() == sender.getRoomNum() */) {
					aClient.write(obj);
				}
			}
		}
	}

	public void broadcastAll(Object obj, Connection sender) {
		synchronized (lock) {
			for (Connection aClient : connections) {
				aClient.write(obj);
			}
		}
	}

	public void addConnection(Connection conn) {
		connections.add(conn);
	}

	public void addUser(String userName) {
		for (String aUser : users) {
			if (aUser == userName) {
				// ���[�U�l�[�������ɑ��݂���G���[����
			}
		}
		users.add(userName);
	}

	public List<String> getUsers() {
		return users;
	}

}