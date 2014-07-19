package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHSourceChanged extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private String source;
	private String currentFileName;
	private int topPixel;

	public CHSourceChanged(String user, String source, String currentFileName,
			int topPixel) {
		this.user = user;
		this.source = source;
		this.currentFileName = currentFileName;
		this.topPixel = topPixel;
	}

	public String getUser() {
		return user;
	}

	public String getSource() {
		return source;
	}

	public String getCurrentFileName() {
		return currentFileName;
	}

	public int getTopPixel() {
		return topPixel;
	}
}
