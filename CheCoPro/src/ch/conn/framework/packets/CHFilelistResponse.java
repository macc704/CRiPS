package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;
import clib.common.filesystem.sync.CFileHashList;

public class CHFilelistResponse extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private CFileHashList fileList;

	public CHFilelistResponse(String user, CFileHashList fileList) {
		this.user = user;
		this.fileList = fileList;
	}

	public String getUser() {
		return user;
	}

	public CFileHashList getFileList() {
		return fileList;
	}
}
