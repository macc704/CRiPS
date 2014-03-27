package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class PrivateProcedureBlockModel extends CommandBlockModel {
	@Override
	
	
	// #ohata プライベート変数を宣言するためのブロック　今は利用しない 
	public void checkError() {

		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			if (connectorID != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(connectorID).checkError();
			}
		}

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}
	}

	@Override
	public void print(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("//private valiable");
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent + 1);
		}
		makeIndent(out, indent);
		out.print("");
	}

	public List<BlockModel> getAllChildren() {
		return getChildren(this);
	}

	public List<BlockModel> getChildren(BlockModel model) {
		List<BlockModel> children = new ArrayList<BlockModel>();
		if (model.getAfterID() != -1) {
			BlockModel child = BlockToJavaAnalyzer.getBlock(model.getAfterID());
			if (child != null) {
				children.add(child);
				children.addAll(getChildren(child));
			}
		}
		for (int id : model.getConnectorIDs()) {
			BlockModel child = BlockToJavaAnalyzer.getBlock(id);
			if (child != null) {
				children.add(child);
				children.addAll(getChildren(child));
			}
		}
		return children;
	}

}
