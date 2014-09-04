package bc.classblockfilewriters;

import java.io.PrintStream;
import java.util.HashSet;

public class ConvertBlockModel extends BasicModel {
	
	private String javaType;

	public ConvertBlockModel(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color, String javaType) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);

		// connectorの登録
		HashSet<String> plugConnectorType = new HashSet<String>();
		plugConnectorType.add("object");
		addConnector("plug", plugConnectorType);

		// socketの登録
		HashSet<String> socketConnectorType = new HashSet<String>();
		socketConnectorType.add("object");
		addConnector("socket", socketConnectorType);

	}

	public void print(PrintStream out, int lineNumber) throws Exception {

		out.println("<BlockGenus" + " " + "name=" + "\"" + getName() + "\" "
				+ "kind=" + "\"" + getKind() + "\" " + "initlabel=" + "\""
				+ getInitialLabel() + "\"" + " color=\"" + getColor() + "\">");

		printBlockConnectors(out, lineNumber);

		makeIndent(out, lineNumber);
		out.println("<JavaType>" + javaType + "</JavaType>");
		
		out.println("</BlockGenus>");

	}

}
