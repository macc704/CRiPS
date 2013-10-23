package ClassBlockFileModel;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SelDefClassModel extends BasicModel {

	private List<MethodBlockModel> methods = new ArrayList<MethodBlockModel>();
	private String className;

	public SelDefClassModel(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);
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

	public void print(PrintStream out, int lineNumber) {

		out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		out.println("<BlockGenus>" + " " + "name=" + "\"" + getName() + "\" "
				+ "kind =" + "\"" + getKind() + "\" " + "initialLabel =" + "\""
				+ getInitialLabel() + "\" " + "headerLabel =" + "\""
				+ getHeaderLabel() + "\" " + "footerLabel =" + "\""
				+ getFooterLabel() + "\" ");

		makeIndent(out, lineNumber + 1);
		out.println("<description>");

		makeIndent(out, lineNumber + 2);
		out.println("<text>");

		makeIndent(out, lineNumber + 3);
		out.println("description about class");

		makeIndent(out, lineNumber + 2);
		out.println("</text>");

		makeIndent(out, lineNumber + 1);
		out.println("</description>");

		makeIndent(out, lineNumber + 1);
		out.println("<BlockConnectors>");

		makeIndent(out, lineNumber + 2);
		out.println("<BlockConnector label=\"‰Šú’l\" connector-kind=\"socket\" connector-type = \"object\">");

		makeIndent(out, lineNumber + 3);
		out.println("<DefaultArg genus-name=\"new-object\" label=\""
				+ getClassName() + "\"></DefaultArg>");

		makeIndent(out, lineNumber + 2);
		out.println("</BlockConnector>");

		makeIndent(out, lineNumber + 1);
		out.println("</BlockConnectors>");

		makeIndent(out, lineNumber + 1);
		out.println("<Stubs>");

		makeIndent(out, lineNumber + 1);
		out.println("</Stubs>");

		makeIndent(out, lineNumber + 1);
		out.println("<LangSpecProperties>");

		makeIndent(out, lineNumber + 1);
		out.println("</LangSpecProperties>");

		out.println("</BlockGenus>");

		for (MethodBlockModel method : methods) {
			method.print(out, lineNumber);
		}

	}
}
