package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHFilelistRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;

	public CHFilelistRequest(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}
