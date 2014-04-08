/*
 * PPTask.java
 * Created on 2012/06/19
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.model;

import clib.common.model.CAbstractModelObject;

/**
 * @author macchan
 * 
 */
public abstract class PPTask extends CAbstractModelObject {

	private static final long serialVersionUID = 1L;

	private String name;

	public PPTask(String name) {
		this.name = name;
	}

	/**
	 * @return the label
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the label to set
	 */
	public void setName(String name) {
		this.name = name;
		fireModelUpdated(name);
	}

	@Override
	public String toString() {
		return name;
	}

}
