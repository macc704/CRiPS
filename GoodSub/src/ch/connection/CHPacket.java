package ch.connection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CHPacket implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int LOGIN = 0;
	public static final int SOURCE = 1;
	public static final int LOGIN_RESULT = 2;
	public static final int RECIVE_SOURCE = 3;
	public static final int LOGUOT = 4;
	public static final int LOGOUT_RESULT = 5;
	public static final int FILE = 6;
	public static final int RECIVE_FILE = 7;
	public static final int SAVE_FILE = 8;
	public static final int FILE_SEND_REQUEST = 9;

	private int command;
	private String myName;
	private String source;
	private List<String> members = new ArrayList<String>();
	private boolean exist = false;
	private List<String> fileNames = new ArrayList<String>();
	private List<byte[]> bytes = new ArrayList<byte[]>();
	private String currentFileName;
	private String adressee;

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

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}

	public List<byte[]> getBytes() {
		return bytes;
	}

	public void setBytes(List<byte[]> bytes) {
		this.bytes = bytes;
	}

	public String getCurrentFileName() {
		return currentFileName;
	}

	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}

	public String getAdressee() {
		return adressee;
	}

	public void setAdressee(String adressee) {
		this.adressee = adressee;
	}

}
