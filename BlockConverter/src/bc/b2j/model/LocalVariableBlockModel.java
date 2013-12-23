package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class LocalVariableBlockModel extends VariableBlockModel {

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

	@Override
	public void print(PrintStream out, int indent) {

		// Listの場合、コネクター先を先読みする必要がある
		if (getName().contains("listobject")) {
			makeIndent(out, indent);
			ArrayList<Integer> connectorIDs = getConnectorIDs();
			BlockModel newDecl = BlockToJavaAnalyzer.getBlock(connectorIDs
					.get(0));

			ArrayList<Integer> newDeclConnectorIDs = newDecl.getConnectorIDs();
			BlockModel typeDecl = BlockToJavaAnalyzer
					.getBlock(newDeclConnectorIDs.get(0));

			out.print(getType() + "<" + typeDecl.getLabel() + "> " + getLabel());

			for (int connectorID : connectorIDs) {
				if (connectorID != BlockModel.NULL) {
					out.print(" = ");
					BlockToJavaAnalyzer.getBlock(connectorID)
							.print(out, indent);
				}
			}
			out.println(";");
			if (getAfterID() != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
			}
		} else {

			makeIndent(out, indent);

			out.print(getType() + " " + getLabel());
			ArrayList<Integer> connectorIDs = getConnectorIDs();
			for (int connectorID : connectorIDs) {
				if (connectorID != BlockModel.NULL) {
					out.print(" = ");
					BlockToJavaAnalyzer.getBlock(connectorID)
							.print(out, indent);
				}
			}
			out.println(";");
			if (getAfterID() != BlockModel.NULL) {
				BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
			}
		}
	}

}
