package bc.j2b.model;

import java.io.PrintStream;

public class ExVariableSetterModel extends ExpressionModel {

	private final int blockHeight = 50;

	private StVariableDeclarationModel variable;
	private ExpressionModel rightExpression;
	private ExpressionModel rightAssignment;
	private String genusName = "setter";
	private ExpressionModel index;

	public ExVariableSetterModel() {
		setBlockHeight(blockHeight);
	}

	public String getType() {
		return variable.getType();
	}

	public ExpressionModel getRightExpression() {
		return rightExpression;
	}

	public void setVariable(StVariableDeclarationModel variable) {
		this.variable = variable;
	}

	public StVariableDeclarationModel getVariable() {
		return variable;
	}

	public void setRightExpression(ExpressionModel model) {
		model.setParent(this);
		this.rightExpression = model;
	}

	public void setIndexModel(ExpressionModel indexModel) {
		this.index = indexModel;
	}

	@Override
	public void print(PrintStream out, int indent) {

		// rightOperand
		if (rightAssignment != null) {
			rightAssignment.print(out, indent);
		}

		if (variable.isArray() && index != null) {
			genusName += "-arrayelement" + variable.getGenusName();
		} else {
			genusName += variable.getGenusName();
		}

		// System.out.println(variable.getType());
		String connectorType = getConnectorType(variable.getType());
		// System.out.println("connectorType" + connectorType);

		if (index != null) {
			index.print(out, indent);
		}

		rightExpression.setConnectorId(getId());
		rightExpression.print(out, indent);

		// BeforeBlockとAfterBlockを検索する
		resolveBeforeAfterBlock(getParent().getParent());

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
		out.println("<Block id=\"" + getId() + "\" genus-name=\"" + genusName
				+ "\">");
		// label
		makeIndent(out, indent + 2);
		out.println("<Label>" + ElementModel.addEscapeSequence(variable.getName()) + "</Label>");
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
		// beforeBlockId
		if (getPrevious() != -1) {
			makeIndent(out, indent + 2);
			out.println("<BeforeBlockId>" + getPrevious() + "</BeforeBlockId>");
		}
		// afterBlockId
		if (getNext() != -1) {
			makeIndent(out, indent + 2);
			out.println("<AfterBlockId>" + getNext() + "</AfterBlockId>");
		}
		// Socket
		makeIndent(out, indent + 2);
		if (index != null) {
			out.println("<Sockets num-sockets=\"2\">");
			makeIndent(out, indent + 3);
			out.print("<BlockConnector connector-kind=\"socket\" connector-type=\""
					+ "number"
					+ "\""
					+ " init-type=\""
					+ "number"
					+ "\" label=\"\" position-type=\"single\"");
			out.print(" con-block-id=\"" + index.getId() + "\"");
			out.println("/>");

			// blockConnecters
			makeIndent(out, indent + 3);
			out.print("<BlockConnector connector-kind=\"socket\" connector-type=\""
					+ getElementConnector(getType())
					+ "\""
					+ " init-type=\""
					+ getElementConnector(getType())
					+ "\" label=\"\" position-type=\"single\"");
			if (rightExpression.getId() != -1) {
				out.print(" con-block-id=\"" + rightExpression.getId() + "\"");
			}
			out.println("/>");
		} else {
			out.println("<Sockets num-sockets=\"1\">");
			// blockConnecters
			makeIndent(out, indent + 3);
			out.print("<BlockConnector connector-kind=\"socket\" connector-type=\""
					+ connectorType
					+ "\""
					+ " init-type=\""
					+ connectorType
					+ "\" label=\"\" position-type=\"single\"");
			if (rightExpression.getId() != -1) {
				out.print(" con-block-id=\"" + rightExpression.getId() + "\"");
			}
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

	public String getLabel() {
		return variable.getName() + " = " + rightExpression.getLabel();
	}

}
