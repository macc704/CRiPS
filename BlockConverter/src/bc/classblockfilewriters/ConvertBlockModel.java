package bc.classblockfilewriters;

import java.io.PrintStream;
import java.util.HashSet;

public class ConvertBlockModel extends BasicModel {

	public ConvertBlockModel(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);

		// connector‚Ì“o˜^
		HashSet<String> plugConnectorType = new HashSet<String>();
		plugConnectorType.add("object");
		addConnector("plug", plugConnectorType);

		// socket‚Ì“o˜^
		HashSet<String> socketConnectorType = new HashSet<String>();
		socketConnectorType.add("object");
		addConnector("socket", socketConnectorType);

	}

	public void print(PrintStream out, int lineNumber) throws Exception {

		out.println("<BlockGenus" + " " + "name=" + "\"" + getName() + "\" "
				+ "kind=" + "\"" + getKind() + "\" " + "initlabel=" + "\""
				+ getInitialLabel() + "\"" + " color=\"" + getColor() + "\">");

		printBlockConnectors(out, lineNumber);

		out.println("</BlockGenus>");

		// <BlockGenus name="toStringFromDouble" kind="function"
		// initlabel="•¶Žš—ñŒ^‚É•ÏŠ·‚·‚é" color="45 201 255">
		// <BlockConnectors>
		// <BlockConnector connector-kind="plug"
		// connector-type="string"></BlockConnector>
		// <BlockConnector connector-kind="socket"
		// connector-type="double-number"></BlockConnector>
		// </BlockConnectors>
		// </BlockGenus>
	}

}
