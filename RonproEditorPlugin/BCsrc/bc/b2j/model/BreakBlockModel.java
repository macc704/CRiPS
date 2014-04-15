package bc.b2j.model;

import java.io.PrintStream;

public class BreakBlockModel extends CommandBlockModel {

	private String name;

	public BreakBlockModel(String name) {
		this.name = name;
	}

	@Override
	public void checkError() {

	}

	@Override
	public void print(PrintStream out, int indent) {
		out.print(name);
		out.print(";");
		out.println();
	}

}
