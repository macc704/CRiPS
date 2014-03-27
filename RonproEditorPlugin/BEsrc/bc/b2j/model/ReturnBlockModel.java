package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;

import bc.b2j.analyzer.BlockConnectorModel;
import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class ReturnBlockModel extends CommandBlockModel {

	public ReturnBlockModel() {
	}

	@Override
	public void checkError() {

	}

	@Override
	public void print(PrintStream out, int indent) {
		out.print("return");

		// Expression
		BlockModel exp = getReturnValue();
		if (exp != null) {
			out.print(" ");
			exp.print(out, indent);
		}

		out.print(";");
		out.println();
	}

	private BlockModel getReturnValue() {
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		int size = connectorIDs.size();
		if (size <= 0) {
			return null;// OK no return value in "void"
		} else if (size == 1) {
			BlockModel exp = BlockToJavaAnalyzer.getBlock(connectorIDs.get(0));
			return exp;
		} else {
			throw new RuntimeException("Return value is too many:"
					+ connectorIDs.size());
		}
	}

	/**
	 */
	public String getReturnType() {
		if (getReturnValue() == null) {
			return "void";
		}

		BlockConnectorModel socket = getSockets().get(0);
		String blocktype = socket.getType();
		if (blocktype.equals("number")) {
			return "int";
		} else if (blocktype.equals("double-number")) {
			return "double";
		} else if (blocktype.equals("boolean")) {
			return "boolean";
		} else if (blocktype.equals("string")) {
			return "String";
		}
		return "void";
	}

}
