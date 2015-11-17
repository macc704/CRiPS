/*
 * PRCheCoProLog.java
 * Created on 2015/11/11 by macchan
 * Copyright(c) 2015 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core.model;

import clib.common.filesystem.CPath;

/**
 * PRCheCoProLog
 */
public class PRCheCoProLog extends PRFileLog{

	public static enum Type implements PRLogType {
		CH_COMMAND_RECORD
	};
	
	// ALL_IMPORTはuser, java, resourceの順
	public static enum SubType implements PRLogSubType {
		LOGIN, LOGOUT, ALL_IMPORT
	};
	
	/**
	 * @param type
	 * @param subType
	 * @param path
	 * @param args
	 */
	public PRCheCoProLog(PRLogSubType subType, CPath path, Object[] args) {
		super(Type.CH_COMMAND_RECORD, subType, path, args);
	}

}
