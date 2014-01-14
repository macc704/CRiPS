/*
 * ExCallMethodModel.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author macchan
 * 
 */
public class ExCallMethodModel extends ExpressionModel {

	private final int BLOCK_HEIGHT = 30;

	private String name;

	private List<ExpressionModel> arguments = new ArrayList<ExpressionModel>();
	private List<String> argumentLabels;

	private String label = "";

	public ExCallMethodModel() {
		setBlockHeight(BLOCK_HEIGHT);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addArgument(ExpressionModel arg) {
		// if (arg == null) {
		// throw new RuntimeException();
		// }
		if (arg != null) {
			arg.setParent(this);
			this.arguments.add(arg);
		}
	}

	public List<ExpressionModel> getArguments() {
		return arguments;
	}

	/**
	 * @return the argumentLabels
	 */
	public List<String> getArgumentLabels() {
		return argumentLabels;
	}

	/**
	 * @param argumentLabels
	 *            the argumentLabels to set
	 */
	public void setArgumentLabels(List<String> argumentLabels) {
		this.argumentLabels = argumentLabels;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void print(PrintStream out, int indent) {

		// arguments
		for (ExpressionModel arg : arguments) {
			arg.setConnectorId(getId());
			arg.print(out, indent);
		}

		// 上より後にやることが重要
		if (!(getParent() instanceof StIfElseModel)
				&& !(getParent() instanceof StWhileModel)
				&& !(getParent() instanceof StLocalVariableModel)// 応急処置
				&& !(getParent() instanceof ExCallMethodModel)) {// 応急処置
			resolveBeforeAfterBlock(getParent().getParent());
		}

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);

		out.println("<Block id=\"" + getId() + "\" genus-name=\"" + getName()
				+ "\">");
		if (!label.equals("")) {
			makeIndent(out, indent);
			out.println("<Label>" + label + "</Label>");
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

		// plug
		String plugType = getConnectorType(getType());
		if (VOID.equals(plugType)) {
			// #matsuzawa 2012.10.29
			// for接続する親ブロック(if等の一番上のブロックも含むところがややこしい)
			if (getConnectorId() != -1) {
				makeIndent(out, indent + 1);
				out.println("<BeforeBlockId>" + getConnectorId()
						+ "</BeforeBlockId>");
			}
		} else {// 戻り値の場合
			if (getParent().getId() != getId()) {// 戻り値ありのメソッドが戻り値受け取りなしで呼ばれた場合を除く
													// #matsuzawa TODO うまく動いていない
				makeIndent(out, indent + 1);
				out.println("<Plug>");
				// blockConnecter
				makeIndent(out, indent + 2);
				out.print("<BlockConnector connector-kind=\"plug\" connector-type=\""
						+ plugType
						+ "\""
						+ " init-type=\""
						+ plugType
						+ "\" label=\"\" position-type=\"single\"");
				out.print(" con-block-id=\"" + getParent().getId() + "\"");

				out.println("/>");
				// end Plug
				makeIndent(out, indent + 1);
				out.println("</Plug>");
			}
		}

		printArguments(arguments, out, indent, this, argumentLabels);

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	// public String getLabel() {
	// return name + "(" + getArgumentLabel() + ")";
	// }

	public static void printArguments(List<ExpressionModel> arguments,
			PrintStream out, int indent, ExpressionModel model,
			List<String> argumentLabels) {
		// 引数(sockets)
		int argsize = arguments.size();
		if (argsize > 0) {
			model.makeIndent(out, indent + 1);
			out.println("<Sockets num-sockets=\"" + argsize + "\">");
			int i = 0;
			for (ExpressionModel arg : arguments) {
				model.makeIndent(out, indent + 2);
				String connectorType = ElementModel.getConnectorType(arg
						.getType());
				if (connectorType.equals("void")) {
					connectorType = "poly"; // polyのがマシだろ．#matsuzawa 2013.01.09
				}
				String label = "";
				if (argumentLabels != null && i < argumentLabels.size()) {
					label = argumentLabels.get(i);
				}
				out.print("<BlockConnector connector-kind=\"socket\" connector-type=\""
						+ connectorType
						+ "\""
						+ " init-type=\""
						+ connectorType
						+ "\" label=\""
						+ label
						+ "\" position-type=\"single\"");
				// if (arg.getId() != -1) {
				out.print(" con-block-id=\"" + arg.getId() + "\"");
				// }
				out.println("/>");
				i++;
			}
			model.makeIndent(out, indent + 1);
			out.println("</Sockets>");
		}
	}

	// private String getArgumentLabel() {
	// String label = "";
	// for (ExpressionModel arg : arguments) {
	// if (label.length() > 0) {
	// label += ", ";
	// }
	// label += arg.getLabel();
	// }
	// return label;
	// }

	// @Override
	// public void print(PrintStream out, int indent) {
	// // 2012.10.31 #matsuzawa コネクタの形が変　なんだこの実装は！
	// // String connectorType = "number";
	// // String initType = "number";
	//
	// // BeforeBlockとAfterBlockを検索する
	// resolveBeforeAfterBlock(getParent().getParent());
	//
	// // 2012.10.31 #matsuzawa コネクタの形が変　なんだこの実装は！
	// // set connecoter type
	// // if (name.equals("print")) {
	// // initType = "string";
	// // connectorType = "string";
	// // }
	//
	// // arguments
	// for (ExpressionModel arg : arguments) {
	// arg.setConnectorId(getId());
	// arg.print(out, indent);
	// }
	//
	// // print BlockEditor File
	// // genus-name
	// makeIndent(out, indent);
	// out.println("<Block id=\"" + getId() + "\" genus-name=\"" + name
	// + "\">");
	// // location
	// makeIndent(out, indent + 1);
	// out.println("<Location>");
	// makeIndent(out, indent + 2);
	// out.println("<X>" + getPosX() + "</X>");
	// makeIndent(out, indent + 2);
	// out.println("<Y>" + getPosY() + "</Y>");
	// makeIndent(out, indent + 1);
	// out.println("</Location>");
	// // beforeBlockId
	// if (getPrevious() != -1) {
	// makeIndent(out, indent + 1);
	// out.println("<BeforeBlockId>" + getPrevious() + "</BeforeBlockId>");
	// }
	// // #matsuzawa 2012.10.29
	// if (getConnectorId() != -1) {
	// out.println("<BeforeBlockId>" + getConnectorId()
	// + "</BeforeBlockId>");
	// }
	// // afterBlockId
	// if (getNext() != -1) {
	// makeIndent(out, indent + 1);
	// out.println("<AfterBlockId>" + getNext() + "</AfterBlockId>");
	// }
	// // Sockets
	// if (!("up".equals(getName())) && !("down".equals(getName()))) {
	// makeIndent(out, indent + 1);
	// out.println("<Sockets num-sockets=\"" + arguments.size() + "\">");
	// // blockConnecters
	// for (ExpressionModel arg : arguments) {
	// makeIndent(out, indent + 2);
	//
	// // 2012.10.31 #matsuzawa コネクタの形が変
	// // ここでconnectorTypeを実装！ExVariableSetterModelからコピー
	// String connectorType = convertJavaTypeToBlockType(arg.getType());
	//
	// out.println("<BlockConnector connector-kind=\"sockets\" connector-type=\""
	// + connectorType
	// + "\""
	// + " init-type=\""
	// + /* initType */connectorType
	// + "\" label=\"\" position-type=\"single\" con-block-id=\""
	// + arg.getId() + "\"/>");
	// }
	// // end Socket
	// makeIndent(out, indent + 1);
	// out.println("</Sockets>");
	// }
	// // end Block
	// makeIndent(out, indent);
	// out.println("</Block>");
	// }
	//
	public String getLabel() {
		return name + "(" + getArgumentLabel() + ")";
	}

	private String getArgumentLabel() {
		String label = "";
		for (ExpressionModel arg : arguments) {
			if (label.length() > 0) {
				label += ", ";
			}
			label += arg.getLabel();
		}
		return label;
	}
}
