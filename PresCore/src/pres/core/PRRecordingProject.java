/*
 * PRRecordingProject.java
 * Created on 2010/02/12 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core;

import pres.core.model.PRCommandLog;
import pres.core.model.PRLog;
import pres.core.model.PRLogLog;
import pres.core.model.PRProjectLog;
import pres.core.text.PRTextRecorder;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CPath;
import clib.common.system.CEncoding;

/**
 * PRRecordingProject 
 * プロジェクト毎のレコーディング設定を表現する．
 */
public class PRRecordingProject implements IPRRecordingProject {

	public static final String RECORDING_DIRNAME_DEFAULT = ".pres2";
	public static final String RECORDING_FILENAME_DEFAULT = "pres2.log";

	private CDirectory baseDirectory;
	private CDirectory recordingDirectory;

	private CFileFilter fileFilter = CFileFilter.ALL_ACCEPT_FILTER();
	private CFileFilter dirFilter = CFileFilter.IGNORE_BY_NAME_FILTER(".*");

	private PRTextRecorder recorder;
	private PRRecordingFileManager targetManager;

	private boolean running = false;

	/**
	 * Constructor
	 */
	public PRRecordingProject(CDirectory baseDirectory) {
		this(baseDirectory, baseDirectory.findOrCreateDirectory(new CPath(
				RECORDING_DIRNAME_DEFAULT)));
	}

	/**
	 * Constructor
	 */
	public PRRecordingProject(CDirectory baseDirectory,
			CDirectory recordingDirectory) {
		this(baseDirectory, recordingDirectory, RECORDING_FILENAME_DEFAULT);
	}

	/**
	 * Constructor
	 */
	public PRRecordingProject(CDirectory baseDirectory,
			CDirectory recordingDirectory, String recordingFilename) {
		this.baseDirectory = baseDirectory;
		this.recordingDirectory = recordingDirectory;
		CFile file = this.recordingDirectory
				.findOrCreateFile(recordingFilename);
		file.setEncodingOut(CEncoding.UTF8);
		this.recorder = new PRTextRecorder(file);
		this.targetManager = new PRRecordingFileManager(this);
	}

	public boolean valid() {
		return !this.baseDirectory.deleted()
				&& !this.recordingDirectory.deleted();
	}

	public boolean isRunning() {
		return this.running;
	}

	/* (non-Javadoc)
	 * @see pres.core.IPRRecordingProject#start()
	 */
	public void start() {
		if (isRunning()) {
			throw new RuntimeException();
		}
		this.running = true;
		record(new PRProjectLog(PRProjectLog.SubType.START));
	}

	/* (non-Javadoc)
	 * @see pres.core.IPRRecordingProject#stop()
	 */
	public void stop() {
		if (!isRunning()) {
			throw new RuntimeException();
		}
		record(new PRProjectLog(PRProjectLog.SubType.STOP));
		this.running = false;
	}

	// Interfaceの実装

	/* (non-Javadoc)
	 * @see pres.core.IPRRecorder#recordLog(java.lang.Object[])
	 */
	public void recordInfo(String message) {
		record(new PRLogLog(PRLogLog.SubType.INFO, message));
	}

	/* (non-Javadoc)
	 * @see pres.core.IPRRecorder#recordDebug(java.lang.Object[])
	 */
	public void recordDebug(String message) {
		record(new PRLogLog(PRLogLog.SubType.DEBUG, message));
	}

	/* (non-Javadoc)
	 * @see pres.core.IPRRecordingProject#record(pres.core.model.PRLog)
	 */
	public void record(PRLog log) {
		if (valid() && isRunning()) {
			recorder.record(log);
			if (log.getType() == PRCommandLog.Type.COMMAND_RECORD
					&& log.getSubType() == PRCommandLog.SubType.SAVE) {
				stampFile(((PRCommandLog) log).getPath());
			}
		}
	}

	/* (non-Javadoc)
	 * @see pres.core.IPRRecordingProject#checkTargetFilesAndUpdate()
	 */
	public void checkTargetFilesAndUpdate() {
		if (valid() && isRunning()) {
			targetManager.checkTargetFilesAndUpdate();
		}
	}

	/******************************
	 * record sub
	 ******************************/

	private void stampFile(CPath target) {
		PRRecordingFile file = targetManager.getRecordingFile(target);
		if (file != null) {
			file.stamp();
		} else {
			this.recordDebug("RecordingFile Not Found.: " + target);
		}
	}

	/******************************
	 * setters & getters
	 ******************************/

	protected CDirectory getBaseDirectory() {
		return baseDirectory;
	}

	protected CDirectory getRecordingDirectory() {
		return recordingDirectory;
	}

	protected CFileFilter getFileFilter() {
		return fileFilter;
	}

	protected CFileFilter getDirFilter() {
		return dirFilter;
	}

	/* (non-Javadoc)
	 * @see pres.core.IPRRecordingProject#setFileFilter(clib.common.filesystem.CFileFilter)
	 */
	public void setFileFilter(CFileFilter fileFilter) {
		if (isRunning()) {
			throw new RuntimeException();
		}
		this.fileFilter = fileFilter;
		this.targetManager.initializeTargetFiles();
	}

	/* (non-Javadoc)
	 * @see pres.core.IPRRecordingProject#setDirFilter(clib.common.filesystem.CFileFilter)
	 */
	public void setDirFilter(CFileFilter dirFilter) {
		if (isRunning()) {
			throw new RuntimeException();
		}
		this.dirFilter = dirFilter;
		this.targetManager.initializeTargetFiles();
	}
}
