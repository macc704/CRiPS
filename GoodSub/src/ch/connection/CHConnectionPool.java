package ch.connection;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ch.view.CHFrame;

public class CHConnectionPool {

	private List<CHConnection> connections = new ArrayList<CHConnection>();
	private Object lock = new Object();
	private CHFrame frame;

	public void setFrame(CHFrame frame) {
		this.frame = frame;
	}

	public CHConnection newConnection(Socket sock) {
		synchronized (lock) {
			CHConnection conn = new CHConnection(sock);
			boolean success = conn.shakehandForServer();
			if (!success) {
				return null;
			}
			// connections.add(conn);
			return conn;
		}
	}

	public void close(CHConnection conn) {
		synchronized (lock) {
			conn.close();
			connections.remove(conn);
		}
	}

	public void broadcast(Object obj, CHConnection sender) {
		synchronized (lock) {
			for (CHConnection aClient : connections) {
				if (aClient != sender) {
					aClient.write(obj);
					frame.println("send : " + obj + "  to : " + aClient);
				}
			}
		}
	}

	public void broadcastAll(Object obj) {
		synchronized (lock) {
			for (CHConnection aClient : connections) {
				aClient.write(obj);
				frame.println("send : " + obj + "  to : " + aClient);
			}
		}
	}

	public void sendMyself(Object obj, CHConnection myself) {
		synchronized (lock) {
			for (CHConnection aClient : connections) {
				if (aClient == myself) {
					aClient.write(obj);
					frame.println("send : " + obj + "  to : " + aClient);
				}
			}
		}
	}

	public void addConnection(CHConnection conn) {
		connections.add(conn);
	}

}