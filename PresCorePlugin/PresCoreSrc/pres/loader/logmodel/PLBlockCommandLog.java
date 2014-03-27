/*
 * PLBlockCommandLog.java
 * Created on 2011/11/08 by macchan
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.logmodel;

import clib.common.filesystem.CPath;

/**
 * TODO H24.2.21 保井変更
 * 継承をPLLogからPLFileLogに変更
 * PLBlockCommandLog
 */
public class PLBlockCommandLog extends PLFileLog {

	/**
	 * TODO H24.2.21 保井変更
	 * @param timestamp
	 * @param type
	 * @param subType
	 * @param args
	 */
	public PLBlockCommandLog(long time, String subType, CPath path,
			Object... args) {
		super(time, "BLOCK_COMMAND_RECORD", subType, path, args);
	}

	/* (non-Javadoc)
	 * @see pres.loader.logmodel.PLLog#getExplanationPhrase()
	 */
	/**
	 * TODO H24.2.21 保井変更
	 */
	@Override
	public String getExplanationPhrase() {

		if (this.getSubType().equals("BLOCK_ADDED")) {
			return "ブロックを追加";
		} else if (this.getSubType().equals("BLOCK_MOVED")) {
			return "ブロックを移動";
		} else if (this.getSubType().equals("BLOCK_REMOVED")) {
			return "ブロックを削除";
		} else if (this.getSubType().equals("BLOCK_RENAMED")) {
			return "ブロックのラベル名を変更";
		} else if (this.getSubType().equals("(BLOCKS_CONNECTED")) {
			return "ブロック同士を連結";
		} else if (this.getSubType().equals("BLOCKS_DISCONNECTED")) {
			return "ブロック同士を切断";
		} else if (this.getSubType().equals("BLOCK_STACK_COMPILED")) {
			return "ブロックのスタックコンパイル";
		} else {
			return this.getSubType();
		}
		//return "BLOCK_COMMAND_RECORD";
	}

	private String message;

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
