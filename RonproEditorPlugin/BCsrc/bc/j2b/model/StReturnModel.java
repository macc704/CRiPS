package bc.j2b.model;

import java.io.PrintStream;

public class StReturnModel extends StatementModel {

	private ExpressionModel returnValue;

	public StReturnModel() {
	}

	public void setReturnValue(ExpressionModel returnValue) {
		this.returnValue = returnValue;
		returnValue.setParent(this);
	}

	public ExpressionModel getReturnValue() {
		return returnValue;
	}

	@Override
	public void print(PrintStream out, int indent) {
		// Child
		if (getReturnValue() != null) {
			getReturnValue().print(out, indent);
		}

		String genusName = "return";
		resolveBeforeAfterBlock(getParent());

		makeIndent(out, indent);
		String tag = "<Block id=\"%ID%\" genus-name=\"%GENUS_NAME%\">";
		tag = tag.replace("%ID%", Integer.toString(getId()));
		tag = tag.replace("%GENUS_NAME%", genusName);
		out.println(tag);

		// label
		// makeIndent(out, indent + 1);
		// out.print("<Label>");
		// out.print(code);
		// out.print("</Label>");
		// out.println();
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
			throw new RuntimeException("wrong return");
		}

		{// Socket
			if (returnValue != null && returnValue.getId() != -1) {
				String connectorType = addEscapeSequence(getConnectorType(returnValue.getType()));
				makeIndent(out, indent + 1);
				out.println("<Sockets num-sockets=\"1\">");
				// blockConnecters
				makeIndent(out, indent + 2);
				out.print("<BlockConnector connector-kind=\"sockets\" connector-type=\""
						+ connectorType
						+ "\""
						+ " init-type=\""
						+ "poly"
						+ "\" label=\"\" position-type=\"single\"");
				out.print(" con-block-id=\"" + returnValue.getId() + "\""
						+ "/>");
				out.println();
				// end Socket
				makeIndent(out, indent + 1);
				out.println("</Sockets>");
			}
		}

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}
}
