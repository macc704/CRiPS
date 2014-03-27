package bc.j2b.model;

import java.io.PrintStream;

/**
 * PostFixModelÇ∆Ç¢Ç§ÇÊÇËÅCincrementÇÃÉÇÉfÉã(èº‡VÅC2012.11.23)
 * 
 */
public class ExPostfixModel extends ExpressionModel {

	private final int blockHeight = 40;
	private StVariableDeclarationModel variable;

	// private String incName;
	// private String className;
	// private String genusName;

	private ExpressionModel postfix;

	public ExPostfixModel() {
		setBlockHeight(blockHeight);
	}

	public String getType() {
		return variable.getType();
	}

	public void setVariable(StVariableDeclarationModel variable) {
		this.variable = variable;
	}

	public StVariableDeclarationModel getVariable() {
		return variable;
	}

	public void setPostfix(ExpressionModel postfix) {
		this.postfix = postfix;
	}

	public ExpressionModel getPostfix() {
		return postfix;
	}

	private ElementModel getBlockParent() {
		ElementModel parent = getParent();
		if (parent instanceof StBlockModel
				|| parent instanceof StAbstractionBlockModel) {
			return parent;
		}
		return parent.getParent();
	}

	@Override
	public void print(PrintStream out, int indent) {
		postfix.setConnectorId(getId());
		postfix.print(out, indent);

		String connectorType = convertJavaTypeToBlockType(variable.getType());

		// BeforeBlockÇ∆AfterBlockÇåüçıÇ∑ÇÈ
		// ResolveBeforeAfterBlock(getParent().getParent());//2012.09.27
		// #matsuzawa
		resolveBeforeAfterBlock(getBlockParent());// 2012.09.27 #matsuzawa

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
		out.println("<Block id=\"" + getId() + "\" genus-name=\"inc"
				+ variable.getGenusName() + "\">");
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
		out.print("<BlockConnector connector-kind=\"sockets\" connector-type=\""
				+ connectorType
				+ "\""
				+ " init-type=\""
				+ connectorType
				+ "\" label=\"\" position-type=\"single\"");
		if (postfix.getId() != -1) {
			out.print(" con-block-id=\"" + postfix.getId() + "\"");
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
		return variable.getName() + getOperator(postfix.getLabel());
	}

	private String getOperator(String label) {
		if ("1".equals(label)) {
			return "++";
		} else if ("-1".equals(label)) {
			return "--";
		} else {
			return "unknown postfix =" + label;
		}
	}

}
