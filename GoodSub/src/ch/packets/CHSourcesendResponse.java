package ch.packets;

import ch.connection.CHPacket;

public class CHSourcesendResponse extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private String source;
	private String currentFileName;

	public CHSourcesendResponse(String user, String source,
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
