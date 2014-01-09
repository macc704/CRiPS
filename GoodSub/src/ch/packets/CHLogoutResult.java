package ch.packets;

import ch.connection.CHPacket;

public class CHLogoutResult extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String myName;

	public CHLogoutResult(int command, String myName) {
		super(command);
		this.myName = myName;
	}

	public String getMyName() {
		return myName;
	}
}
