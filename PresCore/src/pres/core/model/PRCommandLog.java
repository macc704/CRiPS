/*
 * PRCommandLog.java
 * Created on Apr 17, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core.model;

import clib.common.filesystem.CPath;

/**
 * PRCommandLog
 */
public class PRCommandLog extends PRFileLog {

	public static enum Type implements PRLogType {
		COMMAND_RECORD
	};

	public static enum SubType implements PRLogSubType {
		DELETE, CUT, COPY, PASTE, UNDO, REDO, FOCUS_GAINED, FOCUS_LOST, START_RUN, STOP_RUN, START_DEBUG, STOP_DEBUG, STEP, COMPILE, SAVE, UPDATED, EXTENDED, FILE_CREATED, FILE_DELETED, START_FORMAT, END_FORMAT, DEBUG_PLAY, DEBUG_STOP, DEBUG_SPEED
	};

	/**
	 * Constructor
	 */
	public PRCommandLog(SubType subType, CPath path, Object... args) {
		super(Type.COMMAND_RECORD, subType, path, args);
	}

}
