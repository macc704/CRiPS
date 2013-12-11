package gs.connection;

import java.io.Serializable;

public class MemberData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String member;
	private Connection conn;

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

}
