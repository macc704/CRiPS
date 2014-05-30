package pres.loader.logmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pres.core.model.PRTextEditLog;
import pres.core.text.PRTextEscaper;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.system.CEncoding;
import clib.common.system.CEncodingDetector;

/**
 * Presデータをロードする。
 * 
 * @author okadaken
 * 
 */

public class PLLogReader {

	/**
	 * ファイルをロードし、 TPRLog に変換する。
	 * 
	 * @param logFile
	 *            対象となるファイル
	 * @return
	 */
	public static List<PLLog> readPresLogFile(File logFile) {
		PLLogReader reader = new PLLogReader();
		CFile file = CFileSystem.findFile(logFile.getAbsolutePath());
		return reader.readPresLogFile(file);
	}

	public List<PLLog> readPresLogFile(CFile file) {
		try {
			file.setEncodingIn(CEncoding.JISAutoDetect);
			if (CEncoding.UTF8 == CEncodingDetector.detect(file.toJavaFile())) {
				file.setEncodingIn(CEncoding.UTF8);
			}

			List<String> lines = file.loadTextAsList();
			ArrayList<PLLog> logs = new ArrayList<PLLog>();
			int len = lines.size();
			for (int i = 0; i < len; i++) {
				try {
					String line = lines.get(i);
					logs.add(readOneLine(line));
				} catch (PLLogLineStatusException ex) {
					String msg = "error in "
							+ file.getRelativePathFromExecuteDirectory()
							+ " at line " + (i + 1);
					System.err.println(msg + " " + ex.getMessage());
					//continue;
				} catch (NumberFormatException ex) {
					//無効な行 bug #04
					String msg = "error in "
							+ file.getRelativePathFromExecuteDirectory()
							+ " at line " + (i + 1);
					System.err.println(msg + " " + ex.getMessage());
					continue;
				} catch (Exception ex) {
					throw new RuntimeException();
				}
			}
			return logs;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 検査用メソッド。 readLog() を実行中に、何らかの理由で解析し損ねたデータが残っていないかを検査する。
	 * 
	 * @param time
	 * @param tokenizer
	 * @param commandName
	 */
	private void checkLineStatus(long time, StringTokenizer tokenizer,
			String commandName) throws PLLogLineStatusException {
		if (tokenizer.hasMoreTokens()) {
			StringBuffer buf = new StringBuffer();
			buf.append("LOG LINE STATUS WARNING(" + time + "): " + commandName);
			while (tokenizer.hasMoreElements()) {
				buf.append("\t" + tokenizer.nextToken());
			}
			throw new PLLogLineStatusException(buf.toString());
		}
	}

	/**
	 * 1行のログデータを解析して、適切な TPRLog に変換する。
	 * 
	 * @param line
	 * @return
	 */
	@SuppressWarnings("unused")
	private PLLog readOneLine(String line) throws PLLogLineStatusException {

		StringTokenizer tokenizer = new StringTokenizer(line, "\t");

		if (tokenizer.countTokens() < 4) {
			throw new PLLogLineStatusException("No enough tokens count="
					+ tokenizer.countTokens());
		}

		long time = Long.parseLong(tokenizer.nextToken());
		tokenizer.nextToken(); // timeがあれば復元できるため、この日付の文字列表現は捨てる。

		String typeString = tokenizer.nextToken();
		String subTypeString = tokenizer.nextToken();

		if (typeString.equals("COMMAND_RECORD")) {
			String path = tokenizer.nextToken();
			String message = null;
			if (tokenizer.hasMoreTokens()) {
				message = tokenizer.nextToken();
			}

			checkLineStatus(time, tokenizer, typeString);

			if (message == null) {
				return new PLCommandLog(time, subTypeString, new CPath(path));
			} else {
				return new PLCommandLog(time, subTypeString, new CPath(path),
						message);
			}
		} else if (typeString.equals("LOG")) {

			String message = tokenizer.nextToken();

			checkLineStatus(time, tokenizer, typeString);

			return new PLLogLog(time, subTypeString, message);
		} else if (typeString.equals("PROJECT_RECORD")) {

			String message = null;
			if (tokenizer.hasMoreTokens()) {
				tokenizer.nextToken();
			}

			checkLineStatus(time, tokenizer, typeString);

			if (message == null) {
				return new PLProjectLog(time, subTypeString);
			} else {
				return new PLProjectLog(time, subTypeString, message);
			}
		} else if (typeString.equals("TEXTEDIT_RECORD")) {

			String path = tokenizer.nextToken();
			int offset = Integer.parseInt(tokenizer.nextToken());
			int length = Integer.parseInt(tokenizer.nextToken());

			String text = "";
			if (tokenizer.hasMoreTokens()) {
				text = PRTextEscaper.unEscape(tokenizer.nextToken());
			}

			checkLineStatus(time, tokenizer, typeString);

			// 20120508 matsuzawa 追加
			// 20130207 なぜだかわかんねーが，DELETEのタイプがECLIPSEになっちゃってる．
			if (subTypeString.equals(PRTextEditLog.SubType.INSERT.toString())) {
				length = 0;
				subTypeString = PRTextEditLog.SubType.ECLIPSE.toString();
			} else if (subTypeString.equals(PRTextEditLog.SubType.DELETE
					.toString())) {
				text = "";
				subTypeString = PRTextEditLog.SubType.ECLIPSE.toString();
			}
			return new PLTextEditLog(time, subTypeString, new CPath(path),
					offset, length, text);
		} else if (typeString.equals("TEXTEDIT_RECORD_ECLIPSE")) {

			String targetPath = tokenizer.nextToken();
			int start = Integer.parseInt(tokenizer.nextToken());
			int end = Integer.parseInt(tokenizer.nextToken());
			String text = null;
			String preserveText = null;

			if (tokenizer.hasMoreElements()) {
				text = PRTextEscaper.unEscape(tokenizer.nextToken());
			}
			if (tokenizer.hasMoreElements()) {
				preserveText = PRTextEscaper.unEscape(tokenizer.nextToken());
			}
			checkLineStatus(time, tokenizer, typeString);

			// 20120508 matsuzawa 変更
			// 未変更

			return new PLEclipseTextEditLog(time, subTypeString, new CPath(
					targetPath), start, end, text, preserveText);

		} else if (typeString.equals("BLOCK_COMMAND_RECORD")) {

			//TODO H24.1.21 保井追加
			//	   H24.10.11 榊原修正
			//BlockEditorのログを取得する
			//			String token = tokenizer.nextToken();
			String path = tokenizer.nextToken();
			String message = "";

			//BlockEditorのログのメッセージからBlockEditorのコマンドを取得する
			String subType = "";
			if (subTypeString.equals("ANY")) {
				message = tokenizer.nextToken();
				subType = message.substring(message.indexOf('(') + 1,
						message.indexOf(':'));
			} else {
				subType = subTypeString;
			}

			PLBlockCommandLog blocklog = new PLBlockCommandLog(time, subType,
					new CPath(path));
			blocklog.setMessage(message);
			return blocklog;

		} else if (typeString.equals("COCOVIEWER_RECORD")) {
			String path = tokenizer.nextToken();

			// checkLineStatus(time, tokenizer, typeString);
			PLFileLog log = new PLFileLog(time, typeString, subTypeString,
					new CPath(path)) {

				@Override
				public String getExplanationPhrase() {
					// TODO Auto-generated method stub
					return null;
				}
			};

			return log;

		} else {
			throw new RuntimeException("Unknown Log Type: " + typeString);
		}
	}

	/**
	 * テストプログラム
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			List<PLLog> logs = readPresLogFile(new File("doc\\pres2.log"));

			for (int i = 0; i < 100; i++) {
				PLLog log = logs.get(i);
				System.out.println(log);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
