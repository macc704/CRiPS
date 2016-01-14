/*
 * PLLogDivider.java
 * Created on 2013/02/07 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pres.loader.logmodel.PLBlockCommandLog;
import pres.loader.logmodel.PLLog;
import pres.loader.logmodel.PLTextEditLog;

/**
 * PLLogDivider
 */
public class PLLogSeparator {

	private boolean divided = false;
	private List<PLLog> separated = new ArrayList<PLLog>();
	private List<PLLog> nonseparated = new ArrayList<PLLog>();

	/**
	 * 
	 */
	public PLLogSeparator() {
	}

	/**
	 * @return the matched
	 */
	public List<PLLog> getSeparated() {
		if (!divided) {
			throw new RuntimeException();
		}
		return separated;
	}

	/**
	 * @return the unMatched
	 */
	public List<PLLog> getNonSeparated() {
		if (!divided) {
			throw new RuntimeException();
		}
		return nonseparated;
	}

	/*
	ノーマルパターン
	1351142019047	Thu Oct 25 14:13:39 JST 2012	BLOCK_COMMAND_RECORD	JAVA_TO_BLOCK	Circle.java
	1351142021168	Thu Oct 25 14:13:41 JST 2012	BLOCK_COMMAND_RECORD	LOADING_START	Circle.java
	1351142021823	Thu Oct 25 14:13:41 JST 2012	BLOCK_COMMAND_RECORD	ANY	Circle.java	WorkspaceEvent(BLOCK_ADDED: Block 1: 進む with sockets: [Connector label: 歩数, Connector kind: number, blockID: -1 with pos type: SINGLE] and plug: null before: Connector label: , Connector kind: cmd, blockID: -1 with pos type: TOP after: Connector label: , Connector kind: cmd, blockID: -1 with pos type: BOTTOM)
	1351142018157	Thu Oct 25 14:13:38 JST 2012	BLOCK_COMMAND_RECORD	ANY	Circle.java	WorkspaceEvent(PAGE_ADDED: Page name: BlockEditor page color java.awt.Color[r=40,g=40,b=40] page width 4000.0 page drawer NewClass)
	1351142018438	Thu Oct 25 14:13:38 JST 2012	BLOCK_COMMAND_RECORD	ANY	Circle.java	WorkspaceEvent(PAGE_REMOVED: Page name:  page color java.awt.Color[r=40,g=40,b=40] page width 700.0 page drawer )
	....
	1351142017861	Thu Oct 25 14:13:37 JST 2012	BLOCK_COMMAND_RECORD	LOADING_END	Circle.java
	*/

	/*
	こんなにいきなりのパターンもある => 最初に開いたとき　WorkspaceEvent(BLOCK_ADDED: Block 1を検索することで対応
	1351124387644	Thu Oct 25 09:19:47 JST 2012	TEXTEDIT_RECORD	INSERT	Ｔｉｒｅ.java	47	1	子
	1351124389539	Thu Oct 25 09:19:49 JST 2012	COMMAND_RECORD	SAVE	Ｔｉｒｅ.java
	1351124391911	Thu Oct 25 09:19:51 JST 2012	COMMAND_RECORD	FOCUS_LOST	Box.java
	1351124425123	Thu Oct 25 09:20:25 JST 2012	BLOCK_COMMAND_RECORD	ANY	Circle.java	WorkspaceEvent(BLOCK_ADDED: Block 1: 進む with sockets: [Connector label: 歩数, Connector kind: number, blockID: -1 with pos type: SINGLE] and plug: null before: Connector label: , Connector kind: cmd, blockID: -1 with pos type: TOP after: Connector label: , Connector kind: cmd, blockID: -1 with pos type: BOTTOM)
	・・・
	1351124426465	Thu Oct 25 09:20:26 JST 2012	BLOCK_COMMAND_RECORD	OPENED	Circle.java
	 */

