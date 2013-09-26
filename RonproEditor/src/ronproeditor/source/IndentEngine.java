package ronproeditor.source;

import java.util.Scanner;

import ronproeditor.helpers.FileSystemUtil;

/**
 * 　ソースコード整形をするエンジンです。
 */
public class IndentEngine {

	/**
	 * 　一行ずつ読み、スキャンしていきます。
	 */
	public static String execIndent(String before) {

		// 2011/11/20追記 macchan
		while (before.contains("}}")) {
			before = before.replace("}}", "}\n}");
		}
		while (before.contains("{{")) {
			before = before.replace("{{", "{\n{");
		}

		Scanner scanner = new Scanner(before);
		StringBuffer buf = new StringBuffer();

		try {
			int braceCount = 0;// 現在のタブの数を覚えておく
			boolean inComment = false;// コメント内かどうか(状態)
			while (scanner.hasNext()) {

				// 一行とってくる
				String lineString = scanner.nextLine();

				// スキャンする
				ScanResult sr = IndentUtil.scanLine(lineString, inComment);
				inComment = sr.getInComment();

				// 減る場合先に減らす
				if (sr.getCloseBraceCount() > 0) {
					braceCount = braceCount - sr.getCloseBraceCount();
				}

				// スキャンした行のインデント合わせをする
				StringBuffer linebuf = new StringBuffer(lineString);
				IndentUtil.doIndentLine(braceCount + sr.getCloseSecond(),
						linebuf);
				buf.append(linebuf.toString());
				if (scanner.hasNext()) {
					buf.append(FileSystemUtil.CR);
				}

				// 増える場合あとに増やす
				if (sr.getOpenBraceCount() > 0) {
					braceCount = braceCount + sr.getOpenBraceCount();
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		scanner.close();
		return buf.toString();
	}

}
