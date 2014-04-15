package bc.j2b.model;

import java.io.PrintStream;

/**
 * 
 * @author Administrator
 * 
 */
public class StIfElseModel extends StatementModel {

	private final int blockHeight = 75;

	private ExpressionModel testClause;
	private StBlockModel thenClause;
	private StBlockModel elseClause;

	// private StatementModel elseClause;

	public StIfElseModel() {
		setBlockHeight(blockHeight);
	}

	public ExpressionModel getTestClause() {
		return testClause;
	}

	public void setTestClause(ExpressionModel testClause) {
		testClause.setParent(this);
		this.testClause = testClause;
	}

	public void setThenClause(StBlockModel thenClause) {
		thenClause.setParent(this);
		this.thenClause = thenClause;
	}

	public StBlockModel getThenClause() {
		return thenClause;
	}

	public void setElseClause(StBlockModel elseClause) {
		// public void setElseClause(StatementModel elseClause) {
		elseClause.setParent(this);
		this.elseClause = elseClause;
	}

	public StBlockModel getElseClause() {
		// public StatementModel getElseClause() {
		return elseClause;
	}

	public String getGenusName() {
		// TODO ifelseÇµÇ©Ç¬Ç©ÇÌÇ»Ç¢?
		// if (elseClause == null) {
		// return "if";
		// } else {
		return "ifelse";
		// }
	}

	// private void printIfBlockDec2(PrintStream out, int indent, int
	// testBlockId,
	// int thenBlockId, int elseBlockId, int numSockets) {
	// coder.printLocation(out, indent, getPosX(), getPosY());
	// coder.printBeforeAfterBlockId(out, indent, getNext(), getPrevious());
	// coder.printSocketsTag(out, indent, numSockets);
	// coder.printBlockConnector(out, indent + 1, "socket", "boolean",
	// "boolean", "test", false, "single", testBlockId);
	// coder.printBlockConnector(out, indent + 1, "socket", "cmd", "cmd",
	// "then", false, "single", thenBlockId);
	// if (elseBlockId > 0) {
	// coder.printBlockConnector(out, indent + 1, "socket", "cmd", "cmd",
	// "else", false, "single", elseBlockId);
	// }
	// coder.printSocketsEndTag(out, indent);
	//
	// }

	@Override
	public void print(PrintStream out, int indent) {
		int numSockets = 0;

		// children
		int testId = -1;
		if (testClause != null) {
			testClause.setConnectorId(getId());
			testClause.print(out, indent);
			testId = testClause.getId();
			numSockets++;
		}
		int thenId = -1;
		if (thenClause.getChildrenSize() != 0) {
			thenClause.setConnectorId(getId());
			thenClause.print(out, indent);
			thenId = thenClause.getChild(0).getId();
			numSockets++;
		}
		int elseId = -1;
		if (elseClause instanceof StBlockModel) {
			if (elseClause != null
					&& ((StBlockModel) elseClause).getChildrenSize() != 0) {
				elseClause.setConnectorId(getId());
				elseClause.print(out, indent);
				elseId = ((StBlockModel) elseClause).getChild(0).getId();
				numSockets++;
			}
		} else if (elseClause instanceof StBlockModel) {
			elseClause.setConnectorId(getId());
			elseClause.print(out, indent);
			elseId = elseClause.getId();
			numSockets++;
		}
		// BeforeBlockÇ∆AfterBlockÇåüçıÇ∑ÇÈ
		resolveBeforeAfterBlock(getParent());

		// print BlockEditor File
		printIfBlockDec(out, indent, testId, thenId, elseId, numSockets);
	}

	private void printIfBlockDec(PrintStream out, int indent, int testId,
			int thenId, int elseId, int numSockets) {

		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\""
				+ getGenusName() + "\">");
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
		out.println("<Sockets num-sockets=\"" + numSockets + "\">");
		// test blockConnector
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"sockets\" connector-type=\"boolean\""
				+ " init-type=\"boolean\" label=\"\" position-type=\"single\" con-block-id=\""
				+ testId + "\"/>");
		// then blockConnector
		makeIndent(out, indent + 2);
		out.print("<BlockConnector connector-kind=\"sockets\" connector-type=\"cmd\""
				+ " init-type=\"cmd\" label=\"\" position-type=\"single\"");
		if (thenId != -1) {
			out.print(" con-block-id=\"" + thenId + "\"");
		}
		out.println("/>");
		// else blockConnector
		makeIndent(out, indent + 2);
		out.print("<BlockConnector connector-kind=\"sockets\" connector-type=\"cmd\""
				+ " init-type=\"cmd\" label=\"\" position-type=\"single\"");
		if (elseId != -1) {
			out.print(" con-block-id=\"" + elseId + "\"");
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
