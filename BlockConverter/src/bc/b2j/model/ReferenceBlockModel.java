package bc.b2j.model;

import java.io.PrintStream;
import java.util.List;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class ReferenceBlockModel extends BlockModel {

	@Override
	public void checkError() {
		// if (getConnectorIDs().get(0) == BlockModel.NULL) {
		// throw new RuntimeException("ブロックが完全に組まれていませんF： " + getGenusName());
		// }
		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			return;
		}
		BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(0)).checkError();

		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).checkError();
		}

	}

	@Override
	public void print(PrintStream out, int indent) {
		System.out.println(getGenusName());
		if (getGenusName().endsWith("2")) {
			print2(out, indent);
			if (getAfterID() != -1) {
				BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
			}
			return;
		}

		if (getConnectorIDs().get(0) == BlockModel.NULL) {
			if (getAfterID() != -1) {
				BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
			}
			return;
		}

		int blockID = getConnectorIDs().get(0);
		BlockModel model;
		while (blockID != BlockModel.NULL) {
			makeIndent(out, indent);
			out.print(getLabel() + ".");
			model = BlockToJavaAnalyzer.getBlock(blockID);
			methodCallPrint(out, model, indent);
			// if (getGenusName().startsWith("callActionMethod")) {
			// out.println(";");
			// }
			blockID = model.getAfterID();
		}
		if (getAfterID() != -1) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}

	}

	/**
	 */
	private void print2(PrintStream out, int indent) {
		List<Integer> ids = getConnectorIDs();
		if (ids.size() != 2) {
			throw new RuntimeException("connecter count is not 2");
		}

		BlockModel receiver = BlockToJavaAnalyzer.getBlock(getConnectorIDs()
				.get(0));
		BlockModel method = BlockToJavaAnalyzer.getBlock(getConnectorIDs().get(
				1));

		if (receiver == null) {
			throw new RuntimeException("receiver is null");
		}

		if (method == null) {
			throw new RuntimeException("method is null");
		}

		if (method instanceof SetterVariableBlockModel) {
			((SetterVariableBlockModel) method).setIsThisSetter(true);
		}

		receiver.print(out, indent);
		out.print(".");
		method.print(out, indent);

		if (method instanceof SpecialBlockModel) {
			out.println(";");
		}
	}

	public void methodCallPrint(PrintStream out, BlockModel model, int indent) {
		if (model instanceof CallMethodBlockModel) {
			CallMethodBlockModel mcb = (CallMethodBlockModel) model;
			// こうなった#matsuzawa 2012.11.14
			mcb.printOne(out, indent);
			// これでいいじゃん #matsuzawa 2012.11.13
			// mcb.print(out, indent);
			// 何でこうなってんの？ #matsuzawa 2012.11.13 ->
			// 2012.11.14分かった！mcb.printだとその次の次のブロックも印字されてしまうから！
			// ->ということで，CallMethodBlockModelに応急処置．
			// このprintの仕方（IDチェーンでJavaをはくやり方）自体が腐ってる．．

			// out.print(mcb.getGenusName() + "(");
			// ArrayList<Integer> connectorIDs = mcb.getConnectorIDs();
			// for (int connectorID : connectorIDs) {
			// BlockToJavaAnalyzer.getBlock(connectorID)
			// .print(out, indent + 1);
			// if (connectorIDs.get(connectorIDs.size() - 1) != connectorID) {
			// out.print(",");
			// }
			// }
			// out.print(")");
		}
	}

}
