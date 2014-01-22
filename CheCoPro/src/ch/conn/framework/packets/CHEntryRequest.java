package ch.conn.framework.packets;

import ch.conn.framework.CHPacket;

public class CHEntryRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private String password;

	public CHEntryRequest(String user, String password) {
		this.user = user;
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
}
