package bc.b2j.model;

import java.io.PrintStream;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class WhileBlockModel extends CommandBlockModel {

	private boolean isDo;

	/**
	 */
	public WhileBlockModel(boolean isDo) {
		this.isDo = isDo;
	}

	@Override
	public void checkError() {
		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			throw new RuntimeException("ブロックが完全に組まれていませんI： " + getGenusName());
		}
		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).checkError();
		if (getConnectorIDs().get(1) != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(1)).checkError();
		}
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	@Override
	public void print(PrintStream out, int indent) {
		makeIndent(out, indent);

		if (!isDo) {
			out.print("while(");
			BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).print(out,
					indent);

			out.println("){");
			if (getConnectorIDs().get(1) != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(1)).print(
						out, indent + 1);
			}
			out.println("}");
			if (getAfterID() != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
			}
		} else {
			out.println("do{");
			BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).print(out,
					indent + 1);

			out.print("}while(");
			if (getConnectorIDs().get(1) != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(1)).print(
						out, indent);
			}
			out.println(");");
			if (getAfterID() != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
			}
		}

	}
}
