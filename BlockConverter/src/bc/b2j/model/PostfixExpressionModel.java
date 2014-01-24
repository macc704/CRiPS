package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class PostfixExpressionModel extends BlockModel {

	@Override
	public void checkError() {
		System.out.println(getBeforeID());
		resolveCreatedVariable(getBeforeID());
		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			throw new RuntimeException("ブロックが完全に組まれていませんE： " + getGenusName());
		}
		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).checkError();
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	private void resolveCreatedVariable(int blockID) {
		if (blockID == BlockModel.NULL) {
			throw new RuntimeException("変数宣言をする前に変数への代入を行っています。");
		}
		BlockModel block = BlockToJavaAnalyzer.getBlock(blockID);
		if (block instanceof LocalVariableBlockModel) {
			return;
		}
		if (getGenusName().indexOf("proc-param") != -1) {
			return;// 素通し
		}
		if (getName().startsWith("getterprivate") || getName().contains("this")
				|| getName().contains("gettersuper")) {
			return;// #ohata added
		}
		if (!getGenusName().startsWith("getter")) {
			return;
		}
		if (getGenusName().contains("array")) {// とりあえず
			return;
		}
		resolveCreatedVariable(block.getBeforeID());
	}

	@Override
	public void print(PrintStream out, int indent) {
		makeIndent(out, indent);

		out.print(getLabel());
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			if (connectorID != BlockModel.NULL) {
				if (BlockToJavaAnalyzer.getBlock(connectorID).getLabel()
						.equals("1")) {
					out.print("++");
				} else if (BlockToJavaAnalyzer.getBlock(connectorID).getLabel()
						.equals("-1")) {
					out.print("--");
				} else if (Integer.parseInt(BlockToJavaAnalyzer.getBlock(
						connectorID).getLabel()) < -1) {
					out.print(" = ");
					out.print(getLabel()
							+ BlockToJavaAnalyzer.getBlock(connectorID)
									.getLabel());
				} else if (Integer.parseInt(BlockToJavaAnalyzer.getBlock(
						connectorID).getLabel()) > 1) {
					out.print(" = ");
					out.print(getLabel()
							+ "+"
							+ BlockToJavaAnalyzer.getBlock(connectorID)
									.getLabel());
				} else if (Integer.parseInt(BlockToJavaAnalyzer.getBlock(
						connectorID).getLabel()) == 0) {
					out.print(" = ");
					out.print(getLabel());
				}

			}
		}

		out.println(";");

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}
	}
}
