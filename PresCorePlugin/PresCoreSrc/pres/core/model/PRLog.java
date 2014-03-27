/*
 * PRTempLog.java
 * Created on Apr 16, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * PRTempLog
 */
public class PRLog {

	public interface PRLogType {
	};

	public interface PRLogSubType {
	};

	private long timestamp;
	private PRLogType type;
	private PRLogSubType subType;
	private List<Object> arguments = new ArrayList<Object>();

	/**
	 * Constructor
	 */
	public PRLog(PRLogType type, PRLogSubType subType, Object... args) {
		this(System.currentTimeMillis(), type, subType, args);
	}

	/**
	 * Constructor
	 */
	public PRLog(long timestamp, PRLogType type, PRLogSubType subType,
			Object... args) {
		this.timestamp = timestamp;
		this.type = type;
		this.subType = subType;
		addArguments(args);

	}

	protected void addArgument(Object arg) {
		this.arguments.add(arg);
	}

	protected void addArguments(Object[] args) {
		for (int i = 0; i < args.length; i++) {
			this.arguments.add(args[i]);
		}
	}

	//	protected static Object[] insertArguments(Object[] args, Object... inserts) {
	//		int newlen = inserts.length + args.length;
	//		Object[] newArgs = new Object[newlen];
	//		System.arraycopy(inserts, 0, newArgs, 0, inserts.length);
	//		System.arraycopy(args, 0, newArgs, inserts.length, args.length);
	//		return newArgs;
	//	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the type
	 */
	public PRLogType getType() {
		return type;
	}

	/**
	 * @return the subType
	 */
	public PRLogSubType getSubType() {
		return subType;
	}

	/**
	 * @return the arguments
	 */
	public List<Object> getArguments() {
		return arguments;
	}
}
