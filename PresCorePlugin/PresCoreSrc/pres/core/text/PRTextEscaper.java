package pres.core.text;

/**
 * CStringEscaper
 */
public class PRTextEscaper {

	private PRTextEscaper() {
	}

	public static String escape(Object o) {
		if (o == null) {
			return "null";
		}
		String text = o.toString();
		text = text.replaceAll("[\\\\]", "\\\\\\\\");
		text = text.replaceAll("\t", "\\\\t");
		text = text.replaceAll("\n", "\\\\n");
		text = text.replaceAll("\r", "\\\\r");
		return text;
	}

	public static String unEscape(String text) {
		if (text == null) {
			return "null";
		}
		text = text.replaceAll("[\\\\]r", "\r");
		text = text.replaceAll("[\\\\]n", "\n");
		text = text.replaceAll("[\\\\]t", "\t");
		text = text.replaceAll("[\\\\][\\\\]", "\\\\");
		return text;
	}

	public static void main(String[] args) {
		test("\tline1\r\n\tline2");
		test("\tline1\r\n\tline2\\\"");
	}

	private static void test(String original) {
		String escaped = escape(original);
		String processed = unEscape(escaped);
		boolean result = original.equals(processed);
		System.out.println("test:" + result);
		System.out.println("original:");
		System.out.println(original);
		System.out.println("escaped:");
		System.out.println(escaped);
		System.out.println("processed:");
		System.out.println(processed);
	}
}