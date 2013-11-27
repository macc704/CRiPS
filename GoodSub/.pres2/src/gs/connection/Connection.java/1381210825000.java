package gs.connection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

	private Socket sock;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private int roomNum;

	public Connection(Socket sock) {
		this.sock = sock;
	}

	public boolean shakehandForClient() {
		try {
			out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject("SYN");
			in = new ObjectInputStream(sock.getInputStream());
			in.readObject();
			out.writeObject("ACK");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean shakehandForServer() {
		try {
			in = new ObjectInputStream(sock.getInputStream());
			in.readObject();
			out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject("ACK/SYN");
			in.readObject();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public void write(Object obj) {
		if (established()) {
			try {
				out.writeObject(obj);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} else {
			throw new RuntimeException();
		}
	}

	public Object read() {
		if (established()) {
			try {
				return in.readObject();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} else {
			throw new RuntimeException();
		}
	}

	public void close() {
		if (established()) {
			try {
				sock.close();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public boolean established() {
		return !sock.isClosed() && in != null && out != null;
	}

	public void setRoomNum(int num) {
		roomNum = num;
	}

	public int getRoomNum() {
		return roomNum;
	}

}