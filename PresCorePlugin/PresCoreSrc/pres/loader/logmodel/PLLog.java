/*
 * PRTempLog.java
 * Created on Apr 16, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.logmodel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import clib.common.time.CTime;
import clib.common.time.ICTimeOrderable;

/**
 * PRTempLog
 */
public abstract class PLLog implements ICTimeOrderable {

	// public interface PRLogType {
	// };
	// public interface PRLogSubType {
	// };

	public static final SimpleDateFormat FORMATER = new SimpleDateFormat(
			"MM/dd-HH:mm:ss");

	private long timestamp;
	private String type;
	private String subType;
	private List<Object> arguments = new ArrayList<Object>();

	/**
	 * Constructor
	 */
	public PLLog(long timestamp, String type, String subType, Object... args) {
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

	public CTime getTime() {
		return new CTime(timestamp);
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	public String getTimeStampString() {
		return FORMATER.format(new Date(this.timestamp));
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the subType
	 */
	public String getSubType() {
		return subType;
	}

	/**
	 * @return the arguments
	 */
	public List<Object> getArguments() {
		return arguments;
	}

	public String toString() {
		return this.getTimeAndType();
	}

	public String getTimeAndType() {
		return this.getTimeStampString() + ":" + this.getType() + ":"
				+ this.getSubType();
	}

	protected String getTypeExplanation() {
		return "Type: " + this.type + "\t" + "SubType: " + this.subType + "\n";
	}

	public String getExplanation() {
		String explanation = this.getTypeExplanation();
		for (int i = 0; i < this.arguments.size(); i++) {
			explanation += "arg[" + (i + 1) + "]:" + this.arguments.get(i)
					+ "\n";
		}
		return explanation;
	}

	public abstract String getExplanationPhrase();

}
