package ch.packets;

import java.io.File;

import ch.connection.CHPacket;

public class CHFilegetResponse extends CHPacket {

	private static final long serialVersionUID = 1L;

	private File file;
	private byte[] bytes;

	public CHFilegetResponse(int command, File file, byte[] bytes) {
		super(command);
		this.file = file;
		this.bytes = bytes;
	}

	public File getFile() {
		return file;
	}

	public byte[] getBytes() {
		return bytes;
	}
}
