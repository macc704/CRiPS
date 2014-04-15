package bc.j2b.analyzer;

import java.io.File;

import bc.BCSystem;
import bc.utils.FileReader;

/**
 * @author Administrator
 */
public class JavaCommentManager {

	private String source;
	private String sourceName;

	public JavaCommentManager(File file, String enc) {
		source = FileReader.readFile(file, enc);
		sourceName = file.getName().substring(0,
				file.getName().indexOf(".java"));
	}

	public String getSourceName() {
		return this.sourceName;
	}

	/**
	 * visitorに実装する
	 * 
	 * @param position
	 * @param abstractBlocks
	 */
	public String getLineComment(int position) {
		if (position < 0) {
			return "";
		}
		try {
			int start = position + 2; // 比較する文字のポジション
			int end = start;
			int last = source.length();
			while (end < last) {
				if (source.charAt(end) == '\r' || source.charAt(end) == '\n') {
					break;
				}
				end++;
			}
			String comment = source.substring(start, end);
			return comment;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "ERROR@#getLineComment()";
		}
	}

	/**
	 * ラインコメントの場所を獲得する position以降の文字列から改行までを解析 ラインコメントがあれば、ラインコメントのはじめの"/"の場所を返す
	 * それ以外は-1
	 * 
	 * @param position
	 */
	public int getLineCommentPosition(int position) {
		// #ohata added
		for (int i = 0; position + i < source.length(); i++) {
			if (source.charAt(position + i) == '/'
					&& source.charAt(position + i + 1) == '/') {
				BCSystem.out.println("position:" + position);
				return position + i;
			} else if (source.charAt(position + i) == '\r'
					|| source.charAt(position + i) == '\n') {
				return -1;
			}
		}
		return -1;
	}

	// #ohata added
	public int getLineCommentEndPosition(int position) {
		int end = getLineCommentPosition(position);
		int i = 0;
		if (end != -1) {
			while (source.charAt(i + end) != '\r'
					|| source.charAt(i + end) != '\n') {
				i++;
			}
			end = end + i + 2;
		} else {
			end = position;
		}
		return end;
	}

	public String getBlockComment(int position) {
		try {
			int endCommentPos = position + 2;
			while (source.charAt(endCommentPos) != '*'
					&& source.charAt(endCommentPos + 1) != '/') {
				endCommentPos++;
			}
			String comment = source.substring(position + 2, endCommentPos);
			return comment;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "ERROR@#getBlockComment()";
		}
	}
}
