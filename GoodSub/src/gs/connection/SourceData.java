package gs.connection;

import java.io.Serializable;

public class SourceData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String myName;
	private String source;

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
