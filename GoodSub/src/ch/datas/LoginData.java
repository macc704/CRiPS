package ch.datas;

import java.io.Serializable;

public class LoginData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String myName;

	public void setMyName(String userName) {
		this.myName = userName;
	}

	public String getMyName() {
		return myName;
	}
}
