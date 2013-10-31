package ClassBlockFileModel;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SelDefClassModel extends BasicModel {

	private List<MethodBlockModel> methods = new ArrayList<MethodBlockModel>();
	private String className;
	private Map<String, String> langSpecProperties = new LinkedHashMap<String, String>();

	public SelDefClassModel(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);
		langSpecProperties.put("scope", "local");
		langSpecProperties.put("type", "object");
		langSpecProperties.put("is-owned-by-breed", "yes");
		langSpecProperties.put("is-monitorable", "yes");
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return this.className;
	}

	public void addMethod(MethodBlockModel method) {
		methods.add(method);
	}

	public void print(PrintStream out, int lineNumber) throws Exception {
		out.println("<BlockGenus" + " " + "name=" + "\"" + getName() + "\" "
				+ "kind=" + "\"" + getKind() + "\" " + "initlabel=" + "\""
				+ getInitialLabel() + "\"");
		makeIndent(out, ++lineNumber);
		out.println(" header-Label=" + "\"" + getHeaderLabel() + "\" "
				+ "footer-Label=" + "\"" + getFooterLabel() + "\" "
				+ "editable-label=\"yes\" " + "label-unique=\"yes\" "
				+ "color=\"" + getColor() + "\">");

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

		printBlockConnectors(out, lineNumber + 1, "socket", "object",
				"new-object", getClassName());

		makeIndent(out, lineNumber);
		out.println("<Stubs>");

		makeIndent(out, lineNumber);
		out.println("</Stubs>");

		makeIndent(out, lineNumber);
		out.println("<LangSpecProperties>");

		for (String langSpecProperty : langSpecProperties.keySet()) {
			printLangSpecProperty(out, lineNumber + 1, langSpecProperty,
					langSpecProperties.get(langSpecProperty));
		}

		makeIndent(out, lineNumber);
		out.println("</LangSpecProperties>");

		out.println("</BlockGenus>");
		out.println();
	}

	private void printBlockConnectors(PrintStream out, int lineNumber,
			String connectorKind, String connectorType,
			String defaultGenusName, String DefaultLabel) {
		makeIndent(out, lineNumber);
		out.println("<BlockConnectors>");

		makeIndent(out, ++lineNumber);
		out.println("<BlockConnector label=\"‰Šú’l\" connector-kind=\""
				+ connectorKind + "\" connector-type=\"" + connectorType
				+ "\">");

		makeIndent(out, ++lineNumber);
		out.println("<DefaultArg genus-name=\"" + defaultGenusName
				+ "\" label=\"" + DefaultLabel + "\"></DefaultArg>");

		makeIndent(out, --lineNumber);
		out.println("</BlockConnector>");

		makeIndent(out, --lineNumber);
		out.println("</BlockConnectors>");
	}

	private void printLangSpecProperty(PrintStream out, int lineNumber,
			String key, String value) {
		makeIndent(out, lineNumber);
		out.println("<LangSpecProperty key=\"" + key + "\" value=\"" + value
				+ "\"></LangSpecProperty>");
	}

	public void printMenuItem(PrintStream out, int lineNumber) {
		makeIndent(out, lineNumber);
		out.println("<BlockGenusMember>" + getName() + "</BlockGenusMember>");
	}

}
