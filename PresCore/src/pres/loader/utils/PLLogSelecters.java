/*
 * CLogCheckers.java
 * Created on 2011/06/29 by macchan
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.utils;

import java.util.ArrayList;
import java.util.List;

import pres.core.model.PRCommandLog;
import pres.core.model.PREclipseTextEditLog;
import pres.core.model.PRTextEditLog;
import pres.loader.logmodel.PLLog;
import clib.common.utils.ICChecker;

/**
 * CLogCheckers
 */
public class PLLogSelecters {

	public static ICChecker<PLLog> ALL = new ICChecker<PLLog>() {
		public boolean check(PLLog t) {
			return true;
		}
	};

	public static ICChecker<PLLog> COMPILE_RUN = CLogSelecter.createBySubType(
			PRCommandLog.SubType.COMPILE.toString(),
			PRCommandLog.SubType.START_RUN.toString());

	public static ICChecker<PLLog> COMPILE = CLogSelecter
			.createBySubType(PRCommandLog.SubType.COMPILE.toString());

	public static ICChecker<PLLog> RUN = CLogSelecter
			.createBySubType(PRCommandLog.SubType.START_RUN.toString());

	public static ICChecker<PLLog> SAVE = CLogSelecter
			.createBySubType(PRCommandLog.SubType.SAVE.toString());

	public static ICChecker<PLLog> TEXTEDIT = CLogSelecter
			.createByType(PRTextEditLog.Type.TEXTEDIT_RECORD.toString());

	public static ICChecker<PLLog> FILELOG = CLogSelecter.createByType(
			PRCommandLog.Type.COMMAND_RECORD.toString(),
			PRTextEditLog.Type.TEXTEDIT_RECORD.toString(),
			PREclipseTextEditLog.Type.TEXTEDIT_RECORD_ECLIPSE.toString());

	/**
	 * TODO H24.1.21 保井追加 BlockEditorのログチェッカー
	 */
	public static ICChecker<PLLog> BLOCKEDIT = CLogSelecter
			.createByType(PRCommandLog.Type.BLOCK_COMMAND_RECORD.toString());
	
	public static ICChecker<PLLog> IMAGEGETLOGS = CLogSelecter
			.createBySubType("BLOCK_ADDED","BLOCKS_CONNECTED","BLOCKS_DISCONNECTED","BLOCKS_COPYED","BLOCKS_CONNECT_MISSED");
}

class CLogSelecter implements ICChecker<PLLog> {

	public static CLogSelecter createByType(String... types) {
		CLogSelecter selecter = new CLogSelecter();
		for (String type : types) {
			selecter.addType(type);
		}
		return selecter;
	}

	public static CLogSelecter createBySubType(String... subTypes) {
		CLogSelecter selecter = new CLogSelecter();
		for (String subType : subTypes) {
			selecter.addSubType(subType);
		}
		return selecter;
	}

	private List<String> types = new ArrayList<String>();
	private List<String> subTypes = new ArrayList<String>();

	CLogSelecter() {
	}

	void addType(String type) {
		types.add(type);
	}

	void addSubType(String subType) {
		subTypes.add(subType);
	}

	public boolean check(PLLog t) {
		if (isType(types, t.getType())) {
			if (isType(subTypes, t.getSubType())) {
				return true;
			}
		}
		return false;
	}

	boolean isType(List<String> list, String target) {
		if (list.isEmpty()) {
			return true;
		}
		for (String each : list) {
			if (target.startsWith(each)) {
				return true;
			}
		}
		return false;
	}
}