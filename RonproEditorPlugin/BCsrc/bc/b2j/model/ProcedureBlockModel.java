package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import bc.b2j.analyzer.BlockToJavaAnalyzer;
import bc.classblockfilewriters.MethodAnalyzer;

public class ProcedureBlockModel extends CommandBlockModel {

	@Override
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

	public String getKey() {
		String key = getLabel() + "[";
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int i = 0; i + 1 < connectorIDs.size(); i++) {
			int connectorID = connectorIDs.get(i);
			String type = BlockToJavaAnalyzer.getBlock(connectorID).getType();
			if (type.equals("double-number") || type.equals("double")) {
				type = "int";
			}
			type = MethodAnalyzer.convertBlockConnectorType(type);
			key += "@" + type;
		}
		key += "]";
		return key;
	}

	@Override
	public void print(PrintStream out, int indent) {
		String returnType = getReturnType();
		// makeIndent(out, indent);

		// BlockToJavaAnalyzer.getBlock(373).print(out, indent);
		// main の先頭ラインを獲得して、それより前に書き込む必要

		out.print("public");
		out.print(" ");
		out.print(returnType);
		out.print(" " + getLabel() + "(");

		// 引数の定義
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int i = 0; i + 1 < connectorIDs.size(); i++) {
			int connectorID = connectorIDs.get(i);
			BlockToJavaAnalyzer.getBlock(connectorID).print(out, indent);
			if (connectorIDs.get(i + 1) != BlockModel.NULL) {
				out.print(", ");
			}
		}

		out.println(") {");
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent + 1);
		}
		makeIndent(out, indent);
		if (isCollapsed()) {
			out.print("}//" + getComment() + " @(" + getX() + ", " + getY()
					+ ")" + " [close]");
		} else {
			out.print("}//" + getComment() + " @(" + getX() + ", " + getY()
					+ ")" + " [open]");
		}

	}

	public String getReturnType() {
		// Calc!! このモデルには無理があるけど．．． #matsuzawa

		for (BlockModel child : getAllChildren()) {
			if (child instanceof ReturnBlockModel) {
				String returnType = ((ReturnBlockModel) child).getReturnType();
				if (!returnType.equals("null")) {
					return returnType;
				}
			}
		}
		return "void";
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
