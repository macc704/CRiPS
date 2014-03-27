package bc.j2b.model;

import java.io.PrintStream;

public class ExInfixModel extends ExpressionModel {

	private final int blockHeight = 5;

	private ExpressionModel left;
	private ExpressionModel right;

	private String operator;

	public ExInfixModel() {
		setBlockHeight(blockHeight);
	}

	public void setLeftExpression(ExpressionModel model) {
		model.setParent(this);
		this.left = model;
	}

	public void setRightExpression(ExpressionModel model) {
		model.setParent(this);
		this.right = model;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

	public String getType() {
		if (operator.equals("equals")) {
			return "boolean";
		}

		if ("==".equals(operator) || "!=".equals(operator)
				|| "<".equals(operator) || "<=".equals(operator)
				|| ">".equals(operator) || ">=".equals(operator)
				|| "&&".equals(operator) || "||".equals(operator)) {
			return "boolean";
		}

		if (left.getType().toLowerCase().equals("string")
				|| right.getType().toLowerCase().equals("string")) {
			return "string";
		}

		if (getSocketType().equals("double-number")) {
			return "double-number";
		} else if (getSocketType().equals("number")) {
			return "number";
		} else {
			return "string";// TODO 本当にこれでいいのか 2012.11.13 #matsuzawa
		}

		// else if ("-".equals(operator) || "*".equals(operator)
		// || "/".equals(operator)) {
		// return "number";
		// } else if ("+".equals(operator) &&
		// "sum".equals(resolvePlusOperator())) {
		// return "number";
		// } else {
		// return "string";
		// }
		// throw new RuntimeException("not supported operator : " + operator);
	}

	public String getPlugType() {
		// System.out.println(getType() + ", " + left.getType() + ", "
		// + right.getType());
		return getType();
	}

	public String getSocketType() {
		// ループになるからダメ
		// if (getType().equals("string")) {
		// return "string";
		// }
		if (left.getType().toLowerCase().equals("boolean")
				|| right.getType().toLowerCase().equals("boolean")) {
			return "boolean";
		} else if (left.getType().toLowerCase().equals("string")
				|| right.getType().toLowerCase().equals("string")) {
			return "string";
		} else if (left.getType().equals("double-number")
				|| right.getType().equals("double-number")) {
			return "double-number";
		} else if (left.getType().equals("double")
				|| right.getType().equals("double")) {
			return "double-number";
		} else if (left.getType().equals("number")
				|| right.getType().equals("number")) {
			return "number";
		}

		String type = left.getType();// TODO ひとまず左側の型にあわせる
		if ("int".equals(type)) {
			return "number";
		}
		return type;
	}

	public String getGenusName() {

		if (operator.equals("equals")) {
			return "equals-string";
		}

		if (getSocketType().equals("boolean")) {
			if (operator.equals("==")) {
				return "equals-boolean";
			} else if (operator.equals("!=")) {
				return "not-equals-boolean";
			}
		}

		if (getSocketType().equals("double-number")) {
			if (operator.equals("<")) {
				return "lessthan-double";
			} else if (operator.equals("<=")) {
				return "lessthanorequalto-double";
			} else if (operator.equals(">")) {
				return "greaterthan-double";
			} else if (operator.equals(">=")) {
				return "greaterthanorequalto-double";
			} else if (operator.equals("==")) {
				return "equals-number-double";
			} else if (operator.equals("!=")) {
				return "not-equals-number-double";
			} else if (operator.equals("+")) {
				return "sum-double";
			} else if (operator.equals("-")) {
				return "difference-double";
			} else if (operator.equals("*")) {
				return "product-double";
			} else if (operator.equals("/")) {
				return "quotient-double";
			} else if (operator.equals("%")) {
				return "remainder-double";
			}
		}

		// if (getSocketType().equals("number")) {
		if (operator.equals("<")) {
			return "lessthan";
		} else if (operator.equals("<=")) {
			return "lessthanorequalto";
		} else if (operator.equals(">")) {
			return "greaterthan";
		} else if (operator.equals(">=")) {
			return "greaterthanorequalto";
		} else if (operator.equals("==")) {
			return "equals-number";
		} else if (operator.equals("!=")) {
			return "not-equals-number";
		} else if (operator.equals("&&")) {
			return "and";
		} else if (operator.equals("||")) {
			return "or";
		} else if (operator.equals("+")) {
			return resolvePlusOperator();
		} else if (operator.equals("-")) {
			return "difference";
		} else if (operator.equals("*")) {
			return "product";
		} else if (operator.equals("/")) {
			return "quotient";
		} else if (operator.equals("%")) {
			return "remainder";
		}
		// }

		throw new RuntimeException("not supported: " + operator);
	}

	private String resolvePlusOperator() {
		if (getType().equals("string")) {
			return "string-append";
		} else {
			return "sum";
		}

		// ElementModel model = getParent();
		// String leftType = left.getType();
		// if ("int".equals(leftType)) {
		// leftType = "number";
		// }
		// String rightType = right.getType();
		// if ("int".equals(rightType)) {
		// rightType = "number";
		// }
		// if ("number".equals(leftType) && "number".equals(rightType)) {
		// return "sum";
		// }
		// while (model != null) {
		// if (model instanceof ExCallMethodModel) {
		// String name = ((ExCallMethodModel) model).getName();
		// if ("print".equals(name)) {
		// return "string-append";
		// }
		// } else if (model instanceof StVariableDeclarationModel) {
		// String type = ((StVariableDeclarationModel) model).getType();
		// if ("String".equals(type)) {
		// return "string-append";
		// }
		// } else if (model instanceof ExVariableSetterModel) {
		// String type = ((ExVariableSetterModel) model).getType();
		// if ("String".equals(type)) {
		// return "string-append";
		// }
		// }
		// model = model.getParent();
		// }
		// return "sum";
	}

	@Override
	public void print(PrintStream out, int indent) {
		// left Operand
		left.setType(getSocketType());
		left.setConnectorId(getId());
		left.print(out, indent);
		// right Operand
		right.setType(getSocketType());
		right.setConnectorId(getId());
		right.print(out, indent);

		// print BlockEditor File
		// genus-name
		makeIndent(out, indent);
		out.println("<Block id=\"" + getId() + "\" genus-name=\""
				+ getGenusName() + "\">");
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
		makeIndent(out, indent + 1);
		out.println("<Plug>");
		// blockConnector
		makeIndent(out, indent + 2);
		out.print("<BlockConnector connector-kind=\"plug\" connector-type=\""
				+ getPlugType() + "\"" + " init-type=\"" + getPlugType()
				+ "\" label=\"\" position-type=\"mirror\"");
		if (getConnectorId() != -1) {
			out.print(" con-block-id=\"" + getConnectorId() + "\"");
		}
		out.println("/>");
		// end plug
		makeIndent(out, indent + 1);
		out.println("</Plug>");
		// Socket
		makeIndent(out, indent + 1);
		out.println("<Sockets num-sockets=\"2\">");
		// left blockConnector
		makeIndent(out, indent + 2);
		out.print("<BlockConnector connector-kind=\"sockets\" connector-type=\""
				+ getSocketType()
				+ "\""
				+ " init-type=\""
				+ getSocketType()
				+ "\" label=\"\" position-type=\"bottom\"");
		if (left.getId() != -1) {
			out.print(" con-block-id=\"" + left.getId() + "\"");
		}
		out.println("/>");
		// right blockConnector
		makeIndent(out, indent + 2);
		out.print("<BlockConnector connector-kind=\"sockets\" connector-type=\""
				+ getSocketType()
				+ "\""
				+ " init-type=\""
				+ getSocketType()
				+ "\" label=\"\" position-type=\"bottom\"");
		if (right.getId() != -1) {
			out.print(" con-block-id=\"" + right.getId() + "\"");
		}
		out.println("/>");
		// end Socket
		makeIndent(out, indent + 1);
		out.println("</Sockets>");
		// end Block
		makeIndent(out, indent);
		out.println("</Block>");
	}

	public String getLabel() {
		return left.getLabel() + " " + operator + " " + right.getLabel();
	}

}
