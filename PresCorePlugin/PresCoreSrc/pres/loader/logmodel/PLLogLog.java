/*
 * PRLogLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.logmodel;

import pres.core.model.PRLogLog;

/**
 * PRLogLog
 */
public class PLLogLog extends PLLog {

	//	public static enum Type implements PRLogType {
	//		LOG;
	//	};
	//	public static enum SubType implements PRLogSubType {
	//		DEBUG, INFO;
	//	};

	private String message;

	/**
	 * Constructor
	 */
	public PLLogLog(long time, String subType, String message) {
		super(time, PRLogLog.Type.LOG.toString(), subType, message);
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	@Override
	public String getExplanation() {
		String exp = super.getTypeExplanation();
		exp += "message：" + this.getMessage() + "\n";
		return exp;
	}

	@Override
	public String getExplanationPhrase() {
		return this.getSubType();
	}

}
