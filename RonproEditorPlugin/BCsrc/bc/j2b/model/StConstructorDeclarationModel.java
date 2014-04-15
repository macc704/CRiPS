package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class StConstructorDeclarationModel extends StatementModel{
	

	private final String genusName = "constructor";
	private final int blockHeight = 55;

	private String name;
	// private String type;

	private List<StLocalVariableModel> args = new ArrayList<StLocalVariableModel>();

	private StBlockModel body;
	
	private boolean isCollapsed = false;

	public StConstructorDeclarationModel() {
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

	public void setCollapsed(boolean flag){
		this.isCollapsed = flag;
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
		out.println("<Label>" + "コンストラクタ" + "</Label>");
		//comment
		makeIndent(out, indent + 1);
		out.println("<LineComment>" + getComment() + "</LineComment>");
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
		if (body.getChildrenSize() != 0) {
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
		
		if(isCollapsed){
			makeIndent(out, indent + 1);
			out.println("<Collapsed/>");
		}		
		makeIndent(out, indent);
		out.println("</Block>");

	}

	

}
