package bc.classblockfilewriters;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BasicModel {

	private String name;
	private String kind;
	private String initialLabel;
	private String headerLabel;
	private String footerLabel;
	private String color;
	private Map<String, HashSet<String>> connectors;

	public BasicModel() {

	}

	public BasicModel(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color) {
		this.name = name;
		this.kind = kind;
		this.initialLabel = initialLabel;
		this.headerLabel = headerLabel;
		this.footerLabel = footerLabel;
		this.color = color;
		this.connectors = new HashMap<String, HashSet<String>>();
	}

	public void setName(String str) {
		name = str;
	}

	public void setKind(String str) {
		kind = str;
	}

	public void setInitialLabel(String str) {
		initialLabel = str;
	}

	public void setHeaderLabel(String str) {
		headerLabel = str;
	}

	public void footerLabel(String str) {
		footerLabel = str;
	}

	public String getName() {
		return name;
	}

	public void makeIndent(PrintStream out, int number) {
		for (int i = 0; i < number; i++) {
			out.print("\t");
		}
	}

	public String getKind() {
		return kind;
	}

	public String getInitialLabel() {
		return initialLabel;
	}

	public String getHeaderLabel() {
		return headerLabel;
	}

	public String getFooterLabel() {
		return footerLabel;
	}

	public void setColor(String str) {
		color = str;
	}

	public String getColor() {
		return color;
	}

	public void addConnector(String kind, HashSet<String> type) {
		connectors.put(kind, type);
	}

	public Map<String, HashSet<String>> getConnectors() {
		return this.connectors;
	}

	public void printMenuItem(PrintStream out, int lineNumber) {
		makeIndent(out, lineNumber);
		out.println("<BlockGenusMember>" + getName() + "</BlockGenusMember>");
	}

}
