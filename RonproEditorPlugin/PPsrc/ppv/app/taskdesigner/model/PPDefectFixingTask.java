/*
 * PPDefectTask.java
 * Created on 2012/06/19
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.model;

import clib.common.time.CTime;

/**
 * @author macchan
 * 
 */
public class PPDefectFixingTask extends PPRangeTask {

	private static final long serialVersionUID = 1L;

	/**
	 * @param name
	 * @param start
	 * @param end
	 */
	public PPDefectFixingTask(String name, CTime start, CTime end) {
		super(name, start, end);
	}

}
