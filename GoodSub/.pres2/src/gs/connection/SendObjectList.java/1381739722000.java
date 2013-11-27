package gs.connection;

import java.io.Serializable;

public class SendObjectList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String source;
	private String console;
	private int scrollVal;
	private int roomNum;

	public void setSource(String source) {
		this.source = source;
	}

	public void setConsole(String console) {
		this.console = console;
	}

	public void setScrollVal(int scrollVal) {
		this.scrollVal = scrollVal;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public String getSource() {
		return source;
	}

	public String getConsole() {
		return console;
	}

	public int getScrollVal() {
		return scrollVal;
	}

	public int getRoomNum() {
		return roomNum;
	}
}
