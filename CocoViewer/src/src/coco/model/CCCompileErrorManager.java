package src.coco.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import clib.common.filesystem.CDirectory;

public class CCCompileErrorManager {

	private LinkedHashMap<Integer, CCCompileErrorKind> kinds = new LinkedHashMap<Integer, CCCompileErrorKind>();
	private LinkedHashMap<String, Integer> ids = new LinkedHashMap<String, Integer>();

	private int totalErrorCount = 0;
	private long totalErrorCorrectionTime = 0;
	private int totalWorkingTime = 0;

	// ソースコード参照用
	private CDirectory base = null;
	private CDirectory libDir = null;

	public CCCompileErrorManager() {

	}

	public void setBase(CDirectory base) {
		this.base = base;
	}

	public void setLibDir(CDirectory libDir) {
		this.libDir = libDir;
	}

	public CDirectory getBase() {
		return base;
	}

	public CDirectory getLibDir() {
		return libDir;
	}

	public void put(int id, int rare, String message) {
		CCCompileErrorKind list = new CCCompileErrorKind(rare, message);
		kinds.put(id, list);
		ids.put(message, id);
	}

	public CCCompileErrorKind getKind(int id) {
		if (!kinds.containsKey(id)) {
			put(id, 6, "dummy");
		}
		return kinds.get(id);
	}

	public List<CCCompileErrorKind> getAllKinds() {
		return new ArrayList<CCCompileErrorKind>(kinds.values());
	}

	public int getMessagesID(String message) {
		return ids.get(message);
	}

	// public void totalErrorCountUp() {
	// totalErrorCount++;
	// }

	public int getTotalErrorCount() {
		return totalErrorCount;
	}

	public long getTotalErrorCorrectionTime() {
		return totalErrorCorrectionTime;
	}

	public void addError(CCCompileError error) {
		getKind(error.getErrorID()).addError(error);
		totalErrorCorrectionTime += error.getCorrectionTime();
		totalErrorCount++;
	}

	public void addTotalWorkingTime(int workingTime) {
		totalWorkingTime += workingTime;
	}

	public int getTotalWorkingTime() {
		return totalWorkingTime;
	}

	public double getCompileErrorCorrectionTimeRate() {
		double workingtime = (double) totalErrorCorrectionTime
				/ (totalWorkingTime * 60);
		workingtime += 0.005;
		int tmp = (int) (workingtime * 1000);
		workingtime = (double) tmp / 10;
		return workingtime;
	}
}