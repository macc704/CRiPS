package ClassBlockFileModel;

import java.io.PrintStream;

public class MethodBlockModel extends BasicModel {

	public MethodBlockModel(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);
	}

	public void print(PrintStream out, int lineNum) {
		out.println("<BlockGenus name =\"" + getName() + "\" " + "kind=\""
				+ getKind() + "\" " + "initlabel =\"" + getInitialLabel()
				+ "\" " + "color=\"" + getColor() + "\">");
		out.println("</BlockGenus>");
	}
}
