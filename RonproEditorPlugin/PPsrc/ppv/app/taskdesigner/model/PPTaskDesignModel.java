/*
 * PPTaskManager.java
 * Created on 2012/05/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author macchan saveのためのクラス
 */
public class PPTaskDesignModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<PPActualTask> actualTasks;
	private List<PPEstimatedTask> estimatedTasks;
	private List<PPDefectFixingTask> defectFixingTasks;

	public PPTaskDesignModel(List<PPActualTask> actualTasks,
			List<PPEstimatedTask> estimatedTasks,
			List<PPDefectFixingTask> defectFixingTasks) {
		this.actualTasks = actualTasks;
		this.estimatedTasks = estimatedTasks;
		this.defectFixingTasks = defectFixingTasks;
	}

	/**
	 * @return the tasks
	 */
	public List<PPActualTask> getActualTasks() {
		return actualTasks;
	}

	/**
	 * @return the cetasks
	 */
	public List<PPEstimatedTask> getEstimatedTasks() {
		return estimatedTasks;
	}

	/**
	 * @return the defectFixingTasks
	 */
	public List<PPDefectFixingTask> getDefectFixingTasks() {
		return defectFixingTasks;
	}
}
