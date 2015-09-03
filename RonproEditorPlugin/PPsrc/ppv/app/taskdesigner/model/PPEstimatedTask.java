/*
 * A.java
 * Created on 2012/05/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.model;

/**
 * @author macchan
 */
public class PPEstimatedTask extends PPTask {

	private static final long serialVersionUID = 1L;

	private int estimation;

	/**
	 * 
	 */
	public PPEstimatedTask(String name) {
		super(name);
	}

	/**
	 * @return the estimation
	 */
	public int getEstimation() {
		return estimation;
	}

	/**
	 * @param estimation
	 *            the estimation to set
	 */
	public void setEstimation(int estimation) {
		this.estimation = estimation;
	}

}
