package ch.datas;

import java.io.Serializable;

public class SourceData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String myName;
	private String source;
	private String currentFileName;

	public String getCurrentFileName() {
		return currentFileName;
	}

	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

}
