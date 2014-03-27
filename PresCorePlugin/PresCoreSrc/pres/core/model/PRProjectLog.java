/*
 * PRProjectLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core.model;

/**
 * PRProjectLog
 */
public class PRProjectLog extends PRLog {

	public static enum Type implements PRLogType {
		PROJECT_RECORD;
	};

	public static enum SubType implements PRLogSubType {
		START, STOP, REFACTORING, EXTENDED
	};

	/**
	 * Constructor
	 */
	public PRProjectLog(SubType subType, Object... args) {
		super(Type.PROJECT_RECORD, subType, args);
	}

}
