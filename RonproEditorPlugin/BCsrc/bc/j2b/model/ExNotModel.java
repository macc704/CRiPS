package bc.j2b.model;

import java.io.PrintStream;

/**
 * 2012.11.23 #matsuzawa
 * 
 */
public class ExNotModel extends ExpressionModel {

	private final int blockHeight = 40;
	private ExpressionModel expression;

	public ExNotModel() {
		setBlockHeight(blockHeight);
	}

	public String getType() {
		return "boolean";
	}

	public void setExpression(ExpressionModel expression) {
		this.expression = expression;
		expression.setParent(this);
	}

	@Override
	public void print(PrintStream out, int indent) {
		expression.setConnectorId(getId());
		expression.print(out, indent);

		String genusName = "not";
		String connectorType = getType();// "boolean";

		// BeforeBlockとAfterBlockを検索する
		// ResolveBeforeAfterBlock(getParent().getParent());//2012.09.27
		// #matsuzawa
		// resolveBeforeAfterBlock(getBlockParent());// 2012.09.27 #matsuzawa

		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\"" + genusName
				+ "\">");

		// label
		makeIndent(out, indent + 1);
		// out.println("<Label>" + "!" + "</Label>");
		// lineNumber
		makeIndent(out, indent + 1);
		out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
		// parent
		makeIndent(out, indent + 1);
		ElementModel p = getParent() instanceof StExpressionModel ? getParent().getParent() : getParent();
		out.println("<ParentBlock>" + p.getId() + "</ParentBlock>");
		// location
		out.println("<Location>");
		makeIndent(out, indent + 1);
		out.println("<X>" + getPosX() + "</X>");
		makeIndent(out, indent + 1);
		out.println("<Y>" + getPosY() + "</Y>");
		makeIndent(out, indent + 1);
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

		{// plug
			makeIndent(out, indent + 1);
			out.println("<Plug>");
			// blockConnecter
			makeIndent(out, indent + 2);
			out.print("<BlockConnector connector-kind=\"plug\" connector-type=\""
					+ connectorType
					+ "\""
					+ " init-type=\""
					+ connectorType
					+ "\" label=\"\" position-type=\"single\"");
			out.print(" con-block-id=\"" + getParent().getId() + "\"");
			out.println("/>");
			// end Plug
			makeIndent(out, indent + 1);
			out.println("</Plug>");
		}

		{// Socket
			if (expression.getId() != -1) {
				makeIndent(out, indent + 1);
				out.println("<Sockets num-sockets=\"1\">");
				// blockConnecters
				makeIndent(out, indent + 2);
				out.print("<BlockConnector connector-kind=\"sockets\" connector-type=\""
						+ connectorType
						+ "\""
						+ " init-type=\""
						+ connectorType
						+ "\" label=\"\" position-type=\"single\"");
				out.print(" con-block-id=\"" + expression.getId() + "\"" + "/>");
				out.println();
				// end Socket
				makeIndent(out, indent + 1);
				out.println("</Sockets>");
			}
		}

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	public String getLabel() {
		return "!" + expression.getLabel();
	}

}
