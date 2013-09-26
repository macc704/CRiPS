package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ExCastModel extends ExpressionModel {

	private List<ExpressionModel> arguments = new ArrayList<ExpressionModel>();

	public ExCastModel() {
	}

	public void addArgument(ExpressionModel arg) {
		if (arg != null) {
			arg.setParent(this);
			this.arguments.add(arg);
		}
	}

	@Override
	public void print(PrintStream out, int indent) {
		// System.out.println("A:" + getType());
		int firstChildId = -1;
		String plugConnector = "number";
		String socketConnector = "number";

		for (int i = 0; i < arguments.size(); i++) {
			ExpressionModel arg = arguments.get(i);
			arg.setConnectorId(getId());
			arg.print(out, indent);
			if (i == 0) {
				firstChildId = arg.getId();
			}
		}

		String genusName = "";
		if (getType().equals("int") || getType().equals("number")) {
			plugConnector = "number";
			socketConnector = "double-number";
			genusName = "toIntFromDouble";
		} else if (getType().equals("double")
				|| getType().equals("double-number")) {
			plugConnector = "double-number";
			socketConnector = "number";
			genusName = "toDoubleFromInt";
		} else if (getType().equals("toString")) {
			// old
			throw new RuntimeException("toString() not supported");
		} else if (getType().equals("string")) {
			plugConnector = "string";
			socketConnector = "number";
			genusName = "toStringFromInt";
			// throw new RuntimeException("toString() not supported");
		} else {
			throw new RuntimeException("not supported cast: " + getType());
		}

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\"" + genusName
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
		out.println("<BlockConnector connector-kind=\"plug\" connector-type=\""
				+ plugConnector + "\"" + " init-type=\"" + plugConnector
				+ "\" label=\"\" position-type=\"single\" con-block-id=\""
				+ getParent().getId() + "\"/>");
		// end Plug
		makeIndent(out, indent + 1);
		out.println("</Plug>");
		makeIndent(out, indent + 1);
		out.println("<Sockets num-sockets=\"1\">");
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"socket\" connector-type=\""
				+ socketConnector
				+ "\""
				+ " init-type=\""
				+ socketConnector
				+ "\" label=\"\" position-type=\"single\" con-block-id=\""
				+ firstChildId + "\"/>");
		// end Socket
		makeIndent(out, indent + 1);
		out.println("</Sockets>");
		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bc.j2b.model.ElementModel#getLabel()
	 */
	@Override
	public String getLabel() {
		if (arguments.size() != 1) {
			return "ERROR:arguments.size() != 1";
		}
		return "(" + getType() + ")" + arguments.get(0).getLabel();
	}
}
