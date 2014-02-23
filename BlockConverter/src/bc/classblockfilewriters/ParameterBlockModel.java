package bc.classblockfilewriters;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ParameterBlockModel extends BasicModel {

	private String javaType;
	private Map<String, String> langSpecProperties = new HashMap<String, String>();
	private Map<String, List<PublicMethodInfo>> methods;

	public ParameterBlockModel(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color,
			String javaType) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);
		HashSet<String> connector = new HashSet<String>();
		connector.add("object");
		addConnector("plug", connector);
		langSpecProperties.put("type", "object");
		langSpecProperties.put("stack-type", "breed-procedure");

		this.javaType = javaType;
	}

	public void setMethods(Map<String, List<PublicMethodInfo>> methods) {
		this.methods = methods;
	}

	public void print(PrintStream out, int lineNumber) throws Exception {

		out.println("<BlockGenus" + " " + "name=" + "\"" + getName() + "\" "
				+ "kind=" + "\"" + getKind() + "\" " + "initlabel=" + "\""
				+ getInitialLabel() + "\"" + " editable-label=\"yes\" "
				+ "is-starter=\"yes\" is-terminator=\"yes\"" + " color=\""
				+ getColor() + "\">");
		lineNumber++;
		printBlockConnectors(out, lineNumber);
		makeIndent(out, lineNumber);
		out.println("<JavaType>" + addEscapeSequence(javaType) + "</JavaType>");

		printStubs(out, lineNumber);
		makeIndent(out, lineNumber);
		out.println("<LangSpecProperties>");

		for (String langSpecProperty : langSpecProperties.keySet()) {
			printLangSpecProperty(out, lineNumber + 1, langSpecProperty,
					langSpecProperties.get(langSpecProperty));
		}

		makeIndent(out, lineNumber);
		out.println("</LangSpecProperties>");

		if (methods != null) {
			for (String key : methods.keySet()) {
				makeIndent(out, lineNumber);
				out.println("<ClassMethods class=\"" + key + "\">");

				lineNumber++;
				for (PublicMethodInfo method : methods.get(key)) {
					method.print(out, lineNumber);
				}

				makeIndent(out, --lineNumber);
				out.println("</ClassMethods>");
			}
		}

		out.println("</BlockGenus>");
	}

	private void printStubs(PrintStream out, int lineNumber) {
		String scope = "local";

		makeIndent(out, lineNumber);
		out.println("<Stubs>");

		printStubs("<Stub stub-genus=\"getter\">",
				"<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + scope
						+ "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"scope\" value=\"" + scope
						+ "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"stack-type\" value=\""
						+ "breed-procedure" + "\"></LangSpecProperty>", out,
				lineNumber);

		printStub("<Stub stub-genus=\"setter\">",
				"<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-set"
						+ scope + "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"scope\" value=\"" + scope
						+ "\"></LangSpecProperty>", out, lineNumber);

		makeIndent(out, lineNumber);
		out.println("</Stubs>");
	}

}
