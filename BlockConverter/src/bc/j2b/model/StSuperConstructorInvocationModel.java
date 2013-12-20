package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class StSuperConstructorInvocationModel extends StatementModel {
	private String genusName;
	private List<ExpressionModel> parameters = new ArrayList<ExpressionModel>();

	public StSuperConstructorInvocationModel(String genusName) {
		this.genusName = genusName;
	}

	public void addParameter(ExpressionModel parameter) {
		parameters.add(parameter);
	}

	public void print(PrintStream out, int indent) {
		for (ExpressionModel parameter : parameters) {
			parameter.print(out, indent);
		}

		resolveBeforeAfterBlock(getParent());

		makeIndent(out, indent);
		String tag = "<Block id=\"%ID%\" genus-name=\"%GENUS%\">";
		tag = tag.replace("%GENUS%", genusName);
		tag = tag.replace("%ID%", Integer.toString(getId()));
		out.println(tag);
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

		// connector
		// <BlockConnector connector-kind="plug" connector-type="number"
		// init-type="number" label="" position-type="mirror"
		// con-block-id="1005"/>
		// <Sockets num-sockets="3">
		// <BlockConnector connector-kind="socket" connector-type="number"
		// init-type="number" label="" position-type="single"
		// con-block-id="1009"/>
		// <BlockConnector connector-kind="socket" connector-type="number"
		// init-type="number" label="" position-type="single"
		// con-block-id="1010"/>
		// <BlockConnector connector-kind="socket" connector-type="number"
		// init-type="number" label="" position-type="single"
		// con-block-id="1011"/>
		// </Sockets>
		if (parameters.size() > 0) {
			makeIndent(out, indent + 1);
			out.println("<Sockets num-sockets=\"" + parameters.size() + "\">");
			for (ExpressionModel parameter : parameters) {
				makeIndent(out, indent + 2);
				out.println("<BlockConnector connector-kind=\"socket\" connector-type=\""
						+ ElementModel.convertJavaTypeToBlockType(parameter
								.getType())
						+ "\" init-type=\""
						+ ElementModel.convertJavaTypeToBlockType(parameter
								.getType())
						+ "\" label=\"\" position-type=\"single\" con-block-id=\""
						+ parameter.getId() + "\"/>");
			}
			makeIndent(out, indent + 1);
			out.println("</Sockets>");
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

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	public String getLabel() {
		return ";";
	}
}
