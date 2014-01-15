package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class PrivateVariableBlockModel extends VariableBlockModel {
	// #ohata　プライベート変数ブロックモデル　

	private String modifer = "";

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

	public void setModifer(String modifer) {
		this.modifer = modifer;
	}

	@Override
	public void print(PrintStream out, int indent) {

		if (getName().contains("listobject")) {
			makeIndent(out, indent);
			ArrayList<Integer> connectorIDs = getConnectorIDs();
			BlockModel newDecl = BlockToJavaAnalyzer.getBlock(connectorIDs
					.get(0));
			// newキーワード変換
			if (newDecl != null) {
				ArrayList<Integer> newDeclConnectorIDs = newDecl
						.getConnectorIDs();
				BlockModel typeDecl = BlockToJavaAnalyzer
						.getBlock(newDeclConnectorIDs.get(0));
				out.print(getType() + "<" + typeDecl.getLabel() + "> "
						+ getLabel());
			} else {
				out.print(getType() + "<");
				ArrayList<String> parameterizedTypes = getParameterizedType();
				for (int i = 0; i < parameterizedTypes.size(); i++) {
					out.print(parameterizedTypes.get(i));
					if (i + 1 < parameterizedTypes.size()) {
						out.print(", ");
					}
				}
				out.print("> " + getLabel());

			}

			for (int connectorID : connectorIDs) {
				if (connectorID != BlockModel.NULL) {
					out.print(" = ");
					BlockToJavaAnalyzer.getBlock(connectorID)
							.print(out, indent);
				}
			}
			out.println(";" + "//" + getComment() + "@(" + getX() + ", "
					+ getY() + ")");
			if (getAfterID() != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
			}
		} else {
			makeIndent(out, indent);

			out.print("private " + modifer + " " + getType() + " " + getLabel());
			ArrayList<Integer> connectorIDs = getConnectorIDs();

			for (int connectorID : connectorIDs) {
				if (connectorID != BlockModel.NULL) {
					out.print(" = ");
					BlockToJavaAnalyzer.getBlock(connectorID)
							.print(out, indent);
				}
			}

			out.println(";" + "//" + getComment() + "@(" + getX() + ", "
					+ getY() + ")");

			if (getAfterID() != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
			}
		}

	}

	public String getPrivateValue() {
		ArrayList<Integer> connectorIDs = getConnectorIDs();
		for (int connectorID : connectorIDs) {
			if (connectorID != BlockModel.NULL) {
				if ("private-var-string".equals(getGenusName())) {
					return "\""
							+ BlockToJavaAnalyzer.getBlock(connectorID)
									.getLabel() + "\"";
				} else {
					return BlockToJavaAnalyzer.getBlock(connectorID).getLabel();
				}
			}
		}
		return null;
	}

}
