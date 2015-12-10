/*
 * PLCheCoProCommandLog.java
 * Created on 2015/11/11 by macchan
 * Copyright(c) 2015 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.logmodel;

import clib.common.filesystem.CPath;

/**
 * PLCheCoProCommandLog
 */
public class PLCheCoProCommandLog extends PLFileLog{

	public PLCheCoProCommandLog(long time, String subType, CPath path, Object... args) {
		super(time, "CH_COMMAND_RECORD", subType, path, args);
	}
	
	/* (non-Javadoc)
	 * @see pres.loader.logmodel.PLLog#getExplanationPhrase()
	 */
	@Override
	public String getExplanationPhrase() {
		
		String subType = this.getSubType();
		if (subType.equals("LOGIN")) {
			return "ログイン";
		} else if (subType.equals("LOGOUT")) {
			return "ログアウト";
		} else if (subType.equals("ALL_IMPORT")) {
			return "全部取込";
		} else if (subType.equals("PARTIAL_IMPORT")) {
			return "部分取込";
		} else if (subType.equals("FOCUS_GAINED")) {
			return "フォーカスを得る";
		} else if (subType.equals("FOCUS_LOST")) {
			return "フォーカスを失う";	
		} else {
			return subType;
		}
	}

}
