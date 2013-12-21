package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ExArrayInstanceCreationModel extends ExpressionModel {
	private String name;
	private final int blockHeight = 10;

	private List<ExpressionModel> arguments = new ArrayList<ExpressionModel>();

	public ExArrayInstanceCreationModel() {
		setBlockHeight(blockHeight);
	}

	public void addArgument(ExpressionModel arg) {
		if (arg != null) {
			arg.setParent(this);
			this.arguments.add(arg);
		}
	}

	/**
	 * @return the name
	 */
	@Override
	public String getType() {
		return "void";
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

		if (name.contains("int")) {
			out.println("<Block id=\"" + getId()
					+ "\" genus-name=\"new-arrayobject-numberarray\">");
		} else if (name.contains("String")) {
			out.println("<Block id=\"" + getId()
					+ "\" genus-name=\"new-arrayobject-stringarray\">");
		} else if (name.contains("double")) {
			out.println("<Block id=\"" + getId()
					+ "\" genus-name=\"new-arrayobject-double-numberarray\">");
		} else {
			out.println("<Block id=\"" + getId()
					+ "\" genus-name=\"new-arrayobject-objectarray\">");
		}

		// if (name.equals("TextTurtle") || name.equals("ImageTurtle")
		// || name.equals("SoundTurtle")) {
		// out.println("<Block id=\"" + getId()
		// + "\" genus-name=\"new-object-withtext\">");
		// } else {
		// out.println("<Block id=\"" + getId()
		// + "\" genus-name=\"new-object\">");
		// }
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
		// addDefaultArg
		makeIndent(out, indent + 2);
		out.println();

		// end Socket
		makeIndent(out, indent + 1);
		out.println("</Plug>");

		printArguments(arguments, out, indent, this, null);

		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	public String getLabel() {
		return "new " + name + "()";
	}

	public static void printArguments(List<ExpressionModel> arguments,
			PrintStream out, int indent, ExpressionModel model,
			List<String> argumentLabels) {
		// à¯êî(sockets)
		int argsize = arguments.size();
		if (argsize > 0) {
			model.makeIndent(out, indent + 1);
			out.println("<Sockets num-sockets=\"" + argsize + "\">");
			int i = 0;
			for (ExpressionModel arg : arguments) {
				model.makeIndent(out, indent + 2);
				String connectorType = ElementModel
						.convertJavaTypeToBlockType(arg.getType());
				if (connectorType.equals("void")) {
					connectorType = "poly"; // polyÇÃÇ™É}ÉVÇæÇÎÅD#matsuzawa 2013.01.09
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

}
