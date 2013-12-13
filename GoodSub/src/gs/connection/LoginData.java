package gs.connection;

import java.io.Serializable;

public class LoginData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String myName;
	private int groupNum;

	public void setRoomNum(int roomNum) {
		this.groupNum = roomNum;
	}

	public void setMyName(String userName) {
		this.myName = userName;
	}

	public int getRoomNum() {
		return groupNum;
	}

	public String getMyName() {
		return myName;
	}
}
