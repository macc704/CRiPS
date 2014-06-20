package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import bc.BlockConverter;

public class ExClassInstanceCreationModel extends ExpressionModel {

	private String name;
	private final int blockHeight = 10;

	private List<ExpressionModel> arguments = new ArrayList<ExpressionModel>();
	private String genusName;

	
	public ExClassInstanceCreationModel() {
		setBlockHeight(blockHeight);
	}

	public void addArgument(ExpressionModel arg) {
		if (arg != null) {
			arg.setParent(this);
			this.arguments.add(arg);
		}
	}

	public List<ExpressionModel> getAruguments() {
		return this.arguments;
	}
	
	public void setGenusName(String genusName){
		this.genusName = genusName;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getType() {
		return name;
	}

	/**
	 * @param type
	 *            the name to set
	 */
	public void setValue(String value) {
		this.name = value;
	}

	/**
	 * @return the name
	 */
	public String getValue() {
		return name;
	}

	@Override
	public void print(PrintStream out, int indent) {

		// arguments
		for (ExpressionModel arg : arguments) {
			arg.setConnectorId(getId());
			arg.print(out, indent);
		}

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		if (name.equals("TextTurtle") || name.equals("ImageTurtle")
				|| name.equals("SoundTurtle")) {
			out.println("<Block id=\"" + getId()
					+ "\" genus-name=\"new-object-withtext\">");
		} else if (name.equals("ArrayList")) {
			out.println("<Block id=\"" + getId()
					+ "\" genus-name=\"new-listobject\">");
		} else if (name.equals("LinkedList")) {
			out.println("<Block id=\"" + getId()
					+ "\" genus-name=\"new-linkedlistobject\">");
		} else {
			//同一プロジェクト内のクラスのインスタンス生成かどうか確認する

			
			if(genusName != null){
				out.println("<Block id=\"" + getId()
						+ "\" genus-name=\"" + genusName + "\">");
			}else{
			
				out.println("<Block id=\"" + getId()
						+ "\" genus-name=\"new-object\">");	
			}
		}
		
		// label
		makeIndent(out, indent + 1);
		out.println("<Label>" + name + "</Label>");
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
		// plug
		makeIndent(out, indent + 1);
		out.println("<Plug>");
		// blockConnecter
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"plug\" connector-type=\"object\" init-type=\"object\""
				+ " label=\"\" position-type=\"single\" con-block-id=\""
				+ getParent().getId() + "\"/>");
		// end Socket
		makeIndent(out, indent + 1);
		out.println("</Plug>");

		ExCallMethodModel.printArguments(arguments, out, indent, this, null);

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	public String getLabel() {
		return "new " + name + "()";
	}

}
