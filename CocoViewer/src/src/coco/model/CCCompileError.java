package src.coco.model;

public class CCCompileError {

	private int errorID;
	private String projectname;
	private String filename;
	private long beginTime;
	private int correctTime;

	public CCCompileError() {

	}

	public void setData(int errorID, String projectName, String filename,
			long beginTime, int correctTime) {
		this.errorID = errorID;
		this.projectname = projectName;
		this.filename = filename;
		this.beginTime = beginTime;
		this.correctTime = correctTime;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public String getProjectname() {
		return projectname;
	}

	public String getFilename() {
		return filename;
	}

	public int getErrorID() {
		return errorID;
	}

	public int getCorrectTime() {
		return correctTime;
	}
}