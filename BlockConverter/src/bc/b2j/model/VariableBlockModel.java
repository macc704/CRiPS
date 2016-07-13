package bc.b2j.model;

public abstract class VariableBlockModel extends BlockModel {

	private final String[] reservedWords = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
			"class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
			"float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
			"new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
			"switch", "synchrnized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while" };

	@Override
	public void setLabel(String label) {
		for (String reservedWord : reservedWords) {
			if (label.equals(reservedWord)) {
				throw new RuntimeException("変数" + label + "はJavaで予約語となっていますので使用できません。");
			}
		}
		super.setLabel(label);
	}

	@Override
	public void setType(String type) {
		super.setType(type.substring(type.indexOf(" "), type.length()));
	}

	// 今は使っていません。 2011/11/20
	protected String resolveVariableType(String name) {
		if (getType() != null) {
			return getType();
		}

		if (name.endsWith("number")) {
			return "int";
		} else if (name.endsWith("string")) {
			return "String";
		} else if (name.endsWith("boolean")) {
			return "boolean";
		}

		return "";
	}
}
