package src.coco.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ppv.app.datamanager.PPProjectSet;
import clib.common.filesystem.CDirectory;

public class CCCompileErrorManager {
	// HashMapÇ≈ÇÕèáèòÇ™ï€èÿÇ≥ÇÍÇ»Ç¢ÇÃÇ≈LinkedHashMapÇ…ïœçX
	private LinkedHashMap<Integer, CCCompileErrorList> lists = new LinkedHashMap<Integer, CCCompileErrorList>();
	private LinkedHashMap<String, Integer> ids = new LinkedHashMap<String, Integer>();

	private int totalErrorCount = 0;
	private int totalErrorCorrectionTime = 0;
	private int totalWorkingTime = 0;

	private CDirectory baseDir;
	private CDirectory libDir;
	private PPProjectSet ppProjectSet;

	public CCCompileErrorManager() {

	}

	public void put(int id, int rare, String message) {
		CCCompileErrorList list = new CCCompileErrorList(rare, message);
		lists.put(id, list);
		ids.put(message, id);
	}

	public CCCompileErrorList getList(int id) {
		if (!lists.containsKey(id)) {
			put(id, 6, "dummy");
		}
		return lists.get(id);
	}

	// public void totalErrorCountUp() {
	// totalErrorCount++;
	// }

	public int getTotalErrorCount() {
		return totalErrorCount;
	}

	public List<CCCompileErrorList> getAllLists() {
		return new ArrayList<CCCompileErrorList>(lists.values());
	}

	public int getMessagesID(String message) {
		return ids.get(message);
	}

	public void setBaseDir(CDirectory baseDir) {
		this.baseDir = baseDir;
	}

	public CDirectory getBaseDir() {
		return baseDir;
	}

	public void setLibDir(CDirectory libDir) {
		this.libDir = libDir;
	}

	public CDirectory getLibDir() {
		return libDir;
	}

	public void setPPProjectSet(PPProjectSet ppProjectSet) {
		this.ppProjectSet = ppProjectSet;
	}

	public PPProjectSet getPPProjectSet() {
		return ppProjectSet;
	}

	public void addError(CCCompileError error) {
		getList(error.getErrorID()).addError(error);
		totalErrorCorrectionTime += error.getCorrectionTime();
		totalErrorCount++;
	}

	public double getCompileErrorCorrectionTimeRate() {
		double workingtime = (double) totalErrorCorrectionTime
				/ (totalWorkingTime * 60);
		workingtime += 0.005;
		int tmp = (int) (workingtime * 1000);
		workingtime = (double) tmp / 10;
		return workingtime;
	}

	public void addTotalWorkingTime(int workingTime) {
		totalWorkingTime += workingTime;
	}

	public int getTotalWorkingTime() {
		return totalWorkingTime;
	}

	public int getErrorTotalCorrectionTime() {
		return totalErrorCorrectionTime;
	}

	public int getTotalErrorCorrectionTime() {
		return totalErrorCorrectionTime;
	}
}