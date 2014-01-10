package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHLogoutResponse extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;

	public CHLogoutResponse(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}
