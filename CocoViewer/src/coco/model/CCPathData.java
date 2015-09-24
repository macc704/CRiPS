package coco.model;


import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;

public class CCPathData {

	/************************
	 * FilePath
	 ************************/
	private String KindsFilePath = "ext/cocoviewer/ErrorKinds.csv";
	private String OriginalDataFilePath = "CompileError.csv";
	private String DataFilePath = "CompileErrorLog.csv";
	private String MetricsFilePath = "FileMetrics.csv";

	/************************
	 * Directory
	 ************************/
	private CDirectory ppvRootDir = new CDirectory(new CPath(".ppv"));// MyProjects/.ppvフォルダに展開する
	private CDirectory ppvLibDir = new CDirectory(new CPath("lib"));
	private String ppvTempDir = "tmp";// zipファイルを展開するための一時フォルダ /.ppv中
	private String ppvProjectSetName = "hoge";// projectset名


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

	public String getPPVTempDir() {
		return ppvTempDir;
	}

	public CDirectory getPPVLibDir() {
		return ppvLibDir;
	}

	public String getPPVProjectSetName() {
		return ppvProjectSetName;
	}
}
