/*
 * PPRangeTask.java
 * Created on 2012/06/19
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.model;

import clib.common.time.CTime;

/**
 * @author macchan
 * 
 */
public class PPRangeTask extends PPTask {

	private static final long serialVersionUID = 1L;

	private CTime start;
	private CTime end;

	public PPRangeTask(String name, CTime start, CTime end) {
		super(name);
		this.start = start;
		this.end = end;
	}

	@Override
	public String toString() {
		return getName() + ", " + start + ", " + end;
	}

	/**
	 * @return the start
	 */
	public CTime getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(CTime start) {
		this.start = start;
		fireModelUpdated(start);
	}

	/**
	 * @return the end
	 */
	public CTime getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(CTime end) {
		this.end = end;
		fireModelUpdated(end);
	}
}
