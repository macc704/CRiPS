package bc.j2b.model;

import java.io.PrintStream;

public class ExTypeModel extends ExpressionModel {

	private String label;

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void print(PrintStream out, int indent) {

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\""
				+ "type-object" + "\">");
		// label
		makeIndent(out, indent + 1);
		out.println("<Label>" + label + "</Label>");
		// lineNumber
		makeIndent(out, indent + 1);
		out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
		// parent
		makeIndent(out, indent + 1);
		out.println("<ParentBlock>" + getParent().getId() + "</ParentBlock>");
		// location
		makeIndent(out, indent + 1);
		out.println("<Location>");
		makeIndent(out, indent + 2);
		out.println("<X>" + getPosX() + "</X>");
		makeIndent(out, indent + 2);
		out.println("<Y>" + getPosY() + "</Y>");
		makeIndent(out, indent + 1);
		out.println("</Location>");
		// plug
		makeIndent(out, indent + 1);
		out.println("<Plug>");
		// blockConnecter
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"plug\" connector-type=\""
				+ getConnectorType(getType()) + "\" init-type=\""
				+ getConnectorType(getType())
				+ "\" label=\"\" position-type=\"mirror\" con-block-id=\""
				+ getParent().getId() + "\"/>");
		// end Socket
		makeIndent(out, indent + 1);
		out.println("</Plug>");
		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}
}
