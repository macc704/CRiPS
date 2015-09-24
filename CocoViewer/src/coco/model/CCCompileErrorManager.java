package coco.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import ppv.app.datamanager.IPPVLoader;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import ppv.app.datamanager.PPRonproPPVLoader;
import pres.core.IPRRecordingProject;
import pres.core.model.PRLog;
import pres.loader.logmodel.PRCocoViewerLog;
import pres.loader.logmodel.PRCocoViewerLog.SubType;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;

public class CCCompileErrorManager {
	/************************
	 * Compile Errors Data
	 ************************/
	// 順序保証のためLinkedHashMapを使用すること
	private LinkedHashMap<Integer, CCCompileErrorKind> kinds = new LinkedHashMap<Integer, CCCompileErrorKind>();
	private LinkedHashMap<String, Integer> ids = new LinkedHashMap<String, Integer>();

	/************************
	 * GUI Shows Data
	 ************************/
	private int totalErrorCount = 0;
	private int totalErrorCorrectionTime = 0;
	private int totalWorkingTime = 0;

	/************************
	 * For Source Code Window
	 ************************/
	private CDirectory baseDir;
	private CDirectory libDir;
	private PPProjectSet ppProjectSet;
	private CPath projectPath;
	private IPRRecordingProject recodingproject;

	/************************
	 * For Create Coco Data
	 ************************/
	private CCPathData pathdata;
	
	List<CDirectory> projects;
	private IPPVLoader ppvLoader = new PPRonproPPVLoader(); // TODO: Eclipse版への対応	
	private PPDataManager ppDataManager;
	
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
	
	public void writePresLog(SubType cocoviewerSubtype, Object... texts) {
		if (recodingproject != null) {
			PRLog log = new PRCocoViewerLog(cocoviewerSubtype,
					getProjectPath(), texts);
			recodingproject.record(log);
		}
	}
	
	public void addError(CCCompileError error) {
		getKind(error.getErrorID()).addError(error);
		totalErrorCorrectionTime += error.getCorrectionTime();
		totalErrorCount++;
	}
	
	public void addTotalWorkingTime(int workingTime) {
		totalWorkingTime += workingTime;
	}
	
	/************************
	 * Getters
	 ************************/

	public List<CCCompileErrorKind> getAllKinds() {
		return new ArrayList<CCCompileErrorKind>(kinds.values());
	}
	
	public int getMessagesID(String message) {
		return ids.get(message);
	}

	public int getTotalErrorCount() {
		return totalErrorCount;
	}
	
	public int getTotalErrorCorrectionTime() {
		return totalErrorCorrectionTime;
	}
	
	public int getTotalWorkingTime() {
		return totalWorkingTime;
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

	public CDirectory getLibDir() {
		return libDir;
	}
	
	public PPProjectSet getPPProjectSet() {
		return ppProjectSet;
	}
	
	public CPath getProjectPath() {
		return projectPath;
	}
	
	public IPRRecordingProject getRecodingproject() {
		return recodingproject;
	}
	
	public CCPathData getPathdata() {
		return pathdata;
	}

	public List<CDirectory> getProjects() {
		return projects;
	}
	
	public IPPVLoader getPPVLoader() {
		return ppvLoader;
	}

	public PPDataManager getPPDataManager() {
		return ppDataManager;
	}

	/************************
	 * Setters
	 ************************/
	
	public void setBaseDir(CDirectory baseDir) {
		this.baseDir = baseDir;
	}
	
	public void setLibDir(CDirectory libDir) {
		this.libDir = libDir;
	}

	public void setPPProjectSet(PPProjectSet ppProjectSet) {
		this.ppProjectSet = ppProjectSet;
	}
	
	public void setProjectPath(CPath projectPath) {
		this.projectPath = projectPath;
	}
	
	public void setRecordingProject(IPRRecordingProject recodingproject) {
		this.recodingproject = recodingproject;
	}

	public void setProjects(List<CDirectory> projects) {
		this.projects = projects;
	}

	public void setPPVLoader(IPPVLoader ppvLoader) {
		this.ppvLoader = ppvLoader;
	}

	public void setPPDataManager(PPDataManager ppDataManager) {
		this.ppDataManager = ppDataManager;
	}
}