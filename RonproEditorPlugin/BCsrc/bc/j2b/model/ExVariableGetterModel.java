/*
 * VariableGetterModel.java
 * Created on 2011/10/02
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.j2b.model;

import java.io.PrintStream;

/**
 * @author macchan
 */
public class ExVariableGetterModel extends ExpressionModel {

	private StVariableDeclarationModel variable;
	private String genusName = "getter";
	private ExpressionModel index = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see j2b.model.ExpressionModel#getType()
	 */
	@Override
	public String getType() {
		if(index != null){
			return getArrayElementGetterType(variable.getType());
		}else{
			return variable.getType();
		}

	}

	public ExpressionModel getIndex(){
		return this.index;
	}

	/**
	 * @param variable
	 *            the variable to set
	 */
	public void setVariable(StVariableDeclarationModel variable) {
		this.variable = variable;
	}

	public void setIndexModel(ExpressionModel indexModel) {
		this.index = indexModel;
	}


	public void print(PrintStream out, int indent) {

		// print BlockEditor File
		// stubBlock
		String connectorType = getConnectorType(variable.getType());
		String positionType = "mirror";
		if (variable.isArray()) {
			if (index != null) {
				genusName += "-arrayelement" + variable.getGenusName();
				positionType = "single";
			} else {
				genusName += variable.getGenusName();
			}
		} else {
			genusName += variable.getGenusName();
		}

		if (index != null) {
			index.print(out, indent);
			connectorType = getArrayElementGetterType(variable.getType());
			//配列要素アクセスなので、プラグの型を変える
		}

		// if (variable.getGenusName().startsWith("local")) {
		// genusName = genusName + "local-var-" + connectorType;
		// } else {
		// genusName = genusName + "private-var-" + connectorType;
		// }

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
		out.println("<Block id=\"" + getId() + "\" genus-name=\"" + genusName
				+ "\">");
		// label
		makeIndent(out, indent + 2);
		out.println("<Label>" + ElementModel.addEscapeSequence(variable.getName()) + "</Label>");
		if (variable.getGenusName().equals("this")) {
			makeIndent(out, indent + 2);
			out.println("<JavaType>" + ElementModel.addEscapeSequence(variable.getJavaVariableType())
					+ "</JavaType>");
		}
		// lineNumber
		makeIndent(out, indent + 2);
		out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
		// parent
		makeIndent(out, indent + 2);
		ElementModel p = getParent() instanceof StExpressionModel ? getParent()
				.getParent() : getParent();
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

		// Plug
		makeIndent(out, indent + 2);
		out.println("<Plug>");
		// blockConnecters
		makeIndent(out, indent + 3);
		out.print("<BlockConnector connector-kind=\"plug\" connector-type=\""
				+ connectorType + "\"" + " init-type=\"" + connectorType
				+ "\" label=\"\" position-type=\"" + positionType + "\"");
		if (getConnectorId() != -1) {
			out.print(" con-block-id=\"" + getConnectorId() + "\"");
		}
		out.println("/>");
		// end Socket
		makeIndent(out, indent + 2);
		out.println("</Plug>");

		// socket
		if (index != null) {
			makeIndent(out, indent + 2);
			out.println("<Sockets num-sockets=\"1\">");
			makeIndent(out, indent + 3);
			out.print("<BlockConnector connector-kind=\"socket\" connector-type=\""
					+ "number"
					+ "\""
					+ " init-type=\""
					+ "number"
					+ "\" label=\"\" position-type=\"single\"");
			out.print(" con-block-id=\"" + index.getId() + "\"");
			out.println("/>");
			makeIndent(out, indent + 2);
			out.println("</Sockets>");
		}
		// end Block
		makeIndent(out, indent + 1);
		out.println("</Block>");
		makeIndent(out, indent);
		out.println("</BlockStub>");
	}

	public  String getArrayElementGetterType(String type){
		if("int[]".equals(type)){
			return "number";
		}else if("String[]".equals(type)){
			return "string";
		}else if("double[]".equals(type)){
			return "double-number";
		}else if("boolean".equals(type)){
			return "boolean";
		}else{
			return "object";
		}
	}


	public String getLabel() {
		return variable.getName();
	}

}
