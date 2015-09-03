/*
 * PRFileLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core.model;

import clib.common.filesystem.CPath;

/**
 * PRFileLog
 */
public abstract class PRFileLog extends PRLog {

	private CPath path;

	/**
	 * Constructor
	 */
	public PRFileLog(PRLogType type, PRLogSubType subType, CPath path,
			Object... args) {
		super(type, subType);
		addArgument(path);
		addArguments(args);
		this.path = path;
	}

	/**
	 * @return the path
	 */
	public CPath getPath() {
		return path;
	}
}
