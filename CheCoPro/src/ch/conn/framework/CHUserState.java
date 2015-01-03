package ch.conn.framework;

import java.awt.Color;
import java.util.Date;

public class CHUserState extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private boolean login;
	private Color color;
	private Date lastLogin;
	private String currentFileName;

	public CHUserState(String user, boolean login, Color color) {
		this.user = user;
		this.login = login;
		this.color = color;
	}

	public String getUser() {
		return user;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public Color getColor() {
		return color;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getCurrentFileName() {
		return currentFileName;
	}

	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}

}
