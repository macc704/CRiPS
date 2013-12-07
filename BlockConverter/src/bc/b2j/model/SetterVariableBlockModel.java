package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class SetterVariableBlockModel extends CommandBlockModel {

	@Override
	public void checkError() {
		// #matsuzawa 応急処置 2012.11.24
		if (getGenusName().indexOf("proc-param") != -1) {
			return;// 素通し
		}
		// private valueのセッターの場合、同じ構造化ブロック内に変数が無いため、エラーが発生する
		if (getName().startsWith("setterprivate")
				|| getName().startsWith("this-setterprivate")) {
			return;// #ohata added
		}
		resolveCreatedVariable(getBeforeID());
		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			throw new RuntimeException("ブロックが完全に組まれていませんH： " + getGenusName());
		}
		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).checkError();
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	private void resolveCreatedVariable(int blockID) {
		if (blockID == BlockModel.NULL) {
			throw new RuntimeException("変数宣言する前に変数への代入を行っています");
		}
		BlockModel block = BlockToJavaAnalyzer.getBlock(blockID);
		if (block instanceof LocalVariableBlockModel) {
			return;
		}
		// プライベート変数の場合はbeforeが無いため、処理を終える
		if (block.getGenusName().startsWith("private-")) {// #ohata added
			return;
		}
		resolveCreatedVariable(block.getBeforeID());
	}

	@Override
	public void print(PrintStream out, int indent) {
		makeIndent(out, indent);
		if (getName().startsWith("this-setter")) {
			out.print("this." + getLabel());
		} else {
			out.print(getLabel());
		}
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			if (connectorID != BlockModel.NULL) {
				out.print(" = ");
				BlockToJavaAnalyzer.getBlock(connectorID).print(out, indent);
			}
		}

		out.println(";");

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}
	}
}
