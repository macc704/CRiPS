package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHLogin extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;

	public CHLogin(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}
