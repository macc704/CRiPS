package bc.classblockfilewriters;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ParameterBlockModel extends BasicModel {

	private String javaType;
	private Map<String, String> langSpecProperties = new HashMap<String, String>();

	public ParameterBlockModel(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color,
			String javaType) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);
		HashSet<String> connector = new HashSet<String>();
		connector.add("object");
		addConnector("plug", connector);
		langSpecProperties.put("type", "object");
		langSpecProperties.put("stack-type", "breed-procedure");

		this.javaType = javaType;
	}

	public void print(PrintStream out, int lineNumber) throws Exception {

		out.println("<BlockGenus" + " " + "name=" + "\"" + getName() + "\" "
				+ "kind=" + "\"" + getKind() + "\" " + "initlabel=" + "\""
				+ getInitialLabel() + "\"" + " color=\"" + getColor() + "\">");

		printBlockConnector(out, lineNumber, "plug", "object");

		printStubs(out, lineNumber);

		out.println("<LangSpecProperties>");

		for (String langSpecProperty : langSpecProperties.keySet()) {
			printLangSpecProperty(out, lineNumber + 1, langSpecProperty,
					langSpecProperties.get(langSpecProperty));
		}

		makeIndent(out, lineNumber);
		out.println("</LangSpecProperties>");

		out.println("</BlockGenus>");
	}

	private void printStubs(PrintStream out, int lineNumber) {
		String scope = "local";

		makeIndent(out, lineNumber);
		out.println("<Stubs>");

		printStub("<Stub stub-genus=\"callActionMethod\">",
				"<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-"
						+ langSpecProperties.get("scope")
						+ "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"scope\" value=\"" + scope
						+ "\"></LangSpecProperty>", out, lineNumber);
		printStub("<Stub stub-genus=\"callGetterMethod\">",
				"<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + scope
						+ "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"scope\" value=\"" + scope
						+ "\"></LangSpecProperty>", out, lineNumber);

		printStubs("<Stub stub-genus=\"getter\">",
				"<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + scope
						+ "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"scope\" value=\"" + scope
						+ "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"stack-type\" value=\""
						+ "breed-procedure" + "\"></LangSpecProperty>", out,
				lineNumber);

		printStub("<Stub stub-genus=\"setter\">",
				"<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-set"
						+ scope + "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"scope\" value=\"" + scope
						+ "\"></LangSpecProperty>", out, lineNumber);

		printStubs("<Stub stub-genus=\"getter-arrayelement\">",
				"<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-set"
						+ scope + "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"scope\" value=\"" + scope
						+ "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"stack-type\" value=\""
						+ "breed-procedure" + "\"></LangSpecProperty>", out,
				lineNumber);

		printStub("<Stub stub-genus=\"getter-arrayelement\">",
				"<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-set"
						+ scope + "\"></LangSpecProperty>",
				"<LangSpecProperty key=\"scope\" value=\"" + scope
						+ "\"></LangSpecProperty>", out, lineNumber);
		makeIndent(out, lineNumber);
		out.println("</Stubs>");
	}

	// <BlockGenus name="proc-param-object" kind="param" initlabel="オブジェクト型引数"
	// editable-label="yes" label-unique="yes" is-starter="yes"
	// is-terminator="yes"
	// color="200 200 200">
	// <BlockConnectors>
	// <BlockConnector connector-kind="plug"
	// connector-type="object"></BlockConnector>
	// </BlockConnectors>
	// <Stubs>
	// <Stub stub-genus="getter">
	// <LangSpecProperties>
	// <LangSpecProperty key="vm-cmd-name"
	// value="eval-local"></LangSpecProperty>
	// <LangSpecProperty key="scope" value="local"></LangSpecProperty>
	// <LangSpecProperty key="stack-type"
	// value="breed-procedure"></LangSpecProperty>
	// </LangSpecProperties>
	// </Stub>
	// <Stub stub-genus="setter">
	// <LangSpecProperties>
	// <LangSpecProperty key="vm-cmd-name"
	// value="eval-local"></LangSpecProperty>
	// <LangSpecProperty key="scope" value="local"></LangSpecProperty>
	// </LangSpecProperties>
	// </Stub>
	// <Stub stub-genus="getter-arrayelement">
	// <LangSpecProperties>
	// <LangSpecProperty key="vm-cmd-name"
	// value="eval-local"></LangSpecProperty>
	// <LangSpecProperty key="scope" value="local"></LangSpecProperty>
	// <LangSpecProperty key="stack-type"
	// value="breed-procedure"></LangSpecProperty>
	// </LangSpecProperties>
	// </Stub>
	// <Stub stub-genus="setter-arrayelement">
	// <LangSpecProperties>
	// <LangSpecProperty key="vm-cmd-name"
	// value="eval-local"></LangSpecProperty>
	// <LangSpecProperty key="scope" value="local"></LangSpecProperty>
	// </LangSpecProperties>
	// </Stub>
	// </Stubs>
	// <LangSpecProperties>
	// <LangSpecProperty key="type" value="object"></LangSpecProperty>
	// <LangSpecProperty key="stack-type"
	// value="breed-procedure"></LangSpecProperty>
	// </LangSpecProperties>
	// </BlockGenus>

}
