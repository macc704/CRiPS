/*
 * PPTask.java
 * Created on 2012/05/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.model;

import clib.common.time.CTime;

/**
 * @author macchan
 */
public class PPActualTask extends PPRangeTask {

	private static final long serialVersionUID = 1L;

	private PPEstimatedTask ceTask;

	public PPActualTask(String name, CTime start, CTime end) {
		super(name, start, end);
	}

	/**
	 * @return the ceTask
	 */
	public PPEstimatedTask getCeTask() {
		return ceTask;
	}

	/**
	 * @param ceTask
	 *            the ceTask to set
	 */
	public void setCeTask(PPEstimatedTask ceTask) {
		this.ceTask = ceTask;
		fireModelUpdated(ceTask);
	}

}
