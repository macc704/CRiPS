package bc.b2j.model;

import java.io.PrintStream;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class RepeatBlockModel extends CommandBlockModel {

	@Override
	public void checkError() {
		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			throw new RuntimeException("ブロックが完全に組まれていませんG： " + getGenusName());
		}

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

		out.print("for(");
		String roopIdentfier = "a" + indent;
		out.print("int " + roopIdentfier + " = 0; " + roopIdentfier + "< ");
		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).print(out,
				indent);
		out.print("; " + roopIdentfier + "++");

		out.println("){");
		if (getConnectorIDs().get(1) != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(1)).print(out,
					indent + 1);
		}
		out.println("}");
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}

	}

}
