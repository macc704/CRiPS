package src.coco.model;

public class CCCompileError {

	private int errorID;
	private String projectname;
	private String filename;
	private long beginTime;
	private long endTime;

	public CCCompileError() {

	}

	public void setData(int errorID, String projectName, String filename,
			long beginTime, long endTime) {
		this.errorID = errorID;
		this.projectname = projectName;
		this.filename = filename;
		this.beginTime = beginTime;
		this.endTime = endTime;
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

	public long getEndTime() {
		return endTime;
	}

	public long getCorrectTime() {
		return (int) ((endTime - beginTime) / 1000);
	}
}