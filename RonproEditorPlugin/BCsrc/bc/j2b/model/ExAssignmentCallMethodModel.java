package bc.j2b.model;

import java.io.PrintStream;
import java.util.List;

@Deprecated
public class ExAssignmentCallMethodModel extends ExCallMethodModel {

	private String notSocketsBlocks[] = { "input", "getX", "getY", "getWidth",
			"getHeight" };

	@Override
	public void print(PrintStream out, int indent) {
		int firstChildId = -1;
		List<ExpressionModel> arguments = getArguments();

		for (int i = 0; i < arguments.size(); i++) {
			ExpressionModel arg = arguments.get(i);
			arg.setConnectorId(getId());
			arg.print(out, indent);
			if (i == 0) {
				firstChildId = arg.getId();
			}
		}

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\"" + getName()
				+ "\">");
		// lineNumber
		makeIndent(out, indent + 1);
		out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
		// parent
		makeIndent(out, indent + 1);
		ElementModel p = getParent() instanceof StExpressionModel ? getParent().getParent() : getParent();
		out.println("<ParentBlock>" + p.getId() + "</ParentBlock>");
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
		out.println("<BlockConnector connector-kind=\"plug\" connector-type=\"number\""
				+ " init-type=\"number\" label=\"\" position-type=\"single\" con-block-id=\""
				+ getParent().getId() + "\"/>");
		// end Plug
		makeIndent(out, indent + 1);
		out.println("</Plug>");
		if (!hasSocketsChecker()) {
			makeIndent(out, indent + 1);
			out.println("<Sockets num-sockets=\"1\">");
			makeIndent(out, indent + 2);
			out.println("<BlockConnector connector-kind=\"socket\" connector-type=\"number\""
					+ " init-type=\"number\" label=\"\" position-type=\"single\" con-block-id=\""
					+ firstChildId + "\"/>");
			// end Socket
			makeIndent(out, indent + 1);
			out.println("</Sockets>");
		}
		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	private boolean hasSocketsChecker() {
		for (String name : notSocketsBlocks) {
			if (name.equals(getName())) {
				return true;
			}
		}
		return false;
	}

}
