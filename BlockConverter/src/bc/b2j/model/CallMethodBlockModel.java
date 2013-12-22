package bc.b2j.model;

import java.io.PrintStream;
import java.util.List;

import bc.BlockConverter;
import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class CallMethodBlockModel extends CommandBlockModel {

	private boolean stub;

	public CallMethodBlockModel() {
		this(false);
	}

	public CallMethodBlockModel(boolean stub) {
		this.stub = stub;
	}

	protected String getMethodName() {
		if (stub == false) {
			return getGenusName();
		} else {// stub
			return getLabel();
		}
	}

	@Override
	public void checkError() {

		List<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			if (connectorID == BlockModel.NULL) {
				throw new RuntimeException("ブロックが完全に組まれていませんA： "
						+ getMethodName());
			}
			BlockToJavaAnalyzer.getBlock(connectorID).checkError();
		}

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	public void printOne(PrintStream out, int indent) {

		String methodName = getMethodName();

		if ("int".equals(methodName) || "double".equals(methodName)
				|| "toString".equals(methodName)) {
			printCast(out, indent);
			return;
		}

		// if (!(isFunctionMethodCallBlock(methodName))) {
		// if (isCommand() && !hasReference()) {
		// makeIndent(out, indent);
		// }

		// 特殊ケース
		if ("empty".equals(methodName)) {
			if (isCommand()) {
				out.print(";");
				out.println();
			}
			return;
		}

		// 特殊ケース2
		if ("hashCode".equals(methodName)) {
			if (getConnectorIDs().size() == 1) {// must be true
				int id = getConnectorIDs().get(0);
				BlockToJavaAnalyzer.getBlock(id).print(out, indent);
				out.print(".");
			}

			out.print("hashCode()");
			if (isCommand()) {
				out.print(";");
				out.println();
			}
			return;
		}

		// 特殊なメソッド名
		if ("cui-print".equals(methodName)) {
			methodName = "System.out.print";
		} else if ("cui-println".equals(methodName)) {
			methodName = "System.out.println";
		} else if ("cui-random".equals(methodName)) {
			methodName = "Math.random";
		} else if ("sqrt".equals(methodName)) {
			methodName = "Math.sqrt";
		} else if ("sin".equals(methodName)) {
			methodName = "Math.sin";
		} else if ("cos".equals(methodName)) {
			methodName = "Math.cos";
		} else if ("tan".equals(methodName)) {
			methodName = "Math.tan";
		} else if ("log".equals(methodName)) {
			methodName = "Math.log";
		} else if ("toRadians".equals(methodName)) {
			methodName = "Math.toRadians";
		}
		out.print(methodName);

		// 引数（なんだろね）
		out.print("(");
		List<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			BlockToJavaAnalyzer.getBlock(connectorID).print(out, indent);
			if (connectorIDs.get(connectorIDs.size() - 1) != connectorID) {
				out.print(",");
			}
		}
		out.print(")");

		// if (!(isFunctionMethodCallBlock(methodName))) {
		if (isCommand()) {
			out.print(";");
			out.println();
		}
	}

	private boolean isCommand() {
		if (stub) {
			return getPlugID() == BlockModel.NULL;
		}
		return !isFunctionMethodCallBlock(getMethodName());
	}

	// private boolean hasReference() {
	// // String methodName = getGenusName();
	// // if (getPlugID() == BlockModel.NULL) {
	// // System.out.print(methodName + ": ");
	// // System.out.println("NULL");
	// // } else {
	// // System.out.print(methodName + ": ");
	// // System.out.println(BlockToJavaAnalyzer.getBlock(getPlugID()));
	// // }
	// // if (getBeforeID() == BlockModel.NULL) {
	// // System.out.print(methodName + ": ");
	// // System.out.println("NULL");
	// // } else {
	// // System.out.print(methodName + ": ");
	// // System.out.println(BlockToJavaAnalyzer.getBlock(getBeforeID()));
	// // }
	// if (getBeforeID() != BlockModel.NULL
	// && BlockToJavaAnalyzer.getBlock(getBeforeID()) instanceof
	// ReferenceBlockModel) {
	// return true;
	// }
	// return false;
	// }

	@Override
	public void print(PrintStream out, int indent) {
		printOne(out, indent);

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}
	}

	private void printCast(PrintStream out, int indent) {

		if (!(isFunctionMethodCallBlock(getMethodName()))) {
			makeIndent(out, indent);
		}

		if (!(BlockToJavaAnalyzer.getBlock(getPlugID()) instanceof VariableBlockModel)
				&& !"toString".equals(getMethodName())) {
			out.print("(" + getMethodName() + ")");
		}
		List<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			BlockToJavaAnalyzer.getBlock(connectorID).print(out, indent);
			if (connectorIDs.get(connectorIDs.size() - 1) != connectorID) {
			}
		}

		if (!(isFunctionMethodCallBlock(getMethodName()))) {
			out.println(";");
		}

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}

	}

	private boolean isFunctionMethodCallBlock(String methodName) {
		for (String name : BlockConverter.FUNCTION_METHODCALL_BLOCKS) {
			if (name.equals(methodName)) {
				return true;
			}
		}
		if (BlockConverter.projectMethods.get(methodName + "("
				+ getConnectorIDs().size() + ")") != null) {
			return true;
		}

		return false;
	}
}
