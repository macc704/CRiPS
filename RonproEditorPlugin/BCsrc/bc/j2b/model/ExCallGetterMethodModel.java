package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ExCallGetterMethodModel extends ExpressionModel {

	private final int referenceBlockHeight = 40;

	private StVariableDeclarationModel variable;
	private List<ExpressionModel> arguments = new ArrayList<ExpressionModel>();

	public ExCallGetterMethodModel() {
		setBlockHeight(referenceBlockHeight);
	}

	@Override
	public String getType() {
		// #matsuzawa 2012.11.07 修正
		if (arguments.size() > 0) {
			return arguments.get(0).getType();
		} else {
			throw new RuntimeException("No Method in ExCallGetterMethodModel");
		}
	}

	public void setVariable(StVariableDeclarationModel variable) {
		this.variable = variable;
	}

	public void setArgument(ExpressionModel arg) {
		if (arg != null) {
			arg.setParent(this);
			this.arguments.add(arg);
		}
	}

	@Override
	public void print(PrintStream out, int indent) {

		// arguments
		for (ExpressionModel arg : arguments) {
			arg.setConnectorId(getId());
			arg.print(out, indent);
		}

		String connectorType = getConnectorType(getType());

		// print BlockEditor File
		// stubBlock
		makeIndent(out, indent);
		out.println("<BlockStub>");
		makeIndent(out, indent + 1);
		out.println("<StubParentName>" + variable.getName()
				+ "</StubParentName>");
		makeIndent(out, indent + 1);
		out.println("<StubParentGenus>" + variable.getGenusName()
				+ "</StubParentGenus>");
		// genus-name
		makeIndent(out, indent + 1);
		String genusNamePrefix = "callGetterMethod";// numberの場合
		if (connectorType.equals("boolean")) {
			genusNamePrefix = "callBooleanMethod";
		} else if (connectorType.equals("string")) {
			genusNamePrefix = "callStringMethod";
		} else if (connectorType.equals("double-number")) {
			genusNamePrefix = "callDoubleMethod";
		} else if (connectorType.equals("object")) {
			genusNamePrefix = "callObjectMethod";
		}
		out.println("<Block id=\"" + getId() + "\" genus-name=\""
				+ genusNamePrefix + variable.getGenusName() + "\">");
		// label
		makeIndent(out, indent + 2);
		out.println("<Label>" + variable.getName() + "</Label>");
		// lineNumber
		makeIndent(out, indent + 2);
		out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
		// parent
		makeIndent(out, indent + 2);
		ElementModel p = getParent() instanceof StExpressionModel ? getParent()
				.getParent() : getParent();
		out.println("<ParentBlock>" + p.getId() + "</ParentBlock>");
		// location
		makeIndent(out, indent + 2);
		out.println("<Location>");
		makeIndent(out, indent + 3);
		out.println("<X>" + getPosX() + "</X>");
		makeIndent(out, indent + 3);
		out.println("<Y>" + getPosY() + "</Y>");
		makeIndent(out, indent + 2);
		out.println("</Location>");

		// plug
		out.println("<Plug>");
		// blockConnecter
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"plug\" connector-type=\""
				+ connectorType
				+ "\""
				+ " init-type=\"number\" label=\"\" position-type=\"single\" con-block-id=\""
				+ getParent().getId() + "\"/>");
		// end Plug
		makeIndent(out, indent + 1);
		out.println("</Plug>");
		// Socket
		makeIndent(out, indent + 2);
		out.println("<Sockets num-sockets=\"1\">");
		// blockConnecters
		for (ExpressionModel arg : arguments) {
			makeIndent(out, indent + 3);
			out.print("<BlockConnector connector-kind=\"socket\" connector-type=\""
					+ connectorType
					+ "\""
					+ " init-type=\"number\" label=\"\" position-type=\"single\" con-block-id=\""
					+ arg.getId() + "\"");
			out.println("/>");
		}
		// end Socket
		makeIndent(out, indent + 2);
		out.println("</Sockets>");
		// end Block
		makeIndent(out, indent + 1);
		out.println("</Block>");
		makeIndent(out, indent);
		out.println("</BlockStub>");
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
		return variable.getName() + "." + arguments.get(0).getLabel();
	}
}
