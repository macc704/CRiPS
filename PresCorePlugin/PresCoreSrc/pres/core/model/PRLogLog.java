/*
 * PRLogLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core.model;

/**
 * PRLogLog
 */
public class PRLogLog extends PRLog {

	public static enum Type implements PRLogType {
		LOG;
	};

	public static enum SubType implements PRLogSubType {
		DEBUG, INFO;
	};

	private String message;

	/**
	 * Constructor
	 */
	public PRLogLog(SubType subType, String message) {
		super(Type.LOG, subType, message);
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
