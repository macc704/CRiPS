package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class StAbstractionBlockModel extends StatementModel {

	private final int blockHeight = 75;
	private String comment = "";
	private List<StatementModel> children = new ArrayList<StatementModel>();
	private boolean isCollapsed = false;

	public StAbstractionBlockModel() {
		setBlockHeight(blockHeight);
	}

	public String getComment() {
		return comment;
	}

	public void setCommnent(String comment) {
		this.comment = ElementModel.addEscapeSequence(comment);
	}

	public void addChild(StatementModel bodyClause) {
		bodyClause.setParent(this);
		children.add(bodyClause);
	}

	public StatementModel getChild(int index) {
		return children.get(index);
	}

	public List<StatementModel> getChildren() {
		return children;
	}

	public int getChildrenSize() {
		return children.size();
	}

	public void setCollapsed(boolean isCollapsed) {
		this.isCollapsed = isCollapsed;
	}

	@Override
	public void print(PrintStream out, int indent) {
		// children
		for (int i = 0; i < children.size(); i++) {
			ElementModel child = children.get(i);
			child.print(out, indent);
		}

		int bodyId = -1;
		if (children.size() > 0) {
			bodyId = children.get(0).getId();
			children.get(0).setConnectorId(getId());
		}

		resolveBeforeAfterBlock(getParent());

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\"abstraction\">");
		// label(comment)
		makeIndent(out, indent);
		out.println("<Label>" + comment + "</Label>");
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
		// isCollapsed?
		makeIndent(out, indent + 1);
		if (isCollapsed) {
			out.println("<Collapsed/>");
		}
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
		out.println("<Sockets num-sockets=\"1\">");
		// blockConnecters
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"sockets\" connector-type=\"cmd\" init-type=\"cmd\" label=\"\""
				+ " position-type=\"single\" con-block-id=\"" + bodyId + "\"/>");
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
	 * @see j2b.model.ElementModel#getLabel()
	 */
	@Override
	public String getLabel() {
		return "(AbstractionBlock)" + getComment();
	}
}
