package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ExSpecialExpressionModel extends ExpressionModel {

	private String code;
	private List<ExpressionModel> parameter = new ArrayList<ExpressionModel>();

	public ExSpecialExpressionModel(String code) {
		this.code = code;
	}

	public void addParameter(ExpressionModel param) {
		parameter.add(param);
	}

	@Override
	public void print(PrintStream out, int indent) {
		// System.out.println(code);
		// System.out.println(getParent().getClass());

		String genusName;

		for (ExpressionModel param : parameter) {
			param.print(out, indent);
		}

		if (getParent() instanceof StExpressionModel) {
			resolveBeforeAfterBlock(getParent().getParent());
			genusName = "special";
		} else {
			resolveBeforeAfterBlock(getParent());
			genusName = "special-expression";
		}

		makeIndent(out, indent);
		String tag = "<Block id=\"%ID%\" genus-name=\"%GENUS_NAME%\">";
		tag = tag.replace("%ID%", Integer.toString(getId()));
		tag = tag.replace("%GENUS_NAME%", genusName);
		out.println(tag);

		// label
		makeIndent(out, indent + 1);
		out.print("<Label>");
		out.print(code);
		out.print("</Label>");
		out.println();
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

		if (getParent() instanceof StExpressionModel) {
			// beforeBlockId
			if (getPrevious() != -1) {
				makeIndent(out, indent + 1);
				out.println("<BeforeBlockId>" + getPrevious()
						+ "</BeforeBlockId>");
			}
			// afterBlockId
			if (getNext() != -1) {
				makeIndent(out, indent + 1);
				out.println("<AfterBlockId>" + getNext() + "</AfterBlockId>");
			}
			if (parameter.size() > 0) {
				makeIndent(out, indent + 1);
				out.println("<Sockets num-sockets=\"" + parameter.size()
						+ "\">");
				for (ExpressionModel param : parameter) {
					makeIndent(out, indent + 2);
					out.println("<BlockConnector connector-kind=\"socket\" connector-type=\""
							+ ElementModel.getConnectorType(param.getType())
							+ "\" init-type=\""
							+ param.getType()
							+ "\" label=\"\""
							+ " position-type=\"single\" con-block-id=\""
							+ param.getId() + "\"/>");
				}
				makeIndent(out, indent + 1);
				out.println("</Sockets>");
			}
		} else {
			// plug
			String parentType = "poly";
			out.println("<Plug>");
			makeIndent(out, indent + 2);
			out.println("<BlockConnector connector-kind=\"plug\" connector-type=\""
					+ parentType
					+ "\""
					+ " init-type=\"poly\" position-type=\"single\" con-block-id=\""
					+ getParent().getId() + "\"/>");
			// end Plug
			makeIndent(out, indent + 1);
			out.println("</Plug>");

			if (parameter.size() > 0) {
				makeIndent(out, indent + 1);
				out.println("<Sockets num-sockets=\"" + parameter.size()
						+ "\">");
				for (ExpressionModel param : parameter) {
					makeIndent(out, indent + 2);
					out.println("<BlockConnector connector-kind=\"socket\" connector-type=\""
							+ ElementModel.getConnectorType(param.getType())
							+ "\" init-type=\""
							+ param.getType()
							+ "\" label=\"\""
							+ " position-type=\"single\" con-block-id=\""
							+ param.getId() + "\"/>");
				}
				makeIndent(out, indent + 1);
				out.println("</Sockets>");
			}
		}

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bc.j2b.model.ElementModel#getLabel()
	 */
	@Override
	public String getLabel() {
		return code;
	}
}
