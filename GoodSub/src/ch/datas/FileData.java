package ch.datas;

import java.io.File;
import java.io.Serializable;

public class FileData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	File file;
	String myName;
	String requestName;

	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
