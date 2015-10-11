package coco.model;


import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;

public class CCPathData {

	/************************
	 * FilePath
	 ************************/
	private String KindsFilePath;
	private String OriginalDataFilePath;
	private String DataFilePath;
	private String MetricsFilePath;

	/************************
	 * Directory
	 ************************/
	private CDirectory ppvRootDir;// MyProjects/.ppvフォルダに展開する
	private CDirectory ppvLibDir;
	private CDirectory ppvTempDir;// zipファイルを展開するための一時フォルダ /.ppv中
	private CDirectory ppvProjectSetNameDir;// projectset名


	public CCPathData() {

	}

	/************************
	 * Getters
	 ************************/

	public String getKindsFilePath() {
		return KindsFilePath;
	}

	public String getOriginalDataFilePath() {
		return OriginalDataFilePath;
	}

	public String getDataFilePath() {
		return DataFilePath;
	}

	public CDirectory getPPVRootDir() {
		return ppvRootDir;
	}

	public CDirectory getPPVLibDir() {
		return ppvLibDir;
	}

	public CDirectory getPPVTempDir() {
		return ppvTempDir;
	}

	public CDirectory getPPVProjectSetName() {
		return ppvProjectSetNameDir;
	}
	
	
	/*******************************
	 * Setters
	 ******************************/
	public void setKindsFilePath(String filepath) {
		KindsFilePath = filepath;
	}

	public void setOriginalDataFilePath(String filepath) {
		OriginalDataFilePath = filepath;
	}

	public void setDataFilePath(String filepath) {
		DataFilePath = filepath;
	}

	public void setMetricsFilePath(String filepath) {
		MetricsFilePath = filepath;
	}
	
	public void setPPVRootDir(CDirectory dir) {
		ppvRootDir = dir;
	}

	public void setPPVLibDir(CDirectory dir) {
		ppvLibDir  = dir;
	}

	public void setPPVTempDir(CDirectory dir) {
		ppvTempDir = dir;
	}


	public void setPPVProjectSetName(CDirectory dir) {
		ppvProjectSetNameDir = dir;
	}	
}
