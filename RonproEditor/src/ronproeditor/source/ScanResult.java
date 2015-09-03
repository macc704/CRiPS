/*
 * ScanResult.java
 * Created on 2007/09/22 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.source;

/**
 *　一行スキャンした結果を表わすインナークラスです。
 */
public class ScanResult {
	private int openBraceCount = 0;
	private int closeBraceCount = 0;
	private boolean inComment = false;
	private boolean isCloseSecond = false;

	/**
	 * コンストラクタです。
	 */
	ScanResult() {
	}

	/**
	 *　読み込んだ行の開き中括弧の数を増やします
	 */
	public void addOpenBraceCount() {
		openBraceCount++;
	}

	/**
	 *　読み込んだ行の閉じ中括弧の数を増やします
	 */
	public void addCloseBraceCount() {
		closeBraceCount++;
		if (openBraceCount > 0) {
			isCloseSecond = true;
		}
	}

	/**
	 * 閉じカッコが開き括弧のあとに呼ばれたとき
	 * 要するに  {return;}  等の行だったときに
	 * １を返します。
	 */
	public int getCloseSecond() {
		return isCloseSecond ? 1 : 0;
	}

	//  ----------------- setter & getter ----------------

	public void setInComment(boolean inComment) {
		ScanResult.this.inComment = inComment;
	}

	public int getOpenBraceCount() {
		return openBraceCount;
	}

	public int getCloseBraceCount() {
		return closeBraceCount;
	}

	public boolean getInComment() {
		return inComment;
	}

}
