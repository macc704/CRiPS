package ClassBlockFileModel;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MethodBlockModel extends BasicModel {

	private String methodName;
	private String modifier;
	private String returnType;
	private List<String> parameters = new ArrayList<String>();

	public MethodBlockModel(String name, String kind, String initialLabel,
			String headerLabel, String footerLabel, String color) {
		super(name, kind, initialLabel, headerLabel, footerLabel, color);
	}

	public MethodBlockModel(String name, String modifier, String returnType,
			List<String> parameters) {
		this.methodName = name;
		this.modifier = modifier;
		this.parameters = parameters;
		this.returnType = returnType;
	}

	public MethodBlockModel() {
	}

	public void setName(String name) {
		this.methodName = name;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public void print(PrintStream out, int lineNum) {
		makeIndent(out, lineNum);
		out.println("<ClassMethods>");

		printMethods(out, lineNum);

		makeIndent(out, lineNum);
		out.println("</ClassMethods>");
	}

	private void printMethods(PrintStream out, int lineNum) {
		makeIndent(out, ++lineNum);
		out.println("<MethodProperty name=\"" + methodName + "\" modifer=\""
				+ modifier + "\" returnType=\"" + returnType + "\">");
		lineNum++;
		for (String parameter : parameters) {
			makeIndent(out, lineNum);
			out.println("<Parameter>" + parameter + "</Parameter>");
		}
		makeIndent(out, --lineNum);
		out.println("</MethodProperty>");
	}
}
