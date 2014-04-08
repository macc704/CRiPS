package util;

public class StringUtil {

	private static final String TAB = "_TAB";
	private static final String BREAK = "_BREAK";
	private static final String SPACE = "_SPACE";
	private static final String HSPACE = "_HSPACE"; // îºäpÉXÉyÅ[ÉX

	private static int DIFFERENCE = 'Ç`' - 'A';

	public static String convertToSign(String text) {
		String str = new String(text);
		str = str.replaceAll("\t", TAB);
		str = str.replaceAll("\r\n", BREAK);
		str = str.replaceAll("\r", BREAK);
		str = str.replaceAll("\n", BREAK);
		str = str.replaceAll("Å@", SPACE);
		str = str.replaceAll(" ", HSPACE);
		return str;
	}

	public static String convertToNonSign(String text) {
		String str = new String(text);
		str = str.replaceAll(TAB, "\t");
		str = str.replaceAll(BREAK, "\r\n");
		str = str.replaceAll(BREAK, "\n");
		str = str.replaceAll(BREAK, "\r");
		str = str.replaceAll(SPACE, "Å@");
		str = str.replaceAll(HSPACE, " ");
		return str;
	}

	public static String deleteNonChar(String text) {
		String str = new String(text);
		str = str.replace("\n", "");
		str = str.replace("\r", "");
		str = str.replace("\t", "");
		str = str.replace(" ", "");
		str = str.replace("Å@", "");
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
	 * ï∂éöóÒÇ…ä‹Ç‹ÇÍÇÈëSäpï∂éöÇîºäpï∂éöÇ…ÇµÇ‹Ç∑
	 * 
	 * @param text
	 * @return
	 */
	static public String convertToHalfSize(String text) {
		char[] cArray = text.toCharArray();
		StringBuilder sb = new StringBuilder();

		for (char c : cArray) {
			char newChar = c;
			if (('Ç`' <= c && c <= 'Çy') || ('ÇÅ' <= c && c <= 'Çö')
					|| ('ÇO' <= c && c <= 'ÇX')) {
				newChar = (char) (c - DIFFERENCE);
			}
			sb.append(newChar);
		}

		return sb.toString();
	}
}
