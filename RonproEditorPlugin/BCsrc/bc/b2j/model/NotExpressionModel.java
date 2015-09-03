package bc.b2j.model;

import java.io.PrintStream;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class NotExpressionModel extends BlockModel {

	@Override
	public void checkError() {
		resolveCreatedVariable(getBeforeID());
		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			throw new RuntimeException("ブロックが完全に組まれていませんNot： " + getGenusName());
		}
		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).checkError();
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	private void resolveCreatedVariable(int blockID) {

	}

	@Override
	public void print(PrintStream out, int indent) {
		makeIndent(out, indent);

		out.print("!");
		out.print("(");
		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).print(out,
				indent);
		out.print(")");
		// out.println(";");

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}
	}
}
