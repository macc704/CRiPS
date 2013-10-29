package ClassBlockFileModel;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bc.apps.ProjectLangDefMenuPrinter;

public class SelDefClassModel extends BasicModel {

	private List<MethodBlockModel> methods = new ArrayList<MethodBlockModel>();
	private String className;
	private Map<String, String> langSpecProperties = new LinkedHashMap<String, String>();
	private ProjectLangDefMenuPrinter menuPrinter = new ProjectLangDefMenuPrinter();

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

	public void print(PrintStream out, int lineNumber, PrintStream menuOut)
			throws Exception {
		lineNumber += 1;
		makeIndent(out, lineNumber);
		out.println("<BlockGenus" + " " + "name=" + "\"" + getName() + "\" "
				+ "kind=" + "\"" + getKind() + "\" " + "initlabel=" + "\""
				+ getInitialLabel() + "\"");
		makeIndent(out, lineNumber + 1);
		out.println(" header-Label=" + "\"" + getHeaderLabel() + "\" "
				+ "footer-Label=" + "\"" + getFooterLabel() + "\" "
				+ "editable-label=\"yes\" " + "label-unique=\"yes\" "
				+ "color=\"" + getColor() + "\">");

		makeIndent(out, lineNumber + 1);
		out.println("<description>");

		makeIndent(out, lineNumber + 2);
		out.println("<text>");

		makeIndent(out, lineNumber + 2);
		out.println("</text>");

		makeIndent(out, lineNumber + 1);
		out.println("</description>");

		printBlockConnectors(out, lineNumber, "socket", "object", "new-object",
				getClassName());

		makeIndent(out, lineNumber + 1);
		out.println("<Stubs>");

		makeIndent(out, lineNumber + 1);
		out.println("</Stubs>");

		makeIndent(out, lineNumber + 1);
		out.println("<LangSpecProperties>");

		for (String langSpecProperty : langSpecProperties.keySet()) {
			printLangSpecProperty(out, lineNumber + 2, langSpecProperty,
					langSpecProperties.get(langSpecProperty));
		}

		makeIndent(out, lineNumber + 1);
		out.println("</LangSpecProperties>");

		makeIndent(out, lineNumber);
		out.println("</BlockGenus>");

		// menuPrinter.print(menuOut, lineNumber + 4, getName());
	}

	private void printBlockConnectors(PrintStream out, int lineNumber,
			String connectorKind, String connectorType,
			String defaultGenusName, String DefaultLabel) {
		makeIndent(out, lineNumber + 1);
		out.println("<BlockConnectors>");

		makeIndent(out, lineNumber + 2);
		out.println("<BlockConnector label=\"‰Šú’l\" connector-kind=\""
				+ connectorKind + "\" connector-type = \"" + connectorType
				+ "\">");

		makeIndent(out, lineNumber + 3);
		out.println("<DefaultArg genus-name=\"" + defaultGenusName
				+ "\" label=\"" + DefaultLabel + "\"></DefaultArg>");

		makeIndent(out, lineNumber + 2);
		out.println("</BlockConnector>");

		makeIndent(out, lineNumber + 1);
		out.println("</BlockConnectors>");
	}

	private void printLangSpecProperty(PrintStream out, int lineNumber,
			String key, String value) {
		makeIndent(out, lineNumber);
		out.println("<LangSpecProperty key=\"" + key + "\" value=\"" + value
				+ "\"></LangSpecProperty>");
	}
}
