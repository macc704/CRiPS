package bc.b2j.model;

import java.io.PrintStream;

public class TypeBlockModel extends BlockModel {

	public void print(PrintStream out, int indent) {
		out.print(getLabel());
	}

}
