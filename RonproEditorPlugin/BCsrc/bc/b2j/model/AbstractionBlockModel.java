package bc.b2j.model;

import java.io.PrintStream;

import bc.BlockConverter;
import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class AbstractionBlockModel extends BlockModel {

	@Override
	public void setLabel(String label) {
		if (label.startsWith("\\ ")) {
			label = label.substring(1);
		}
		if (label.endsWith(" \\")) {
			label = label.substring(0, label.length() - 1);
		}
		super.setLabel(label);
	}

	@Override
	public void checkError() {
		if (getConnectorIDs().get(0) != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0))
					.checkError();
		}

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}

	}

	@Override
	public void print(PrintStream out, int indent) {

		makeIndent(out, indent);
		out.print("{	//");
		if (isCollapsed()) {
			out.print(BlockConverter.COLLAPSED_BLOCK_LABEL);
		}
		out.println(getLabel());

		if (getConnectorIDs().get(0) != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).print(out,
					indent);
		}

		makeIndent(out, indent);
		out.println("}");
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}

	}
}
