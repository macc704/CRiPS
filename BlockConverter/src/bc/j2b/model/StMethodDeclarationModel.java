package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class StMethodDeclarationModel extends StatementModel {

	private final String genusName = "procedure";
	private final int blockHeight = 55;

	private String name;
	// private String type;

	private boolean isCollapsed = false;

	private List<StLocalVariableModel> args = new ArrayList<StLocalVariableModel>();

	private StBlockModel body;

	public StMethodDeclarationModel() {
		setBlockHeight(blockHeight);
	}

	public void addArgument(StLocalVariableModel argModel) {
		args.add(argModel);
		argModel.setParent(this);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// /**
	// * @param type
	// * the type to set
	// */
	// public void setType(String type) {
	// this.type = CheckJavaToBlockType(type);
	// }

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(StBlockModel body) {
		body.setParent(this);
		this.body = body;
	}

	public StBlockModel getBody() {
		return body;
	}

	public void setCollapsed(boolean flag) {
		isCollapsed = flag;
	}

	@Override
	public void print(PrintStream out, int indent) {
		// setId(idCounter++);
		// setPosY(getPosY() + possitionY);
		// possitionY += getBlockHeight();

		// args
		for (StLocalVariableModel arg : args) {
			arg.printAsArgument(out, indent);
		}

		// body
		if (body != null) {
			body.print(out, indent);
		}

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\"" + genusName
				+ "\">");
		// label
		makeIndent(out, indent + 1);
		out.println("<Label>" + name + "</Label>");

		{// 2013 09/26 ohata tag for line comment
			// comment
			makeIndent(out, indent + 1);
			out.println("<LineComment>" + getComment() + "</LineComment>");
		}

		{// 2013 09/26 hakamata tag for linenumber and parent block parent
			// blockは暫定
			// lineNumber
			makeIndent(out, indent + 1);
			out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
			// parent
			makeIndent(out, indent + 1);
			ElementModel p = getParent() instanceof StExpressionModel ? getParent()
					.getParent() : getParent();
			out.println("<ParentBlock>" + p.getId() + "</ParentBlock>");
		}

		// location
		makeIndent(out, indent + 1);
		out.println("<Location>");
		makeIndent(out, indent + 2);
		out.println("<X>" + getPosX() + "</X>");
		makeIndent(out, indent + 2);
		out.println("<Y>" + getPosY() + "</Y>");
		makeIndent(out, indent + 1);
		out.println("</Location>");
		// afterBlockId
		if (body != null && body.getChildrenSize() != 0) {
			makeIndent(out, indent + 1);
			out.println("<AfterBlockId>" + body.getChild(0).getId()
					+ "</AfterBlockId>");
		}

		// 引数
		makeIndent(out, indent + 1);
		out.println("<Sockets num-sockets=\"" + args.size() + 1 + "\">");
		for (StLocalVariableModel arg : args) {
			makeIndent(out, indent + 2);
			out.println("<BlockConnector connector-kind=\"socket\" connector-type=\""
					+ arg.getBlockType()
					+ "\""
					+ " init-type=\"poly\" label=\"\" is-expandable=\"yes\" position-type=\"single\" con-block-id=\""
					+ arg.getId() + "\"/>");
		}
		// 空きコネクタ
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"sockets\" connector-type=\"poly\" init-type=\"poly\" label=\"\" position-type=\"single\" is-expandable=\"yes\"/>");
		makeIndent(out, indent + 1);
		out.println("</Sockets>");

		if (isCollapsed) {
			makeIndent(out, indent + 1);
			out.println("<Collapsed/>");
		}

		makeIndent(out, indent);
		out.println("</Block>");
	}

}
