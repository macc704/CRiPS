/*
 * PRFileLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.logmodel;

import clib.common.filesystem.CPath;

/**
 * PRFileLog
 */
public abstract class PLFileLog extends PLLog {

	private CPath path;

	/**
	 * Constructor
	 */
	public PLFileLog(long time, String type, String subType, CPath path,
			Object... args) {
		super(time, type, subType);
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

	public String toString() {
		return super.toString() + ":" + path;
	}

	@Override
	public String getExplanation() {
		String exp = super.getTypeExplanation();
		exp += "ファイル：" + this.path;
		return exp;
	}

}
