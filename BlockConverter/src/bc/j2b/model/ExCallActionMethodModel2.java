package bc.j2b.model;

import java.io.PrintStream;

public class ExCallActionMethodModel2 extends ExpressionModel {

	private final int referenceBlockHeight = 40;

	private ExpressionModel receiver;
	private ElementModel callMethod;

	public ExCallActionMethodModel2() {
		setBlockHeight(referenceBlockHeight);
	}

	public void setReceiver(ExpressionModel receiver) {
		receiver.setParent(this);
		this.receiver = receiver;
	}

	public void setCallMethod(ElementModel method) {
		method.setParent(this);
		this.callMethod = method;
	}

	@Override
	public void print(PrintStream out, int indent) {

		if (receiver == null || callMethod == null) {
			throw new RuntimeException();
		}

		// receiver
		receiver.setConnectorId(getId());
		receiver.print(out, indent);

		// callMethod
		callMethod.setConnectorId(getId());
		callMethod.print(out, indent);

		boolean cmd = true;
		String connectorType = "void";
		String genusName = "callActionMethod2";
		if (callMethod instanceof ExpressionModel
				&& !((ExpressionModel) callMethod).getType().equals("void")
				&& !(callMethod instanceof ExVariableSetterModel)) {
			cmd = false;
			connectorType = convertJavaTypeToBlockType(((ExpressionModel) callMethod)
					.getType());

			genusName = "callGetterMethod2";
		}

		// BeforeBlockÇ∆AfterBlockÇåüçıÇ∑ÇÈ
		if (cmd) {
			resolveBeforeAfterBlock(getParent().getParent());
		} else {
			resolveBeforeAfterBlock(getParent());
		}

		// genus-name
		makeIndent(out, indent + 1);
		out.println("<Block id=\"" + getId() + "\" genus-name=\"" + genusName
				+ "\">");
		// label
		makeIndent(out, indent + 2);
		out.println("<Label>" + getLabel() + "</Label>");
		// lineNumber
		makeIndent(out, indent + 1);
		out.println("<LineNumber>" + getLineNumber() + "</LineNumber>");
		// parent
		makeIndent(out, indent + 1);
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

		if (cmd) {
			// beforeBlockId
			if (getPrevious() != -1) {
				makeIndent(out, indent + 2);
				out.println("<BeforeBlockId>" + getPrevious()
						+ "</BeforeBlockId>");
			}
			// afterBlockId
			if (getNext() != -1) {
				makeIndent(out, indent + 2);
				out.println("<AfterBlockId>" + getNext() + "</AfterBlockId>");
			}
		}

		if (!cmd) {
			makeIndent(out, indent + 1);
			// plug
			out.println("<Plug>");
			// blockConnecter
			makeIndent(out, indent + 2);
			out.println("<BlockConnector connector-kind=\"plug\" connector-type=\""
					+ connectorType
					+ "\""
					+ " init-type=\"number\" label=\"\" position-type=\"single\" con-block-id=\""
					+ getParent().getId() + "\"/>");
			// end Plug
			makeIndent(out, indent + 1);
			out.println("</Plug>");
		}

		{// Socket
			makeIndent(out, indent + 2);
			out.println("<Sockets num-sockets=\"2\">");
			// blockConnecters
			makeIndent(out, indent + 3);
			out.print("<BlockConnector connector-kind=\"socket\" connector-type=\"object\""
					+ " init-type=\"object\" label=\"receiver\" position-type=\"single\"");
			if (receiver.getId() != -1) {
				out.print(" con-block-id=\"" + receiver.getId() + "\"");
			}
			out.println("/>");
			if (cmd) {
				makeIndent(out, indent + 3);
				out.print("<BlockConnector connector-kind=\"socket\" connector-type=\"cmd\""
						+ " init-type=\"cmd\" label=\"\" position-type=\"single\"");
				if (callMethod.getId() != -1) {
					out.print(" con-block-id=\"" + callMethod.getId() + "\"");
				}
				out.println("/>");
			} else {
				makeIndent(out, indent + 3);
				out.print("<BlockConnector connector-kind=\"socket\" connector-type=\""
						+ connectorType
						+ "\""
						+ " init-type=\""
						+ connectorType
						+ "\" label=\"\" position-type=\"single\"");
				if (callMethod.getId() != -1) {
					out.print(" con-block-id=\"" + callMethod.getId() + "\"");
				}
				out.println("/>");
			}
			// end Socket
			makeIndent(out, indent + 2);
			out.println("</Sockets>");
		}

		// end Block
		makeIndent(out, indent + 1);
		out.println("</Block>");
	}

	public String getLabel() {
		return "";
	}
}
