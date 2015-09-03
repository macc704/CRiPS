package util;

public class StringUtil {

	private static final String TAB = "_TAB";
	private static final String BREAK = "_BREAK";
	private static final String SPACE = "_SPACE";
	private static final String HSPACE = "_HSPACE"; // 半角スペース

	private static int DIFFERENCE = 'Ａ' - 'A';

	public static String convertToSign(String text) {
		String str = new String(text);
		str = str.replaceAll("\t", TAB);
		str = str.replaceAll("\r\n", BREAK);
		str = str.replaceAll("\r", BREAK);
		str = str.replaceAll("\n", BREAK);
		str = str.replaceAll("　", SPACE);
		str = str.replaceAll(" ", HSPACE);
		return str;
	}

	public static String convertToNonSign(String text) {
		String str = new String(text);
		str = str.replaceAll(TAB, "\t");
		str = str.replaceAll(BREAK, "\r\n");
		str = str.replaceAll(BREAK, "\n");
		str = str.replaceAll(BREAK, "\r");
		str = str.replaceAll(SPACE, "　");
		str = str.replaceAll(HSPACE, " ");
		return str;
	}

	public static String deleteNonChar(String text) {
		String str = new String(text);
		str = str.replace("\n", "");
		str = str.replace("\r", "");
		str = str.replace("\t", "");
		str = str.replace(" ", "");
		str = str.replace("　", "");
		return str;
	}

	public static boolean isEmpty(String text) {

		if (deleteNonChar(text).equals("")) {
			return true;
		} else {
			return false;
		}

	}

	public static String replaceBreakToHTML(String text) {
		String[] line = text.split("\n");
		String str = "<html>";
		for (int i = 0; i < line.length; i++) {
			str = str + line[i] + "<br>";
		}
		str = str + "</html>";
		return str;
	}

	/**
	 * 文字列に含まれる全角文字を半角文字にします
	 * 
	 * @param text
	 * @return
	 */
	static public String convertToHalfSize(String text) {
		char[] cArray = text.toCharArray();
		StringBuilder sb = new StringBuilder();

		for (char c : cArray) {
			char newChar = c;
			if (('Ａ' <= c && c <= 'Ｚ') || ('ａ' <= c && c <= 'ｚ')
					|| ('０' <= c && c <= '９')) {
				newChar = (char) (c - DIFFERENCE);
			}
			sb.append(newChar);
		}

		return sb.toString();
	}
}
