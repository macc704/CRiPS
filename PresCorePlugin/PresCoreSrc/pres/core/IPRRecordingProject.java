/*
 * IPRRecordingProject.java
 * Created on Apr 11, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core;

import pres.core.model.PRLog;
import clib.common.filesystem.CFileFilter;

/**
 * IPRRecordingProject
 */
public interface IPRRecordingProject {

	public boolean valid();

	public boolean isRunning();

	public void start();

	public void stop();

	public void recordInfo(String message);

	public void recordDebug(String message);

	public void record(PRLog log);

	public void checkTargetFilesAndUpdate();

	public void setFileFilter(CFileFilter fileFilter);

	public void setDirFilter(CFileFilter dirFilter);

}