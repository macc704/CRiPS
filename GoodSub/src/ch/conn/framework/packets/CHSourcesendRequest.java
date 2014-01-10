package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHSourcesendRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private String source;
	private String currentFileName;

	public CHSourcesendRequest(String user, String source,
			String currentFileName) {
		this.user = user;
		this.source = source;
		this.currentFileName = currentFileName;
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
}
