package bc.j2b.model;

import java.io.PrintStream;

public class ExLeteralModel extends ExpressionModel {

	private String value;
	private final int blockHeight = 10;

	public ExLeteralModel() {
		setBlockHeight(blockHeight);
	}

	/**
	 * @param type
	 *            the name to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the name
	 */
	public String getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bc.j2b.model.ExpressionModel#getType()
	 */
	@Override
	public String getType() {
		String type = getOriginalType();
		if (type.equals("color")) {
			type = "number"; // colorÇÃå`ÇÕî‘çÜ
		}
		return type;
	}

	public String getOriginalType() {
		return super.getType();
	}

	@Override
	public void print(PrintStream out, int indent) {

		String originalType = getOriginalType();
		if (originalType.equals("string")) {
			removeSemiCollonOfValue();
			removeEscapeOfValue();
		}

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		if (originalType.equals("poly")) {
			out.println("<Block id=\"" + getId() + "\" genus-name=\""
					+ "special-expression" + "\">");
			// label
			makeIndent(out, indent + 1);
			out.println("<Label>" + value + "</Label>");
		} else if (originalType.equals("boolean")) {
			out.println("<Block id=\"" + getId() + "\" genus-name=\"" + value
					+ "\">");
		} else if (originalType.equals("color")) {
			out.println("<Block id=\"" + getId() + "\" genus-name=\"" + value
					+ "\">");
		} else {
			out.println("<Block id=\"" + getId() + "\" genus-name=\""
					+ getType() + "\">");
			// label
			makeIndent(out, indent + 1);
			out.println("<Label>" + value + "</Label>");
		}
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
		// plug
		String plugType = getType();
		makeIndent(out, indent + 1);
		out.println("<Plug>");
		// blockConnecter
		makeIndent(out, indent + 2);
		out.println("<BlockConnector connector-kind=\"plug\" connector-type=\""
				+ plugType + "\" init-type=\"" + plugType
				+ "\" label=\"\" position-type=\"mirror\" con-block-id=\""
				+ getParent().getId() + "\"/>");
		// end Socket
		makeIndent(out, indent + 1);
		out.println("</Plug>");
		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	private void removeSemiCollonOfValue() {

		String newValue = value;

		int point = newValue.indexOf("\"");
		if (point != newValue.length() + 1) {
			newValue = newValue.substring(point + 1, value.length());
		}

		point = newValue.lastIndexOf("\"");
		if (point != -1) {
			newValue = newValue.substring(0, point);
		}

		setValue(newValue);
	}

	private void removeEscapeOfValue() {

		String newValue = value;

		for (int i = 0; i < newValue.length(); i++) {
			if ((newValue.charAt(i) == '\\' && newValue.charAt(i + 1) == '\\')
					|| (newValue.charAt(i) == '\\' && newValue.charAt(i + 1) == '\"')
					|| (newValue.charAt(i) == '\\' && newValue.charAt(i + 1) == '\'')) {
				newValue = newValue.substring(0, i)
						+ newValue.substring(i + 1, newValue.length());
			}
		}
		setValue(newValue);
	}

	public String getLabel() {
		return value;
	}
}
