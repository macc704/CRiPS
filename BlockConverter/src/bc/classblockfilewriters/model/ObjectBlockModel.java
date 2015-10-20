package bc.classblockfilewriters.model;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bc.classblockfilewriters.PublicMethodCommandWriter;
import bc.classblockfilewriters.PublicMethodInfo;

public class ObjectBlockModel extends BasicModel {

	protected Map<String, List<PublicMethodInfo>> methods = new HashMap<String, List<PublicMethodInfo>>();
	protected String className;
	protected Map<String, String> langSpecProperties = new LinkedHashMap<String, String>();
	protected String superClassName;

	public ObjectBlockModel(String name, String kind, String initialLabel, String headerLabel, String footerLabel, String color) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);
		if (kind.startsWith("local")) {
			langSpecProperties.put("scope", "local");
		} else {
			langSpecProperties.put("scope", "global");
		}
		langSpecProperties.put("type", "object");
		langSpecProperties.put("is-owned-by-breed", "yes");
		langSpecProperties.put("is-monitorable", "yes");
	}

	public void setSuperClassName(String name) {
		this.superClassName = name;
	}

	public String getSuperClassName() {
		return this.superClassName;
	}

	public Map<String, List<PublicMethodInfo>> getMethods() {
		return methods;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return this.className;
	}

	public void setMethods(Map<String, List<PublicMethodInfo>> methods) {
		this.methods = methods;
	}

	public void print(PrintStream out, int lineNumber) throws Exception {

		out.println("<BlockGenus" + " " + "name=" + "\"" + getGenusName() + "\" " + "kind=" + "\"" + getKind() + "\" " + "initlabel=" + "\"" + getInitialLabel() + "\"");
		makeIndent(out, ++lineNumber);
		out.println(" header-label=" + "\"" + getHeaderLabel() + "\" " + "footer-label=" + "\"" + getFooterLabel() + "\" " + "editable-label=\"yes\" " + "label-unique=\"yes\" " + "color=\"" + getColor() + "\">");

		makeIndent(out, lineNumber);
		out.println("<description>");

		makeIndent(out, ++lineNumber);
		out.println("<text>");

		makeIndent(out, ++lineNumber);
		out.println("disctiption");

		makeIndent(out, --lineNumber);
		out.println("</text>");

		makeIndent(out, --lineNumber);
		out.println("</description>");

		// コンストラクタを求める
		String constructor = getConstructorName();
		if (constructor != null) {
			printBlockConnectors(out, lineNumber + 1, "socket", "object", constructor, getClassName());
		} else {
			if (className.contains("[]")) {
				printBlockConnectors(out, lineNumber + 1, "socket", "object", "new-arrayobject-objectarray", getClassName());
			} else {
				printBlockConnectors(out, lineNumber + 1, "socket", "object", "new-object", getClassName());
			}
		}

		printStubs(out, lineNumber);

		makeIndent(out, lineNumber);
		out.println("<LangSpecProperties>");

		for (String langSpecProperty : langSpecProperties.keySet()) {
			printLangSpecProperty(out, lineNumber + 1, langSpecProperty, langSpecProperties.get(langSpecProperty));
		}

		makeIndent(out, lineNumber);
		out.println("</LangSpecProperties>");
		if (methods != null) {
			makeIndent(out, lineNumber++);
			out.println("<ClassMethods>");
			for (String key : methods.keySet()) {
				makeIndent(out, lineNumber++);
				out.println("<ClassName name=\"" + key + "\">");

				for (PublicMethodInfo method : methods.get(key)) {
					method.print(out, lineNumber);
				}
				makeIndent(out, --lineNumber);
				out.println("</ClassName>");

			}
			makeIndent(out, --lineNumber);
			out.println("</ClassMethods>");
		}
		makeIndent(out, lineNumber);
		out.println("<JavaType>" + className + "</JavaType>");
		out.println("<SuperClassName>" + superClassName + "</SuperClassName>");

		out.println("</BlockGenus>");
		out.println();
		// コマンドブロック情報の書き出し
		if (!className.contains("[]")) {
			PublicMethodCommandWriter commandWriter = new PublicMethodCommandWriter();
			if (methods != null) {
				for (String key : methods.keySet()) {
					for (PublicMethodInfo method : methods.get(key)) {
						commandWriter.setMethods(method);
						commandWriter.printCommand(out);
					}
				}
			}
		}
		out.println();
	}

	public void printForUni(PrintStream out, int lineNumber) throws Exception {
		out.println("<BlockGenus" + " " + "name=" + "\"" + getGenusName() + "\" " + "kind=" + "\"" + getKind() + "\" " + "initlabel=" + "\"" + getInitialLabel() + "\"");
		makeIndent(out, ++lineNumber);
		out.println(" header-label=" + "\"" + getHeaderLabel() + "\" " + "footer-label=" + "\"" + getFooterLabel() + "\" " + "editable-label=\"yes\" " + "label-unique=\"yes\" " + "color=\"" + getColor() + "\">");

		makeIndent(out, lineNumber);
		out.println("<description>");

		makeIndent(out, ++lineNumber);
		out.println("<text>");

		makeIndent(out, ++lineNumber);
		out.println("disctiption");

		makeIndent(out, --lineNumber);
		out.println("</text>");

		makeIndent(out, --lineNumber);
		out.println("</description>");

		// コンストラクタを求める
		String constructor = getConstructorName();
		if (constructor != null) {
			printBlockConnectors(out, lineNumber + 1, "socket", "object", constructor, getClassName());
		} else {
			if (className.contains("[]")) {
				printBlockConnectors(out, lineNumber + 1, "socket", "object", "new-arrayobject-objectarray", getClassName());
			} else {
				printBlockConnectors(out, lineNumber + 1, "socket", "object", "new-object", getClassName());
			}
		}

		printStubs(out, lineNumber);

		makeIndent(out, lineNumber);
		out.println("<LangSpecProperties>");

		for (String langSpecProperty : langSpecProperties.keySet()) {
			printLangSpecProperty(out, lineNumber + 1, langSpecProperty, langSpecProperties.get(langSpecProperty));
		}

		makeIndent(out, lineNumber);
		out.println("</LangSpecProperties>");
		if (methods != null) {
			makeIndent(out, lineNumber++);
			out.println("<ClassMethods>");
			for (String key : methods.keySet()) {
				makeIndent(out, lineNumber++);
				out.println("<CategoryName name=\"" + key + "\">");

				for (PublicMethodInfo method : methods.get(key)) {
					method.printForUni(out, lineNumber);
				}
				makeIndent(out, --lineNumber);
				out.println("</CategoryName>");
			}
			makeIndent(out, --lineNumber);
			out.println("</ClassMethods>");
		}
		makeIndent(out, lineNumber);
		out.println("<Type>" + className + "</Type>");
		out.println("<SuperClassName>" + superClassName + "</SuperClassName>");

		out.println("</BlockGenus>");
		out.println();
		// コマンドブロック情報の書き出し
		if (!className.contains("[]")) {
			PublicMethodCommandWriter commandWriter = new PublicMethodCommandWriter();
			if (methods != null) {
				for (String key : methods.keySet()) {
					for (PublicMethodInfo method : methods.get(key)) {
						commandWriter.setMethods(method);
						commandWriter.printCommandForUni(out);
					}
				}
			}
		}
		out.println();
	}

	private String getConstructorName() {
		List<PublicMethodInfo> methods = this.methods.get(className);
		if (methods != null) {
			for (PublicMethodInfo method : methods) {
				if (method.getGenusName().startsWith("new-")) {
					return method.getGenusName();
				}
			}
		}

		return null;
	}

	protected void printStubs(PrintStream out, int lineNumber) {
		makeIndent(out, lineNumber);
		out.println("<Stubs>");
		printAllStubs(out, lineNumber);

		makeIndent(out, lineNumber);
		out.println("</Stubs>");
	}

	protected void printAllStubs(PrintStream out, int lineNumber) {
		printStub("<Stub stub-genus=\"callActionMethod\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);
		printStub("<Stub stub-genus=\"callGetterMethod\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);

		printStub("<Stub stub-genus=\"callBooleanMethod\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);

		printStub("<Stub stub-genus=\"callDoubleMethod\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);

		printStub("<Stub stub-genus=\"callStringMethod\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);

		printStub("<Stub stub-genus=\"callObjectMethod\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);

		printStub("<Stub stub-genus=\"getter\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);

		printStub("<Stub stub-genus=\"setter\">", "<LangSpecProperty key=\"vm-cmd-name\" value=\"eval-set" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", "<LangSpecProperty key=\"scope\" value=\"" + langSpecProperties.get("scope") + "\"></LangSpecProperty>", out, lineNumber);
	}

	protected void printBlockConnectors(PrintStream out, int lineNumber, String connectorKind, String connectorType, String defaultGenusName, String DefaultLabel) {
		makeIndent(out, lineNumber);
		out.println("<BlockConnectors>");

		makeIndent(out, ++lineNumber);
		out.println("<BlockConnector label=\"初期値\" connector-kind=\"" + connectorKind + "\" connector-type=\"" + connectorType + "\" connector-javatype=\"" + className + "\">");

		makeIndent(out, ++lineNumber);
		out.println("<DefaultArg genus-name=\"" + defaultGenusName + "\" label=\"" + DefaultLabel + "\"></DefaultArg>");

		makeIndent(out, --lineNumber);
		out.println("</BlockConnector>");

		makeIndent(out, --lineNumber);
		out.println("</BlockConnectors>");
	}

}