	/*
	こういうパターンがある（ファイルを変えたときか！） => WorkspaceEvent(PAGE_は抜く事で対応
	1351142023399	Thu Oct 25 14:13:43 JST 2012	BLOCK_COMMAND_RECORD	ANY	Circle.java	WorkspaceEvent(BLOCK_ADDED: Block 249: 円を描く with sockets: [Connector label: , Connector kind: cmd, blockID: 250 with pos type: SINGLE] and plug: null before: Connector label: , Connector kind: cmd, blockID: 247 with pos type: TOP after: Connector label: , Connector kind: cmd, blockID: -1 with pos type: BOTTOM)
	1351142023399	Thu Oct 25 14:13:43 JST 2012	BLOCK_COMMAND_RECORD	ANY	Circle.java	WorkspaceEvent(BLOCK_ADDED: Block 245: start with sockets: [Connector label: , Connector kind: poly, blockID: -1 with pos type: SINGLE] and plug: null before: null after: Connector label: , Connector kind: cmd, blockID: 247 with pos type: BOTTOM)
	1351142023415	Thu Oct 25 14:13:43 JST 2012	BLOCK_COMMAND_RECORD	LOADING_END	Circle.java
	1351142137452	Thu Oct 25 14:15:37 JST 2012	BLOCK_COMMAND_RECORD	ANY	Shell.java	WorkspaceEvent(PAGE_REMOVED: Page name:  page color java.awt.Color[r=40,g=40,b=40] page width 700.0 page drawer )
	1351142137452	Thu Oct 25 14:15:37 JST 2012	BLOCK_COMMAND_RECORD	ANY	Shell.java	WorkspaceEvent(PAGE_ADDED: Page name: BlockEditor page color java.awt.Color[r=40,g=40,b=40] page width 4000.0 page drawer NewClass)
	1351142137670	Thu Oct 25 14:15:37 JST 2012	BLOCK_COMMAND_RECORD	ANY	Shell.java	WorkspaceEvent(PAGE_REMOVED: Page name:  page color java.awt.Color[r=40,g=40,b=40] page width 700.0 page drawer )
	1351142137670	Thu Oct 25 14:15:37 JST 2012	BLOCK_COMMAND_RECORD	ANY	Shell.java	WorkspaceEvent(PAGE_ADDED: Page name: BlockEditor page color java.awt.Color[r=40,g=40,b=40] page width 4000.0 page drawer NewClass)
	1351142137873	Thu Oct 25 14:15:37 JST 2012	BLOCK_COMMAND_RECORD	ANY	Shell.java	WorkspaceEvent(PAGE_REMOVED: Page name:  page color java.awt.Color[r=40,g=40,b=40] page width 700.0 page drawer )
	1351142137889	Thu Oct 25 14:15:37 JST 2012	BLOCK_COMMAND_RECORD	ANY	Shell.java	WorkspaceEvent(PAGE_ADDED: Page name: BlockEditor page color java.awt.Color[r=40,g=40,b=40] page width 4000.0 page drawer NewClass)
	1351142137935	Thu Oct 25 14:15:37 JST 2012	BLOCK_COMMAND_RECORD	JAVA_TO_BLOCK	Shell.java
	1351142139964	Thu Oct 25 14:15:39 JST 2012	BLOCK_COMMAND_RECORD	LOADING_START	Shell.java
	1351142140605	Thu Oct 25 14:15:40 JST 2012	BLOCK_COMMAND_RECORD	ANY	Shell.java	WorkspaceEvent(BLOCK_ADDED: Block 1: 進む with sockets: [Connector label: 歩数, Connector kind: number, blockID: -1 with pos type: SINGLE] and plug: null before: Connector label: , Connector kind: cmd, blockID: -1 with pos type: TOP after: Connector label: , Connector kind: cmd, blockID: -1 with pos type: BOTTOM)
	1351142140605	Thu Oct 25 14:15:40 JST 2012	BLOCK_COMMAND_RECORD	ANY	Shell.java	WorkspaceEvent(BLOCK_ADDED: Block 3: 戻る with sockets: [Connector label: 歩数, Connector kind: number, blockID: -1 with pos type: SINGLE] and plug: null before: Connector label: , Connector kind: cmd, blockID: -1 with pos type: TOP after: Connector label: , Connector kind: cmd, blockID: -1 with pos type: BOTTOM)
	 */

	/*
	 * このパターンはえぐい（ファイルがかわってしまう）
	1351142015427	Thu Oct 25 14:13:35 JST 2012	BLOCK_COMMAND_RECORD	LOADING_START	Box.java
	1351142016067	Thu Oct 25 14:13:36 JST 2012	BLOCK_COMMAND_RECORD	ANY	Box.java	WorkspaceEvent(BLOCK_ADDED: Block 1: 進む with sockets: [Connector label: 歩数, Connector kind: number, blockID: -1 with pos type: SINGLE] and plug: null before: Connector label: , Connector kind: cmd, blockID: -1 with pos type: TOP after: Connector label: , Connector kind: cmd, blockID: -1 with pos type: BOTTOM)
	1351142017814	Thu Oct 25 14:13:37 JST 2012	BLOCK_COMMAND_RECORD	ANY	Box.java	WorkspaceEvent(BLOCK_ADDED: Block 249: XXをYYする。 with sockets: [Connector label: , Connector kind: cmd, blockID: 250 with pos type: SINGLE] and plug: null before: Connector label: , Connector kind: cmd, blockID: 247 with pos type: TOP after: Connector label: , Connector kind: cmd, blockID: -1 with pos type: BOTTOM)
	．．． 
	1351142017814	Thu Oct 25 14:13:37 JST 2012	BLOCK_COMMAND_RECORD	ANY	Box.java	WorkspaceEvent(BLOCK_ADDED: Block 245: start with sockets: [Connector label: , Connector kind: poly, blockID: -1 with pos type: SINGLE] and plug: null before: null after: Connector label: , Connector kind: cmd, blockID: 247 with pos type: BOTTOM)
	1351142018157	Thu Oct 25 14:13:38 JST 2012	BLOCK_COMMAND_RECORD	ANY	Circle.java	WorkspaceEvent(PAGE_REMOVED: Page name:  page color java.awt.Color[r=40,g=40,b=40] page width 700.0 page drawer )
	1351142017861	Thu Oct 25 14:13:37 JST 2012	BLOCK_COMMAND_RECORD	LOADING_END	Circle.java
	 */

