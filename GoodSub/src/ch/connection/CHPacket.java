package ch.connection;

import java.io.File;
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
	public static final int REQUEST_RESULT = 7;// fileget_res
	public static final int FILEGET_RES = 8;
	public static final int FILE_SEND_REQUEST = 9;
	public static final int LOGIN_MEMBER_STATUS = 10;
	public static final int FILELIST_REQ = 11;
	public static final int FILELIST_RES = 12;

	private int command;
	private String myName;
	private String source;
	private List<String> members = new ArrayList<String>();
	private boolean exist = false;
	private String currentFileName;
	private String adressee;
	private File file;
	private byte[] bytes;
	private List<String> fileNames = new ArrayList<String>();

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

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}

}
