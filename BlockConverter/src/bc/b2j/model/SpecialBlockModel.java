package bc.b2j.model;

import java.io.PrintStream;

import bc.b2j.analyzer.BlockToJavaAnalyzer;

public class SpecialBlockModel extends CommandBlockModel {

	@Override
	public void print(PrintStream out, int indent) {
		String code = getLabel();
		if (code.endsWith("\n")) {
			code = code.trim();
		}
		if (code.endsWith("\r")) {
			code = code.trim();
		}
		out.print(code);

		if ("special".equals(getGenusName())) {
			out.print(";");
			out.println();
		}

		// これを入れないと後が表示されない．　なんちゅう設計じゃ #matsuzawa 2012.11.07
		if (getAfterID() != BlockModel.NULL) {
			BlockToJavaAnalyzer.getBlock(getAfterID()).print(out, indent);
		}
	}

}
