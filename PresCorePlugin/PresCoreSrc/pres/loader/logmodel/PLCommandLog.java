/*
 * PRCommandLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.logmodel;

import clib.common.filesystem.CPath;


/**
 * PRCommandLog
 */
public class PLCommandLog extends PLFileLog {

	/*
	public static enum Type implements PRLogType {
		COMMAND_RECORD
	};

	public static enum SubType implements PRLogSubType {
		DELETE, CUT, COPY, PASTE, UNDO, REDO, FOCUS_GAINED, FOCUS_LOST, START_RUN, STOP_RUN, START_DEBUG, STOP_DEBUG, COMPILE, SAVE, UPDATED, EXTENDED
	};
	*/
	
	/**
	 * Constructor
	 */
	public PLCommandLog(long time, String subType, CPath path, Object... args) {
		super(time, "COMMAND_RECORD", subType, path, args);
	}

	@Override
	public String getExplanationPhrase() {

		if( this.getSubType().equals("SAVE") ){
			return "セーブ";
		} else if( this.getSubType().equals("COMPILE") ){
			return "コンパイル";
		} else if( this.getSubType().equals("START_RUN") ){
			return "プログラム実行";
		} else if( this.getSubType().equals("STOP_RUN") ){
			return "プログラム停止";
		} else if( this.getSubType().equals("START_DEBUG" ) ){
			return "デバッグ開始";
		} else if( this.getSubType().equals("STOP_DEBUG" ) ){
			return "デバッグ停止";
		}
		else{
			return this.getSubType();
		}
	}

}
