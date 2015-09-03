/*
 * MatcherUtil.java
 * Created on 2007/09/21 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.helpers;

/**
 * MatcherUtil
 */
public class MatcherUtil {

	public static String replaceAll(String source, String regex,
			String replacement) {
		return source
				.replaceAll(escapeString(regex), escapeString(replacement));
	}

	public static String escapeString(String regex) {
		StringBuffer buf = new StringBuffer();
		for (char c : regex.toCharArray()) {
			if (c == '$' || c == '\\' || c == '*' || c == '+') {
				buf.append('\\');
			}
			buf.append(c);
		}
		return buf.toString();
	}
}
