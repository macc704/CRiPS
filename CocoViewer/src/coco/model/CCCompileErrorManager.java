package coco.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ppv.app.datamanager.PPProjectSet;
import pres.core.IPRRecordingProject;
import pres.core.model.PRLog;
import pres.loader.logmodel.PRCocoViewerLog;
import pres.loader.logmodel.PRCocoViewerLog.SubType;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;

public class CCCompileErrorManager {
	// HashMapでは順序が保証されないのでLinkedHashMapに変更
	private LinkedHashMap<Integer, CCCompileErrorKind> kinds = new LinkedHashMap<Integer, CCCompileErrorKind>();
	private LinkedHashMap<String, Integer> ids = new LinkedHashMap<String, Integer>();

	private int totalErrorCount = 0;
	private int totalErrorCorrectionTime = 0;
	private int totalWorkingTime = 0;

	private CDirectory baseDir;
	private CDirectory libDir;
	private PPProjectSet ppProjectSet;
	private CPath projectPath;
	private IPRRecordingProject recodingproject;

	public CCCompileErrorManager() {

	}

	public void put(int id, int rare, String message) {
		CCCompileErrorKind kind = new CCCompileErrorKind(rare, message);
		kinds.put(id, kind);
		ids.put(message, id);
	}

	public CCCompileErrorKind getKind(int id) {
		if (!kinds.containsKey(id)) {
			put(id, 6, "dummy");
		}
		return kinds.get(id);
	}

	// public void totalErrorCountUp() {
	// totalErrorCount++;
	// }

	public int getTotalErrorCount() {
		return totalErrorCount;
	}

	public List<CCCompileErrorKind> getAllKinds() {
		return new ArrayList<CCCompileErrorKind>(kinds.values());
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
		getKind(error.getErrorID()).addError(error);
		totalErrorCorrectionTime += error.getCorrectionTime();
		totalErrorCount++;
	}

	public double getCompileErrorCorrectionTimeRate() {
		if (totalWorkingTime == 0) {
			return 0;
		}

		double rate = (double) totalErrorCorrectionTime
				/ (totalWorkingTime * 60);
		rate += 0.0005;
		int tmp = (int) (rate * 1000);
		rate = (double) tmp / 10;
		return rate;
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

	public void setProjectPath(CPath projectPath) {
		this.projectPath = projectPath;
	}

	public CPath getProjectPath() {
		return projectPath;
	}

	public void setRecordingProject(IPRRecordingProject recodingproject) {
		this.recodingproject = recodingproject;
	}

	public IPRRecordingProject getRecodingproject() {
		return recodingproject;
	}

	public void writePresLog(SubType cocoviewerSubtype, Object... texts) {
		if (recodingproject != null) {
			PRLog log = new PRCocoViewerLog(cocoviewerSubtype,
					getProjectPath(), texts);
			recodingproject.record(log);
		}
	}
}