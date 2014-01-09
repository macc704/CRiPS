package ch.packets;

import ch.connection.CHPacket;

public class CHSourcesendRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String myName;
	private String source;
	private String currentFileName;

	public CHSourcesendRequest(int command, String myName, String source,
			String currentFileName) {
		super(command);
		this.myName = myName;
		this.source = source;
		this.currentFileName = currentFileName;
	}

	public String getMyName() {
		return myName;
	}

	public String getSource() {
		return source;
	}

	public String getCurrentFileName() {
		return currentFileName;
	}
}
