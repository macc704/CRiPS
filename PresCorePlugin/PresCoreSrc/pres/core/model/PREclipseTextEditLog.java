/*
 * PRTextEditLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core.model;

import clib.common.filesystem.CPath;

/**
 * PRTextEditLog
 */
public class PREclipseTextEditLog extends PRFileLog {

	public static enum Type implements PRLogType {
		TEXTEDIT_RECORD_ECLIPSE
	};

	public static enum SubType implements PRLogSubType {
		CREATE, CHANGE, UNDONE, REDONE
	};

	private int start;
	private int end;
	private String text;
	private String preservedText;

	/**
	 * Constructor
	 */
	public PREclipseTextEditLog(SubType subType, CPath path, int start,
			int end, String text, String preservedText) {
		super(Type.TEXTEDIT_RECORD_ECLIPSE, subType, path, start, end, text,
				preservedText);
		this.start = start;
		this.end = end;
		this.text = text;
		this.preservedText = preservedText;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the preservedText
	 */
	public String getPreservedText() {
		return preservedText;
	}
}
