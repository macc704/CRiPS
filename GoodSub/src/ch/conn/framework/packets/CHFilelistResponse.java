package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;
import clib.common.filesystem.sync.CFileList;

public class CHFilelistResponse extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private CFileList fileList;

	public CHFilelistResponse(String user, CFileList fileList) {
		this.user = user;
		this.fileList = fileList;
	}

	public String getUser() {
		return user;
	}

	public CFileList getFileList() {
		return fileList;
	}
}
