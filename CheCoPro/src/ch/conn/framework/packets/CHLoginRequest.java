package ch.conn.framework.packets;

import java.awt.Color;

import ch.conn.framework.CHPacket;

public class CHLoginRequest extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private String password;
	private Color color;

	public CHLoginRequest(String user, String password, Color color) {
		this.user = user;
		this.password = password;
		this.color = color;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public Color getColor() {
		return color;
	}
}
