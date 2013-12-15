package src.coco.model;

public class CCCompileError {

	private int errorID;
	private String filePath;
	private long beginTime;
	private long endTime;
	private int correctTime;

	public CCCompileError() {

	}

	public void setData(int errorID, String filePath, long beginTime,
			long endTime, int correctTime) {
		this.errorID = errorID;
		this.filePath = filePath;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.correctTime = correctTime;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public String getProjectSetName() {
		String segments[] = filePath.split("/cash/");
		return segments[1].split("/")[0];
	}

	public String getProjectName() {
		String segments[] = filePath.split("/cash/");
		return segments[1].split("/")[1];
	}

	public String getFilename() {
		String segments[] = filePath.split("/ProjectBase/");
		return segments[1];
	}

	public int getErrorID() {
		return errorID;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getCorrectTime() {
		return correctTime;
	}
}