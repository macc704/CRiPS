package pres.loader.logmodel;

import pres.core.model.PRFileLog;
import clib.common.filesystem.CPath;

public class PRCocoViewerLog extends PRFileLog {

	public static enum Type implements PRLogType {
		COCOVIEWER_RECORD
	};

	public static enum SubType implements PRLogSubType {
		ANY, COCOVIEWER_OPEN, COCOVIEWER_CLOSE, DETAIL_OPEN, DETAIL_CLOSE, SOURCE_OPEN, SOURCE_CLOSE, SOURCE_OPEN_ERROR
	};

	/**
	 * Constructor
	 */
	public PRCocoViewerLog(SubType subType, CPath path, Object... args) {
		super(Type.COCOVIEWER_RECORD, subType, path, args);
	}
}