package bc.b2j.model;

import java.io.PrintStream;
import java.util.ArrayList;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class NoProcparamDataBlockModel extends BlockModel {

	@Override
	public void checkError() {
		// #matsuzawa 応急処置 2012.11.24

		if (getGenusName().indexOf("proc-param") != -1) {
			return;// 素通し
		}
		if (getName().startsWith("getterprivate")
				|| getName().startsWith("this-getter")) {
			return;// #ohata added
		}
		if (!getGenusName().startsWith("getter")) {
			return;
		}
		checkPlugBlock(getPlugID());
	}

	private void checkPlugBlock(int plugID) {
		BlockModel block = BlockToJavaAnalyzer.getBlock(plugID);
		if (block.getBeforeID() != BlockModel.NULL) {
			checkWhetherCreatedVariable(block.getBeforeID());
			return;
		}
		checkPlugBlock(block.getPlugID());
	}

	private void checkWhetherCreatedVariable(int blockID) {
		if (blockID == BlockModel.NULL) {
			throw new RuntimeException("変数宣言する前に変数の値を使おうとしています。");
		}
		BlockModel block = BlockToJavaAnalyzer.getBlock(blockID);
		if (block instanceof LocalVariableBlockModel) {
			if (((LocalVariableBlockModel) block).getLabel().equals(getLabel())) {// 2012.10.23
																					// #matsuzawa
				return; // ok
			}
		}
		checkWhetherCreatedVariable(block.getBeforeID());
	}

	@Override
	public void setLabel(String label) {
		if (label.startsWith("\\ ")) {
			label = label.substring(1);
		}
		if (label.endsWith(" \\")) {
			label = label.substring(0, label.length() - 1);
		}
		// TODO 文字列にダブルクォーテーション、￥マークを入ったらエスケープする
		for (int i = 0; i < label.length(); i++) {
			if (label.charAt(i) == '\\' || label.charAt(i) == '\"'
					|| label.charAt(i) == '\'') {
				label = label.substring(0, i) + "\\"
						+ label.substring(i, label.length());
				i++;
			}
		}
		super.setLabel(label);
	}

	@Override
	public void print(PrintStream out, int indent) {

		// if (getGenusName().startsWith("to")) {

		if ("toIntFromDouble".equals(getGenusName())) {
			BlockModel blockModel = BlockToJavaAnalyzer
					.getBlock(getConnectorIDs().get(0));
			out.print("(int)(");
			blockModel.print(out, indent);
			out.print(")");
		} else if ("toIntFromString".equals(getGenusName())) {
			BlockModel blockModel = BlockToJavaAnalyzer
					.getBlock(getConnectorIDs().get(0));
			out.print("Integer.parseInt(");
			blockModel.print(out, indent);
			out.print(")");
		} else if ("toDoubleFromInt".equals(getGenusName())) {
			BlockModel blockModel = BlockToJavaAnalyzer
					.getBlock(getConnectorIDs().get(0));
			out.print("(double)(");
			blockModel.print(out, indent);
			out.print(")");
		} else if ("toDoubleFromString".equals(getGenusName())) {
			BlockModel blockModel = BlockToJavaAnalyzer
					.getBlock(getConnectorIDs().get(0));
			out.print("Double.parseDouble(");
			blockModel.print(out, indent);
			out.print(")");
		} else if ("toStringFromInt".equals(getGenusName())) {
			BlockModel blockModel = BlockToJavaAnalyzer
					.getBlock(getConnectorIDs().get(0));
			out.print("Integer.toString(");
			blockModel.print(out, indent);
			out.print(")");
		} else if ("toStringFromDouble".equals(getGenusName())) {
			BlockModel blockModel = BlockToJavaAnalyzer
					.getBlock(getConnectorIDs().get(0));
			out.print("Double.toString(");
			blockModel.print(out, indent);
			out.print(")");
		} else if ("toStringFromObject".equals(getGenusName())) {
			BlockModel blockModel = BlockToJavaAnalyzer
					.getBlock(getConnectorIDs().get(0));
			blockModel.print(out, indent);
			out.print(".toString()");
		}

		/*
		 * else { throw new RuntimeException(getGenusName() +
		 * "is not supported for cast"); } }
		 */

		else if ("true".equals(getGenusName())
				|| "false".equals(getGenusName())) {
			out.print(getGenusName());
		} else if ("string".equals(getGenusName())) {
			out.print("\"" + getLabel() + "\"");
		} else if ("double-number".equals(getGenusName())
				|| "number".equals(getGenusName())
				|| getGenusName().startsWith("getter")) {
			out.print(getLabel());
		} else if (getGenusName().startsWith("this-getter")) {
			out.print("this." + getLabel());
		} else if ("pi".equals(getGenusName()) || "e".equals(getGenusName())) {
			out.print("Math." + getGenusName());
		} else if (getGenusName().startsWith("new-object")) {// new-object-withtextを作った
			// #matsuzawa 2012.11.06
			out.print("new " + typeString(getLabel()));
			// 引数（なんだろね） CallMethodからコピー　#matsuzawa 2012.11.06
			out.print("(");
			ArrayList<Integer> connectorIDs = getConnectorIDs();
			boolean first = true;
			for (int connectorID : connectorIDs) {
				BlockModel block = BlockToJavaAnalyzer.getBlock(connectorID);
				if (block == null) {
					continue;
				}
				if (!first) {
					out.print(",");
				}
				block.print(out, indent);
				first = false;
			}
			out.print(")");
		} else if (getGenusName().startsWith("new-arrayobject")) {
			out.print("new "
					+ typeString(getLabel()).substring(0,
							typeString(getLabel()).indexOf("[")));
			ArrayList<Integer> connectorIDs = getConnectorIDs();
			for (int connectorID : connectorIDs) {
				out.print("[");
				BlockModel block = BlockToJavaAnalyzer.getBlock(connectorID);
				block.print(out, indent);
				out.print("]");
			}
		} else {
			out.print("java.awt.Color." + getGenusName());
		}
	}
}
