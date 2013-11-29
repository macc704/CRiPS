package gs.connection;

import java.io.Serializable;

public class SendObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private String source;
	private String userName;
	private int roomNum;

	// public void setSource(String source) {
	// System.out.println("set : " + source);
	// this.source = source;
	// }

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	// public String getSource() {
	// // System.out.println("get : " + source);
	// return source;
	// }

	public int getRoomNum() {
		return roomNum;
	}

	public String getUserName() {
		return userName;
	}
}
