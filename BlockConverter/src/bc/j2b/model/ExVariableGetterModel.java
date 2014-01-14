/*
 * VariableGetterModel.java
 * Created on 2011/10/02
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.j2b.model;

import java.io.PrintStream;

/**
 * @author macchan
 */
public class ExVariableGetterModel extends ExpressionModel {

	private StVariableDeclarationModel variable;
	private String genusName = "getter";

	/*
	 * (non-Javadoc)
	 * 
	 * @see j2b.model.ExpressionModel#getType()
	 */
	@Override
	public String getType() {
		return variable.getType();
	}

	/**
	 * @param variable
	 *            the variable to set
	 */
	public void setVariable(StVariableDeclarationModel variable) {
		this.variable = variable;
	}

	public void setGenusName(String name) {
		this.genusName = name;
	}

	public void print(PrintStream out, int indent) {

		// print BlockEditor File
		// stubBlock

		String connectorType = getConnectorType(variable.getType());

		// if (variable.getGenusName().startsWith("local")) {
		// genusName = genusName + "local-var-" + connectorType;
		// } else {
		// genusName = genusName + "private-var-" + connectorType;
		// }

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
		out.println("<Block id=\"" + getId() + "\" genus-name=\"" + genusName
				+ variable.getGenusName() + "\">");
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
		// Plug
		makeIndent(out, indent + 2);
		out.println("<Plug>");
		// blockConnecters
		makeIndent(out, indent + 3);
		out.print("<BlockConnector connector-kind=\"plug\" connector-type=\""
				+ connectorType + "\"" + " init-type=\"" + connectorType
				+ "\" label=\"\" position-type=\"mirror\"");
		if (getConnectorId() != -1) {
			out.print(" con-block-id=\"" + getConnectorId() + "\"");
		}
		out.println("/>");
		// end Socket
		makeIndent(out, indent + 2);
		out.println("</Plug>");
		// end Block
		makeIndent(out, indent + 1);
		out.println("</Block>");
		makeIndent(out, indent);
		out.println("</BlockStub>");
	}

	public String getLabel() {
		return variable.getName();
	}
}