	public void separateForInvalidBlockEditorLog(List<PLLog> logs) {
		if (divided) {
			throw new RuntimeException();
		}
		boolean inLoading = false;
		for (PLLog log : logs) {
			if (!"BLOCK_COMMAND_RECORD".equals(log.getType())) {
				if (inLoading) {
					//System.out.println(log.getSubType());
					//System.out.println("ここにこない無いはずではあるのだけど．．"); //いや，くる．SAVEとかFOCUS_GAINEDとか．
					inLoading = false;
				}
				nonseparated.add(log);
				continue;
			}

			//			if (log.getSubType().startsWith("PAGE_")) {
			//				separated.add(log);
			//				continue;
			//			}

			if (!inLoading) {
				if ("JAVA_TO_BLOCK".equals(log.getSubType())) {
					inLoading = true;
				}
				if (((PLBlockCommandLog) log).getMessage()
						.indexOf("BLOCK_ADDED: Block 1:") != -1) {//いきなりブロックの初期化が始まった(1であることが重要)
					inLoading = true;
				}
			}

			//残すもの　BLOCK_, 命令と BLOCKS_命令, 
			//FOCUS_GAINED命令．<=こいつを入れてあげないとBlockEditor起動直後がカウントされない．<=その後操作しなければ見てただけって事になる．
			//BLOCK_TO_JAVA命令．<=こいつを入れないと, TextEditLogをはしょるのに困る
			//残さないもの　その他 PAGE_, FOCUS_, COMPILE, RUN, LOADING_, OPENEDなど
			if (log.getSubType().equals("FOCUS_GAINED")
					|| log.getSubType().equals("BLOCK_TO_JAVA")) {
				nonseparated.add(log);
			}
			if (!inLoading && log.getSubType().startsWith("BLOCK")
					|| log.getSubType().equals("FOCUS_LOST")) {
				nonseparated.add(log);
			} else {
				separated.add(log);
			}

			if (inLoading) {
				if ("LOADING_END".equals(log.getSubType())) {
					inLoading = false;
				}
				if ("OPENED".equals(log.getSubType())) {
					inLoading = false;
				}
			}
		}
		divided = true;
	}

	public void separateForNonOneTextEdit(List<PLLog> logs) {
		if (divided) {
			throw new RuntimeException();
		}
		boolean inFormatting = false;
		boolean inBtoJ = false;

		for (PLLog log : logs) {
			if (log.getSubType().equals("BLOCK_TO_JAVA")) {
				inBtoJ = true;
			}
			if (log.getSubType().equals("SAVE")) {
				inBtoJ = false;
				inFormatting = false;//念のため
			}
			if (log.getSubType().equals("START_FORMAT")) {
				inFormatting = true;
			}
			if (log.getSubType().equals("END_FORMAT")) {
				inFormatting = false;
			}
			if (!(log instanceof PLTextEditLog)) {
				nonseparated.add(log);
				continue;
			}

			if (inFormatting || inBtoJ) {
				separated.add(log);
				continue;
			}

			String text = ((PLTextEditLog) log).getText();
			if (!isTyping(text)) {
				separated.add(log);
				continue;
			}

			nonseparated.add(log);
		}
		divided = true;
	}

	public static boolean isTyping(String text) {
		return text.length() == 1 /*|| returnPattern.matcher(text).matches()*///0文字もはねる．（０文字挿入の削除が対象になってしまう．）
				|| japanesePattern.matcher(text).matches();//ASCII, 日本語入力は２文字以上になる
	}

	public static Pattern japanesePattern = Pattern
			.compile("[^a-zA-Z0-9()/{}]*");

	//public static Pattern returnPattern = Pattern.compile("^[\n\t ]*$");

	//public static Pattern pattern = Pattern
	//	.compile("^[a-zA-Z0-9!@#$%^*(),.<>~`{}\\/+=-]*$");//\r\tが含まれている

	public static void main(String[] args) {
		test("a", true);
		test("A", true);
		test("abc", false);
		test("ほげ", true);
		test("\n\t ", true);
		test(" ", true);
		test("abc   \n\t", false);
		test("abc\n\t", false);
		test("ほげabc", false);
		test("abcほげ", false);
		test("ほげ\n\t", true);//okにしよう
		test("ほげ()\n\t", false);
		test("ほげa\n\t", false);
	}

	public static void test(String text, boolean expected) {
		//System.out.println(text + " is japanese = "	+ japanesePattern.matcher(text).matches());
		System.out.println(isTyping(text) == expected);
	}
}
