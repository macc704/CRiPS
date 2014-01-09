package ch.packets;

import java.io.File;

import ch.connection.CHPacket;

public class CHFilegetResponse extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private File file;
	private byte[] bytes;

	public CHFilegetResponse(String user, File file, byte[] bytes) {
		this.user = user;
		this.file = file;
		this.bytes = bytes;
	}

	public String getUser() {
		return user;
	}

	public File getFile() {
		return file;
	}

	public byte[] getBytes() {
		return bytes;
	}
}
