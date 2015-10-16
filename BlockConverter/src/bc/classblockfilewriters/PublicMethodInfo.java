package bc.classblockfilewriters;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bc.classblockfilewriters.model.BasicModel;

public class PublicMethodInfo extends BasicModel {

	private String methodName;
	private String modifier;
	private String returnType;// メソッドの返り値　ただし、ここでのreturnTypeはコネクターの形の5種類になっている
	private String genusName;
	private List<String> parameters = new ArrayList<String>();
	private String javaType;// こちらをメソッドの素の返り値の型とする
	private List<String> parameterJavaTypes = new ArrayList<String>();

	public static String METHOD_NAME = "name";
	public static String RETURN_TYPE = "return-type";
	public static String PARAM_NUM = "param-num";

	public PublicMethodInfo(MethodDeclaration node) {

		setMethodName(node);

		setMethodParameterInfo(node);

		setColor("255 0 0");
	}

	public void setMethodParameterInfo(MethodDeclaration node) {
		List<String> parameters = new ArrayList<String>();
		String genusName = calcGenusName(node, getVcCommandName());

		setgenusName(genusName);

		setParameterJavaType(parameters);

		setParameters(parameters);
	}

	public String calcGenusName(MethodDeclaration node, String genusHederName) {
		String genusName = genusHederName + "[";
		for (int i = 0; i < node.parameters().size(); i++) {
			SingleVariableDeclaration param = (SingleVariableDeclaration) node.parameters().get(i);
			String paramType = param.getType().toString();
			if (paramType.equals("double")) {
				paramType = "int";
			}
			genusName += "@" + MethodAnalyzer.convertBlockConnectorType(paramType);
		}
		genusName += "]";
		return genusName;
	}

	public void setMethodName(MethodDeclaration node) {
		setInitialLabel(node.getName().toString());
		setReturnType("object");
		setJavaType(node.getName().toString());
		setName(node.getName().getFullyQualifiedName());

		// オーバーロード対応版のメソッドの名前をセット
		setModifier("public");
		if (node.getReturnType2() != null) {
			setReturnType(MethodAnalyzer.convertBlockConnectorType(node.getReturnType2().toString()));
			setJavaType(node.getReturnType2().toString());
		}
	}

	public void setJavaType(String returnType) {
		this.javaType = returnType;
	}

	public void setParameterJavaType(List<String> paramTypes) {
		for (String param : paramTypes) {
			this.parameterJavaTypes.add(param.substring(0, param.indexOf(" ")));
		}
	}

	public List<String> getParameterJavaTypes() {
		return parameterJavaTypes;
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

	public void setgenusName(String fullName) {
		this.genusName = fullName;
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

	public String getGenusName() {
		return this.genusName;
	}

	public String getModifier() {
		return modifier;
	}

	public String getName() {
		return methodName;
	}

	public String getVcCommandName(){
		return this.methodName;
	}

	public void print(PrintStream out, int lineNum) {
		makeIndent(out, lineNum);
		out.println("<Method>");
		printMethods(out, lineNum);

		makeIndent(out, lineNum);
		out.println("</Method>");
	}

	public void printMethods(PrintStream out, int lineNum) {
		makeIndent(out, ++lineNum);
		out.println("<MethodProperty name=\"" + methodName + "\" modifer=\"" + modifier + "\" returnType=\"" + returnType + "\" returnJavaType=\"" + javaType + "\">");
		lineNum++;
		for (String parameter : parameters) {
			makeIndent(out, lineNum);
			out.println("<Parameter>" + addEscapeSequence(parameter) + "</Parameter>");
		}
		makeIndent(out, --lineNum);
		out.println("</MethodProperty>");
	}

	public String getKeyForResolver(){
		String paramSize = Integer.toString(getParameters().size());
		if (paramSize.equals("0")) {
			paramSize = "";
		}

		return getName() + "(" + paramSize + ")";
	}

	public Element createMethodElement(Document doc){
		Element methodElement = doc.createElement("Method");
		methodElement.setAttribute(METHOD_NAME, methodName);
		methodElement.setAttribute(RETURN_TYPE, returnType);
		methodElement.setAttribute(PARAM_NUM, String.valueOf(parameters.size()));

		return methodElement;
	}
}
