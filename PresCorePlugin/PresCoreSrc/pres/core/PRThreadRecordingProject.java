/*
 * PRThreadRecordingProject.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core;

import pres.core.model.PRLog;
import pres.core.model.PRLogLog;
import clib.common.filesystem.CFileFilter;
import clib.common.thread.CTaskManager;
import clib.common.thread.ICTask;

/**
 * PRThreadRecordingProject
 */
public class PRThreadRecordingProject implements IPRRecordingProject {

	private CTaskManager taskManager;
	private PRRecordingProject project;

	/**
	 * Constructor
	 */
	public PRThreadRecordingProject(PRRecordingProject project) {
		this.project = project;
		this.taskManager = new CTaskManager();
		this.taskManager.setInterval(500);
		this.taskManager.setPriority(Thread.currentThread().getPriority() - 1);
	}

	public boolean valid() {
		return project.valid();
	}

	public boolean isRunning() {
		return this.taskManager.isRunning();
	}

	public synchronized void start() {
		if (isRunning() || project.isRunning()) {
			throw new RuntimeException();
		}
		project.start();
		this.taskManager.start();
		this.recordInfo("LoggingThread Start");
	}

	public synchronized void stop() {
		if (!isRunning() || !project.isRunning()) {
			throw new RuntimeException();
		}
		this.recordInfo("LoggingThread Stop");
		this.taskManager.stop();
		project.stop();
	}

	public void checkTargetFilesAndUpdate() {
		if (valid() && isRunning()) {
			taskManager.addTask(new ICTask() {
				public void doTask() {
					project.checkTargetFilesAndUpdate();
				}
			});
		}
	}

	public void record(final PRLog log) {
		if (valid() && isRunning()) {
			taskManager.addTask(new ICTask() {
				public void doTask() {
					project.record(log);
				}
			});
		}
	}

	public void recordDebug(String message) {
		record(new PRLogLog(PRLogLog.SubType.DEBUG, message));
	}

	public void recordInfo(String message) {
		record(new PRLogLog(PRLogLog.SubType.INFO, message));
	}

	public void setDirFilter(CFileFilter dirFilter) {
		if (isRunning()) {
			throw new RuntimeException();
		}
		project.setDirFilter(dirFilter);
	}

	public void setFileFilter(CFileFilter fileFilter) {
		if (isRunning()) {
			throw new RuntimeException();
		}
		project.setFileFilter(fileFilter);
	}

}
