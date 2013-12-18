package ch.datas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SendDatas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int LOGIN = 0;
	public static final int SOURCE = 1;
	public static final int LOGIN_RESULT = 2;
	public static final int SOURCE_RESULT = 3;

	private int command;
	private String myName;
	private String source;
	private List<String> members = new ArrayList<String>();

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}

}
