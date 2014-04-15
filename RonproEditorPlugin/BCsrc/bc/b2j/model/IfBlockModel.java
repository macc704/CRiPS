package bc.b2j.model;

import java.io.PrintStream;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class IfBlockModel extends CommandBlockModel {

	@Override
	public void checkError() {

		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			throw new RuntimeException("ブロックが完全に組まれていませんB： " + getGenusName());
		}

		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).checkError();

		if (getConnectorIDs().get(1) != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(1)).checkError();
		}

		if ("ifelse".equals(getGenusName())) {
			if (getConnectorIDs().get(2) != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(2))
						.checkError();
			}
		}

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	@Override
	public void print(PrintStream out, int indent) {
		makeIndent(out, indent);

		out.print("if(");

		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).print(out,
				indent);

		out.println("){");
		if (getConnectorIDs().get(1) != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(1)).print(out,
					indent + 1);
		}
		out.print("}");
		if ("ifelse".equals(getGenusName())
				&& getConnectorIDs().get(2) != BlockModel.NULL) {
			BlockModel elseBlock = BlockToJavaAnalyzer
					.getBlock(getConnectorIDs().get(2));
			out.print("else ");
			if (elseBlock instanceof IfBlockModel
					&& elseBlock.getAfterID() == BlockModel.NULL) {// else if
																	// の特殊形
				elseBlock.print(out, 0);
			} else {
				out.println("{");
				elseBlock.print(out, indent + 1);
				out.println("}");
			}
		}
		out.println();
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}

	}
}
