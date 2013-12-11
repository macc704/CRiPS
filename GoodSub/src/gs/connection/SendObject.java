package gs.connection;

import java.io.Serializable;

public class SendObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String myName;
	private int roomNum;
	private String selectedMember;

	public String getSelectedMember() {
		return selectedMember;
	}

	public void setSelectedMember(String selectedMember) {
		this.selectedMember = selectedMember;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public void setMyName(String userName) {
		this.myName = userName;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public String getMyName() {
		return myName;
	}
}
