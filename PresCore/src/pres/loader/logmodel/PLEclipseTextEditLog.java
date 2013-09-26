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
public class PLEclipseTextEditLog extends PLFileLog {

//	public static enum Type implements PRLogType {
//		TEXTEDIT_RECORD_ECLIPSE
//	};
//	public static enum SubType implements PRLogSubType {
//		CREATE, CHANGE, UNDONE, REDONE
//	};

	private int start;
	private int end;
	private String text;
	private String preservedText;

	/**
	 * Constructor
	 */
	public PLEclipseTextEditLog(long time, String subType, CPath path, int start,
			int end, String text, String preservedText) {
		super(time, "TEXTEDIT_RECORD_ECLIPSE", subType, path, start, end, text,
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
	
	@Override
	public String getExplanation(){
		String exp = super.getTypeExplanation();
		exp += "file：" + this.getPath() + "\n";
		exp += "position：" + this.start + "文字目～" + this.end + "文字目\n";
		exp += "text：" + this.text + "\n";
		exp += "preservedText：" + this.preservedText;
		return exp;
	}

	@Override
	public String getExplanationPhrase() {
		return "テキスト操作：" + this.getSubType();
	}

}
