package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHFilesizeNotice extends CHPacket {

	private static final long serialVersionUID = 1L;

	private int fileSize;

	public CHFilesizeNotice(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getFileSize() {
		return fileSize;
	}
}
