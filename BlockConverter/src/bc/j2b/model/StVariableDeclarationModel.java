package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class StVariableDeclarationModel extends StatementModel implements
		Cloneable {

	private int variableBlockHeight = 40;

	private String name;
	private String type;
	private boolean isProjectObject = false;
	private List<String> parameterizedType = new ArrayList<String>();
	private String javaVariableType;

	private boolean isArray = false;

	private ExpressionModel initializer;

	public void setArray(boolean array) {
		this.isArray = array;
	}

	public boolean isArray() {
		return this.isArray;
	}

	public void setParameterizedType(String type) {
		parameterizedType.add(type);
	}

	public StVariableDeclarationModel() {
		setBlockHeight(variableBlockHeight);
	}

	public ExpressionModel getInitializer() {
		return this.initializer;
	}

	public void setProjectObject(boolean projectObject) {
		this.isProjectObject = projectObject;
	}

	public void setJavaVariableType(String javaVariableType) {
		this.javaVariableType = javaVariableType;
	}

	public boolean isProjectObject() {
		return this.isProjectObject;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	public void setInitializer(ExpressionModel model) {
		model.setParent(this);
		this.initializer = model;
	}

	@Override
	public void print(PrintStream out, int indent) {
		int initializerId = -1;
		if (initializer != null) {
			initializer.setConnectorId(getId());
			initializer.print(out, indent);
			initializerId = initializer.getId();
		}
		// BeforeBlockÇ∆AfterBlockÇåüçıÇ∑ÇÈ
		resolveBeforeAfterBlock(getParent());

		String connectorType = getBlockType();

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\""
				+ getGenusName() + "\">");
		// label
		makeIndent(out, indent + 1);
		out.println("<Label>" + name + "</Label>");
		// variable type
		makeIndent(out, indent + 1);

		out.println("<HeaderLabel>" + ElementModel.addEscapeSequence(getType())
				+ "</HeaderLabel>");

		makeIndent(out, indent + 1);
		out.println("<JavaType>" + addEscapeSequence(javaVariableType) + "</JavaType>");

		{// 2013 09/26 ohata tag for line comment
			// comment
			makeIndent(out, indent + 1);
			out.println("<LineComment>" + getComment() + "</LineComment>");
		}

		{// 2013 09/26 hakamata tag for linenumber and parent block parent
			// blockÇÕébíË
			// lineNumber
			makeIndent(out, indent + 1);
			out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
			// parent
			makeIndent(out, indent + 1);
			ElementModel p = getParent() instanceof StExpressionModel ? getParent()
					.getParent() : getParent();
			if (p != null) {// ohata privateÇ…ParentÇÕë∂ç›ÇµÇ»Ç¢ÇΩÇﬂÅAÇ±Ç±Ç≈Ç ÇÈÇ€Ç≈Ç‹Ç∑Å@
				out.println("<ParentBlock>" + p.getId() + "</ParentBlock>");
			}
		}

		{//
			// parameterizedtype
			makeIndent(out, indent + 1);
			out.println("<ParameterizedType>");

			for (String type : parameterizedType) {
				makeIndent(out, indent + 2);

				out.println("<Type>" + type + "</Type>");
			}

			makeIndent(out, indent + 1);
			out.println("</ParameterizedType>");
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
		out.print("<BlockConnector connector-kind=\"socket\" connector-type=\""
				+ connectorType + "\"" + " init-type=\"" + connectorType
				+ "\" label=\"\" position-type=\"single\"");
		if (initializerId != -1) {
			out.print(" con-block-id=\"" + initializerId + "\"");
		}
		out.println("/>");
		// end Socket
		makeIndent(out, indent + 1);
		out.println("</Sockets>");
		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	public StVariableDeclarationModel clone() {
		try {
			return (StVariableDeclarationModel) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public String getGenusName() {
		return null;
	}

	public String getLabel() {
		if (initializer != null) {
			return type + " " + name + " = " + initializer.getLabel();
		} else {
			return type + " " + name;
		}
		// return name;
	}

	public void printAsArgument(PrintStream out, int indent) {
		String blockType = getBlockType();

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\""
				+ getArgGenusName() + "\">");
		// label
		makeIndent(out, indent + 1);
		out.println("<Label>" + name + "</Label>");

		makeIndent(out, indent + 1);
		out.println("<JavaType>" + javaVariableType + "</JavaType>");

		{// 2013 09/26 ohata tag for line comment
			// comment
			makeIndent(out, indent + 1);
			out.println("<LineComment>" + getComment() + "</LineComment>");
		}

		{// 2013 09/26 hakamata tag for linenumber and parent block parent
			// blockÇÕébíË
			// lineNumber
			makeIndent(out, indent + 1);
			out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
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

		// plug
		makeIndent(out, indent + 1);
		out.println("<Plug>");
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"plug\" connector-type=\""
				+ blockType + "\"" + " init-type=\"" + blockType
				+ "\" position-type=\"single\" con-block-id=\""
				+ getParent().getId() + "\"/>");
		// end Plug
		makeIndent(out, indent + 1);
		out.println("</Plug>");

		// line comment

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	public String getBlockType() {
		return getConnectorType(type);
	}

	private String getArgGenusName() {
		// if(blockType.equals("number")){
		// return "proc-param-number";
		// }
		return "proc-param-" + getBlockType();
	}

}
