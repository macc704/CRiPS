package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHLogout extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;

	public CHLogout(String user) {
		this.user = user;
	}

	public String getMyName() {
		return user;
	}
}
