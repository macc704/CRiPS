package gs.connection;

import java.io.Serializable;

public class LoginData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String myName;
	private int groupNumber;

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public void setMyName(String userName) {
		this.myName = userName;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public String getMyName() {
		return myName;
	}
}
