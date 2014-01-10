package ch.conn.framework.packets;

import java.io.Serializable;

public class CHFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private String path;
	private byte[] bytes;

	public CHFile(String path, byte[] bytes) {
		this.path = path;
		this.bytes = bytes;
	}

	public String getPath() {
		return path;
	}

	public byte[] getBytes() {
		return bytes;
	}

}
