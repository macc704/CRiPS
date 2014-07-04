package ronproeditor.source;

/**
 *　コードを整形するための便利メソッドを持っているクラスです。
 */
public class IndentUtil {

	//	/**
	//	 * その行の現在のインデントの数を数えるメソッドです。
	//	 */
	//	public static int countIndentDepth(String lineString) {
	//
	//		char[] lineChar = lineString.toCharArray();
	//		int countIndent = 0;
	//
	//		//タブもしくはスペースの数を数える
	//		for (int j = 0; j < lineChar.length; j++) {
	//			if (lineChar[j] == ' ') {
	//				countIndent++;
	//			} else if (lineChar[j] == '\t') {
	//				countIndent += REApplication.WHITESPACE_COUNT_FOR_TAB * 2;
	//			} else {
	//				return countIndent;
	//			}
	//		}
	//
	//		return countIndent;
	//	}

	/**
	 * 一行を正しいインデントにするメソッドです。
	 */
	public static void doIndentLine(int depth, StringBuffer line) {
		int pos = getStartPoint(line);
		String lineString = line.toString();
		if (pos <= line.length()) {
			lineString = lineString.substring(pos);
		}
		if (line.length() > 0) {
			line.delete(0, line.length());
		}
		for (int j = 0; j < depth; j++) {
			line.append('\t');
		}
		line.append(lineString);

		//		int beforeIndentCount = countIndentDepth(lineString);
		//		int wishIndentCount = depth * REApplication.WHITESPACE_COUNT_FOR_TAB
		//				* 2;
		//		//あってるか調べてる
		//		int insertSpace = wishIndentCount - beforeIndentCount;
		//		if (insertSpace == 0) {//ちょうどいい
		//			return;
		//		} else if (insertSpace > 0) {//すくないからたす
		//			String insertString = "";
		//			for (int i = 0; i < insertSpace; i++) {
		//				insertString = insertString + " ";
		//			}
		//			line.insert(0, insertString);
		//		} else {//おおいから削る
		//			//ただし！マイナスになると困るので小さいほうをとる
		//			insertSpace = insertSpace * -1;
		//			insertSpace = (insertSpace < beforeIndentCount) ? insertSpace
		//					: beforeIndentCount;
		//			for (int i = 0; i < insertSpace; i++) {
		//				line.deleteCharAt(0);
		//			}
		//		}
	}

	private static int getStartPoint(StringBuffer line) {
		int i;
		for (i = 0; i < line.length(); i++) {
			switch (line.charAt(i)) {
			case ' ':
			case '　':
			case '\t':
				continue;
			default:
				return i;
			}
		}
		return i;
	}

	/**
	 * 一行スキャンするメソッドです。
	 */
	public static ScanResult scanLine(String s, boolean inComment) {
		ScanResult sr = new ScanResult();
		boolean inSingleQuote = false;
		boolean inDoubleQuote = false;
		char lastChar = '\0';
		char[] line = s.toCharArray();
		int len = line.length;

		for (int i = 0; i < len; i++) {
			if (line[i] == '}' && inSingleQuote == false
					&& inDoubleQuote == false && inComment == false) {
				sr.addCloseBraceCount();
			} else if (line[i] == '{' && inSingleQuote == false
					&& inDoubleQuote == false && inComment == false) {
				sr.addOpenBraceCount();
			} else if (line[i] == '/' && lastChar == '/'
					&& inDoubleQuote == false && inComment == false) {//行コメント
				sr.setInComment(inComment);
				return sr;
			} else if (line[i] == '\'' && lastChar != '\\'
					&& inDoubleQuote == false && inComment == false) {//charリテラル
				inSingleQuote = !inSingleQuote;
			} else if (line[i] == '"' && lastChar != '\\'
					&& inSingleQuote == false && inComment == false) {//文字列リテラル
				inDoubleQuote = !inDoubleQuote;
			} else if (line[i] == '*' && lastChar == '/'
					&& inDoubleQuote == false && inComment == false) {//コメント開くとき
				inComment = !inComment;
				lastChar = '\0';
				continue;
			} else if (line[i] == '/' && lastChar == '*'
					&& inDoubleQuote == false && inComment == true) {//コメント閉じるとき
				inComment = !inComment;
				lastChar = '\0';
				continue;
			} else if (line[i] == '\\' && lastChar == '\\'
					&& (inSingleQuote == true || inDoubleQuote == true)) {//\\は一つとみなす
				lastChar = '\0';
				continue;
			}
			lastChar = line[i];
		}
		sr.setInComment(inComment);
		return sr;
	}
}