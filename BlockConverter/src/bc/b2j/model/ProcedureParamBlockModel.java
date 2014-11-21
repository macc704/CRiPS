package bc.b2j.model;

import java.io.PrintStream;

public class ProcedureParamBlockModel extends DataBlockModel {

	private final String[] reservedWords = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
			"class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
			"float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
			"new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
			"switch", "synchrnized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while" };

	
	@Override
	public void print(PrintStream out, int indent) {

		if (getGenusName().equals("proc-param-number")) {
			out.print("int");
		} else if (getGenusName().equals("proc-param-double-number")) {
			out.print("double");
		} else if (getGenusName().equals("proc-param-string")) {
			out.print("String");
		} else if (getGenusName().equals("proc-param-boolean")) {
			out.print("boolean");
		} else {
			if (getJavaType() != null) {
				out.print(getJavaType());
			} else {
				out.print("Object");
			}
		}
		String label = getLabel();
		for (String reservedWord : reservedWords) {
			if (label.equals(reservedWord)) {
				throw new RuntimeException("変数" + label + "はJavaで予約語となっていますので使用できません。");
			}
		}

		out.print(" " + label);
	}

	public String getType() {

		if (getGenusName().equals("proc-param-number")) {
			return "int";
		} else if (getGenusName().equals("proc-param-double-number")) {
			return "double";
		} else if (getGenusName().equals("proc-param-string")) {
			return "String";
		} else if (getGenusName().equals("proc-param-boolean")) {
			return "boolean";
		} else {
			if (getJavaType() != null) {
				return getJavaType();
			} else {
				return "Object";
			}
		}
	}

}
