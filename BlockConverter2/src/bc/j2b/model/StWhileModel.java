package bc.j2b.model;

import java.io.PrintStream;

public class StWhileModel extends StatementModel {

	private final int blockHeight = 95;
	private ExpressionModel testClause;
	private StBlockModel bodyClause;

	private boolean isDo = false;

	public StWhileModel() {
		this(false);
	}

	public StWhileModel(boolean isDo) {
		setBlockHeight(blockHeight);
		this.isDo = isDo;
	}

	public void setTestClause(ExpressionModel testClause) {
		testClause.setParent(this);
		this.testClause = testClause;
	}

	public void setBodyClause(StBlockModel bodyClause) {
		bodyClause.setParent(this);
		this.bodyClause = bodyClause;
	}

	public ExpressionModel getTestClause() {
		return testClause;
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
		if (!isDo) {
			out.println("<Block id=\"" + getId() + "\" genus-name=\"while\">");
		} else {
			out.println("<Block id=\"" + getId() + "\" genus-name=\"dowhile\">");
		}
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
		if (!isDo) {// 順番が逆なだけ．
			// test blockConnector
			makeIndent(out, indent + 2);
			out.println("<BlockConnector connector-kind=\"sockets\" connector-type=\"boolean\""
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
		} else {
			// then blockConnector
			makeIndent(out, indent + 2);
			out.print("<BlockConnector connector-kind=\"sockets\" connector-type=\"cmd\""
					+ " init-type=\"cmd\" label=\"\" position-type=\"single\"");
			if (doId != -1) {
				out.print(" con-block-id=\"" + doId + "\"");
			}
			out.println("/>");
			// test blockConnector
			makeIndent(out, indent + 2);
			out.println("<BlockConnector connector-kind=\"sockets\" connector-type=\"boolean\""
					+ " init-type=\"boolean\" label=\"\" position-type=\"single\" con-block-id=\""
					+ testId + "\"/>");
		}
		// end Socket
		makeIndent(out, indent + 1);
		out.println("</Sockets>");
		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

}
