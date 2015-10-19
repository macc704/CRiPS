package bc.classblockfilewriters.model;

import java.io.PrintStream;

public class ObjectArrayBlockModel extends ObjectBlockModel {

	public ObjectArrayBlockModel(String name, String kind, String initialLabel, String headerLabel, String footerLabel, String color) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);
	}

	public void print(PrintStream out, int lineNumber) throws Exception {
		out.println("<BlockGenus" + " " + "name=" + "\"" + getGenusName() + "\" " + "kind=" + "\"" + getKind() + "\" " + "initlabel=" + "\"" + getInitialLabel() + "\"");
		makeIndent(out, ++lineNumber);
		out.println(" header-label=" + "\"" + getHeaderLabel() + "\" " + "footer-label=" + "\"" + getFooterLabel() + "\" " + "editable-label=\"yes\" " + "label-unique=\"yes\" " + "color=\"" + getColor() + "\">");

		makeIndent(out, lineNumber);
		out.println("<description>");

		makeIndent(out, ++lineNumber);
		out.println("<text>");

		makeIndent(out, ++lineNumber);
		out.println("disctiption");

		makeIndent(out, --lineNumber);
		out.println("</text>");

		makeIndent(out, --lineNumber);
		out.println("</description>");

		// コンストラクタを設定
		if (className.contains("[]")) {
			printBlockConnectors(out, lineNumber + 1, "socket", "object", "new-arrayobject-objectarray", getClassName());

			printStubs(out, lineNumber);

			makeIndent(out, lineNumber);
			out.println("<LangSpecProperties>");

			for (String langSpecProperty : langSpecProperties.keySet()) {
				printLangSpecProperty(out, lineNumber + 1, langSpecProperty, langSpecProperties.get(langSpecProperty));
			}

			makeIndent(out, lineNumber);
			out.println("</LangSpecProperties>");

			makeIndent(out, lineNumber);
			out.println("<JavaType>" + getClassName() + "</JavaType>");

			out.println("</BlockGenus>");
			out.println();

			out.println();
		}
	}

	protected void printStubs(PrintStream out, int lineNumber) {
		makeIndent(out, lineNumber);
		out.println("<Stubs>");
		printAllStubs(out, lineNumber);

		printStub("<Stub stub-genus=\"setter-arrayelement\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-set" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);
		printStub("<Stub stub-genus=\"getter-arrayelement\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-set" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);

		makeIndent(out, lineNumber);
		out.println("</Stubs>");
	}

}
