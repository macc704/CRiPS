package bc.j2b.model;

import java.io.PrintStream;

public class ExCallActionMethodModel extends ExpressionModel {

	private final int referenceBlockHeight = 40;

	private StVariableDeclarationModel variable;
	private StatementModel callMethod;

	public ExCallActionMethodModel() {
		setBlockHeight(referenceBlockHeight);
	}

	@Override
	public String getType() {
		return variable.getType();
	}

	public void setVariable(StVariableDeclarationModel variable) {
		this.variable = variable;
	}

	public void setCallMethod(StatementModel model) {
		model.setParent(this);
		this.callMethod = model;
	}

	@Override
	public void print(PrintStream out, int indent) {

		// callMethod
		int callMethodID = -1;
		if (callMethod instanceof StBlockModel) {
			if (callMethod != null
					&& ((StBlockModel) callMethod).getChildrenSize() != 0) {
				callMethod.setConnectorId(getId());
				callMethod.print(out, indent);
				callMethodID = ((StBlockModel) callMethod).getChild(0).getId();
			}
		}

		// BeforeBlockÇ∆AfterBlockÇåüçıÇ∑ÇÈ
		resolveBeforeAfterBlock(getParent().getParent());

		String valGenusName = variable.getGenusName();
		if (valGenusName == null) {
			valGenusName = "local-var-object";
		}

		// print BlockEditor File
		// stubBlock
		makeIndent(out, indent);
		out.println("<BlockStub>");
		makeIndent(out, indent + 1);
		out.println("<StubParentName>" + variable.getName()
				+ "</StubParentName>");
		makeIndent(out, indent + 1);
		out.println("<StubParentGenus>" + valGenusName + "</StubParentGenus>");
		// genus-name
		makeIndent(out, indent + 1);
		out.println("<Block id=\"" + getId()
				+ "\" genus-name=\"callActionMethod" + valGenusName + "\">");
		// label
		makeIndent(out, indent + 2);
		out.println("<Label>" + variable.getName() + "</Label>");
		// lineNumber
		makeIndent(out, indent + 2);
		out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
		// parent
		makeIndent(out, indent + 2);
		ElementModel p = getParent() instanceof StExpressionModel ? getParent().getParent() : getParent();
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
		out.println("<Sockets num-sockets=\"1\">");
		// blockConnecters
		makeIndent(out, indent + 3);
		out.print("<BlockConnector connector-kind=\"socket\" connector-type=\"cmd\""
				+ " init-type=\"cmd\" label=\"\" position-type=\"single\"");
		if (callMethodID != -1) {
			out.print(" con-block-id=\"" + callMethodID + "\"");
		}
		out.println("/>");
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
		if (callMethod instanceof StBlockModel
				&& ((StBlockModel) callMethod).getChildrenSize() != 0) {
			return variable.getName() + "."
					+ ((StBlockModel) callMethod).getChild(0).getLabel();
		}
		return variable.getName() + "." + callMethod.getLabel();
	}
}
