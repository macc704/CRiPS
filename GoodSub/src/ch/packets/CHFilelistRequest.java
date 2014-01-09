package ch.packets;

import ch.connection.CHPacket;

public class CHFilelistRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;

	public CHFilelistRequest(String user) {
		this.user = user;
	}

	public String getMember() {
		return user;
	}
}
