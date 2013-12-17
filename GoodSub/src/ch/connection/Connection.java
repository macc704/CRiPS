package ch.connection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Connection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Socket sock;
	private ObjectInputStream in;
	private ObjectOutputStream out;

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
				out.reset();
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
				Object obj = in.readObject();
				return obj;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} else {
			throw new RuntimeException("read() but not established");
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

}