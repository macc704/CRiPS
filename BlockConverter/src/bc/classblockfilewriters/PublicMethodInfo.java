package bc.classblockfilewriters;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class PublicMethodInfo extends BasicModel {

	private String methodName;
	private String modifier;
	private String returnType;// メソッドの返り値　ただし、ここでのreturnTypeはコネクターの形の5種類になっている
	private String fullName;
	private List<String> parameters = new ArrayList<String>();
	private String javaType;// こちらをメソッドの素の返り値の型とする

	public PublicMethodInfo(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);
	}

	public PublicMethodInfo(String name, String modifier, String returnType,
			List<String> parameters) {
		this.methodName = name;
		this.modifier = modifier;
		this.parameters = parameters;
		this.returnType = returnType;
	}

	public PublicMethodInfo() {
		setColor("255 0 0");
	}

	public void setJavaType(String returnType) {
		this.javaType = returnType;
	}

	public String getJavaType() {
		return this.javaType;
	}

	public void setName(String name) {
		this.methodName = name;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getModifier() {
		return modifier;
	}

	public String getName() {
		return methodName;
	}

	public void print(PrintStream out, int lineNum) {
		makeIndent(out, lineNum);
		out.println("<Method>");
		printMethods(out, lineNum);

		makeIndent(out, lineNum);
		out.println("</Method>");
	}

	private void printMethods(PrintStream out, int lineNum) {
		makeIndent(out, ++lineNum);
		out.println("<MethodProperty name=\"" + methodName + "\" modifer=\""
				+ modifier + "\" returnType=\"" + returnType + "\">");
		lineNum++;
		for (String parameter : parameters) {
			makeIndent(out, lineNum);
			out.println("<Parameter>" + addEscapeSequence(parameter)
					+ "</Parameter>");
		}
		makeIndent(out, --lineNum);
		out.println("</MethodProperty>");
	}
}
