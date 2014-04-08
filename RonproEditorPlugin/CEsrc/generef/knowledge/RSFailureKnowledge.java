package generef.knowledge;

import java.io.File;

import util.StringUtil;
import clib.common.compiler.CDiagnostic;

public class RSFailureKnowledge {

	private CDiagnostic error;

	private long writingPointTime;

	private long openWindowTimeMillis;
	private long closeWindowTimeMillis;

	private String cause;
	private String handle;

	private String unFixedFilePath;
	private String fixedFilePath;

	private int threshold;

	public RSFailureKnowledge(CDiagnostic error, String cause, String handle,
			String unFixedFilePath, String fixedFilePath,
			long openWindowTimeMillis, long closeWindowTimeMillis,
			long writingPointTime, int threshold) {
		this.error = error;
		this.cause = cause;
		this.handle = handle;
		this.unFixedFilePath = unFixedFilePath;
		this.fixedFilePath = fixedFilePath;
		this.openWindowTimeMillis = openWindowTimeMillis;
		this.closeWindowTimeMillis = closeWindowTimeMillis;
		this.writingPointTime = writingPointTime;
		this.threshold = threshold;
	}

	public RSFailureKnowledge(CDiagnostic error, String unFixedFilePath,
			String fixedFilePath, int threshold) {
		this(error, "", "", unFixedFilePath, fixedFilePath, 0, 0, 0, threshold);
	}

	/***************************************************************************
	 * getter
	 **************************************************************************/

	public long getWindowOpenTime() {
		return openWindowTimeMillis;
	}

	public long getWindowCloseTime() {
		return closeWindowTimeMillis;
	}

	public long getWritingTime() {
		// ãLò^ïsó«ÇÃèÍçá
		if (closeWindowTimeMillis == 0 || openWindowTimeMillis == 0) {
			return 0;
		}
		return closeWindowTimeMillis - openWindowTimeMillis;
	}

	public CDiagnostic getCompileError() {
		return error;
	}

	public String getCause() {
		return cause;
	}

	public String getHandle() {
		return handle;
	}

	public String getFixedFilePath() {
		return fixedFilePath;
	}

	public String getUnFixedFilePath() {
		return unFixedFilePath;
	}

	@Deprecated
	public File getUnFixedFile() {
		return new File(unFixedFilePath);
	}

	@Deprecated
	public File getFixedFile() {
		return new File(fixedFilePath);
	}

	public long getWritingPointTime() {
		return writingPointTime;
	}

	public int getThreshold() {
		return threshold;
	}

	/***************************************************************************
	 * setter
	 **************************************************************************/

	public void setCause(String cause) {
		this.cause = cause;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public void setWindowOpenTime(long millis) {
		this.openWindowTimeMillis = millis;
	}

	public void setWindowCloseTime(long millis) {
		this.closeWindowTimeMillis = millis;
	}

	public void setWritingPointTime(long time) {
		this.writingPointTime = time;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public boolean isEmpty() {
		if (StringUtil.isEmpty(cause) || StringUtil.isEmpty(handle)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return error.toString();
	}

}
