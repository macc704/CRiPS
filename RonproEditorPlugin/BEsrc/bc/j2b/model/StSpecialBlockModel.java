package bc.j2b.model;

import java.io.PrintStream;

@Deprecated
public class StSpecialBlockModel extends StatementModel {

	private String code;

	public StSpecialBlockModel(String code) {
		this.code = code;
	}

	@Override
	public void print(PrintStream out, int indent) {

		resolveBeforeAfterBlock(getParent());

		makeIndent(out, indent);
		String tag = "<Block id=\"%ID%\" genus-name=\"special\" label=\"%LABEL%\">";
		tag = tag.replace("%ID%", Integer.toString(getId()));
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
		ElementModel p = getParent() instanceof StExpressionModel ? getParent().getParent() : getParent();
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

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

}
