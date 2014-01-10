package ch.conn.framework;

public class CHUserState extends CHPacket {

	private static final long serialVersionUID = 1L;

	private String user;
	private boolean login;

	public CHUserState(String user, boolean login) {
		this.user = user;
		this.login = login;
	}

	public String getUser() {
		return user;
	}

	public boolean isLogin() {
		return login;
	}

}
