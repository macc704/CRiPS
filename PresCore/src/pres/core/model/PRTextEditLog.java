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
public class PRTextEditLog extends PRFileLog {

	public static enum Type implements PRLogType {
		TEXTEDIT_RECORD
	};

	//ECLIPSEタイプは，INSERTかDELETEかは区別しない．
	//TEXTが1文字以上ならINSERTを含み，LENGTHが1以上ならDELETEを含む．
	public static enum SubType implements PRLogSubType {
		INSERT, DELETE, ECLIPSE
	};

	private int offset;
	private int length;
	private String text;

	/**
	 * Constructor
	 */
	public PRTextEditLog(SubType subType, CPath path, int offset, int length,
			String text) {
		super(Type.TEXTEDIT_RECORD, subType, path, offset, length, text);
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
}
