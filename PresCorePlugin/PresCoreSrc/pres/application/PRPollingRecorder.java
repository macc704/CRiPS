/*
 * PRPollingRecorder.java
 * Created on 2010/02/13 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.application;

import pres.core.IPRRecordingProject;
import pres.core.PRRecordingProject;
import clib.common.thread.CRunnableThread;

/**
 * PRPollingRecorder
 */
public class PRPollingRecorder extends CRunnableThread {

	private PRRecordingProject project;

	/**
	 * Constructor
	 */
	public PRPollingRecorder() {
	}

	/**
	 * Constructor
	 */
	public PRPollingRecorder(PRRecordingProject project) {
		this.project = project;
	}

	public IPRRecordingProject getProject() {
		return project;
	}

	public void setProject(PRRecordingProject project) {
		if (isRunning()) {
			return;
		}
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see clib.common.thread.ICRunnable#allowStart()
	 */
	public boolean allowStart() {
		return this.project != null;
	}

	/* (non-Javadoc)
	 * @see clib.common.thread.ICRunnable#handlePrepareStart()
	 */
	public void handlePrepareStart() {
		project.start();
	}

	/* (non-Javadoc)
	 * @see clib.common.thread.ICRunnable#handlePrepareStop()
	 */
	public void handlePrepareStop() {
		project.stop();
	}

	/* (non-Javadoc)
	 * @see clib.common.thread.ICRunnable#handleProcessStep()
	 */
	public void handleProcessStep() {
		project.checkTargetFilesAndUpdate();
	}

}
