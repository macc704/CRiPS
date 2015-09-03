/*
 * PRTextEditLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.logmodel;

import clib.common.filesystem.CPath;

/**
 * PRTextEditLog
 */
public class PLTextEditLog extends PLFileLog {

	//	public static enum Type implements PRLogType {
	//		TEXTEDIT_RECORD
	//	};
	//	public static enum SubType implements PRLogSubType {
	//		INSERT, DELETE
	//	};

	private int offset;
	private int length;
	private String text;

	/**
	 * Constructor
	 */
	public PLTextEditLog(long time, String subType, CPath path, int offset,
			int length, String text) {
		super(time, "TEXTEDIT_RECORD", subType, path, offset, length, text);
		this.offset = offset;
		this.length = length;
		this.text = text;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	@Override
	public String getExplanation() {
		String exp = super.getTypeExplanation();
		exp += "offset="
				+ this.getOffset()
				+ ", length="
				+ this.getLength()
				+ ", text="
				+ this.getText().replace("\n", "\\n").replace("\r", "\\r")
						.replace("\t", "\\t");
		return exp;
	}

	@Override
	public String getExplanationPhrase() {
		return "TEXTEDIT_RECORD:" + this.getSubType();
	}

}
