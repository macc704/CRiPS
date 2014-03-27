/*
 * PRProjectLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.logmodel;

import pres.core.model.PRProjectLog;

/**
 * PRProjectLog
 */
public class PLProjectLog extends PLLog {

	//	public static enum Type implements PRLogType {
	//		PROJECT_RECORD;
	//	};
	//	public static enum SubType implements PRLogSubType {
	//		START, STOP, REFACTORING, EXTENDED
	//	};

	/**
	 * Constructor
	 */
	public PLProjectLog(long time, String subType, Object... args) {
		super(time, PRProjectLog.Type.PROJECT_RECORD.toString(), subType, args);
	}

	@Override
	public String getExplanationPhrase() {
		return "PROJECT_RECORD:" + super.getSubType();
	}

}
