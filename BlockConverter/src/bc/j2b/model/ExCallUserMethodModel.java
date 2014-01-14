/*
 * ExCallMethodModel.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.j2b.model;

import java.io.PrintStream;

/**
 * @author macchan
 * 
 */
public class ExCallUserMethodModel extends ExCallMethodModel {

	public ExCallUserMethodModel() {
		super();
	}

	@Override
	public void print(PrintStream out, int indent) {
		// arguments
		for (ExpressionModel arg : getArguments()) {
			arg.setConnectorId(getId());
			arg.print(out, indent);
		}

		// 上より後にやることが重要
		if (!(getParent() instanceof StIfElseModel)
				&& !(getParent() instanceof StWhileModel)
				&& !(getParent() instanceof StLocalVariableModel)// 応急処置
				&& !(getParent() instanceof ExCallUserMethodModel)
				&& !(getParent() instanceof StReturnModel)) {// 応急処置
			resolveBeforeAfterBlock(getParent().getParent());
		}

		// user method special(1)
		makeIndent(out, indent);
		out.print("<BlockStub>");
		out.print("<StubParentName>" + getName() + "</StubParentName>");
		out.print("<StubParentGenus>procedure</StubParentGenus>");
		out.println();

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\""
				+ /* user method special(2)-> */"callerprocedure" + "\">");

		// user method special(3)
		makeIndent(out, indent);
		out.println("<Label>" + getName() + "</Label>");
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

		ExCallMethodModel.printArguments(getArguments(), out, indent, this,
				getArgumentLabels());

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");

		// user method special(4)
		makeIndent(out, indent);
		out.println("</BlockStub>");
	}
}
