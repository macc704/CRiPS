/*
 * PRNullRecordingProject.java
 * Created on Apr 11, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core;

import pres.core.model.PRLog;
import clib.common.filesystem.CFileFilter;

/**
 * PRNullRecordingProject
 */
public class PRNullRecordingProject implements IPRRecordingProject {

	public boolean valid() {
		return false;
	}

	public boolean isRunning() {
		return false;
	}

	public void start() {
	}

	public void stop() {
	}

	public void recordDebug(String message) {
	}

	public void recordInfo(String message) {
	}

	public void record(PRLog log) {
	}

	public void checkTargetFilesAndUpdate() {
	}

	public void setDirFilter(CFileFilter dirFilter) {
	}

	public void setFileFilter(CFileFilter fileFilter) {
	}

}
