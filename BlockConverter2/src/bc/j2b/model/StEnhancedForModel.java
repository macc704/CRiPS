package bc.j2b.model;

import java.io.PrintStream;

public class StEnhancedForModel extends StatementModel {
	private final int blockHeight = 95;
	private StatementModel testClause;
	private StBlockModel bodyClause;

	public StEnhancedForModel() {
		setBlockHeight(blockHeight);
	}

	public void setTestClause(StatementModel testClause) {
		testClause.setParent(this);
		this.testClause = testClause;
	}

	public void setBodyClause(StBlockModel bodyClause) {
		bodyClause.setParent(this);
		this.bodyClause = bodyClause;
	}

	public StBlockModel getBodyClause() {
		return bodyClause;
	}

	@Override
	public void print(PrintStream out, int indent) {

		// test child
		int testId = -1;
		if (testClause != null) {
			testClause.setConnectorId(getId());
			testClause.print(out, indent);
			testId = testClause.getId();
		}
		// do child
		int doId = -1;
		if (bodyClause.getChildrenSize() != 0) {
			bodyClause.setConnectorId(getId());
			bodyClause.print(out, indent);
			doId = bodyClause.getChild(0).getId();
		}
		// BeforeBlockとAfterBlockを検索する
		resolveBeforeAfterBlock(getParent());

		printWhileBlockDec(out, indent, testId, doId);
	}

	private void printWhileBlockDec(PrintStream out, int indent, int testId,
			int doId) {

		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\"enhancedfor\">");

		// lineNumber
		makeIndent(out, indent + 1);
		out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
		// parent
		makeIndent(out, indent + 1);
		ElementModel p = getParent() instanceof StExpressionModel ? getParent()
				.getParent() : getParent();
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
		// beforeBlockId
		if (getPrevious() != -1) {
			makeIndent(out, indent + 1);
			out.println("<BeforeBlockId>" + getPrevious() + "</BeforeBlockId>");
		}
		// afterBlockId
		if (getNext() != -1) {
			makeIndent(out, indent + 1);
			out.println("<AfterBlockId>" + getNext() + "</AfterBlockId>");
		}
		// Socket
		makeIndent(out, indent + 1);
		out.println("<Sockets num-sockets=\"2\">");

		// test blockConnector
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"sockets\" connector-type=\"cmd\""
				+ " init-type=\"boolean\" label=\"\" position-type=\"single\" con-block-id=\""
				+ testId + "\"/>");
		// then blockConnector
		makeIndent(out, indent + 2);
		out.print("<BlockConnector connector-kind=\"sockets\" connector-type=\"cmd\""
				+ " init-type=\"cmd\" label=\"\" position-type=\"single\"");
		if (doId != -1) {
			out.print(" con-block-id=\"" + doId + "\"");
		}
		out.println("/>");
		// end Socket
		makeIndent(out, indent + 1);
		out.println("</Sockets>");
		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

}
