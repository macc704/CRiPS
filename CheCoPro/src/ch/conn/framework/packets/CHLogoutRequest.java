package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHLogoutRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;

	public CHLogoutRequest(String user) {
		this.user = user;
	}

	public String getMyName() {
		return user;
	}
}
