package bc.classblockfilewriters;

import java.io.PrintStream;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ConstructorInfo extends PublicMethodInfo {

	public ConstructorInfo(MethodDeclaration node) {
		super(node);
	}

	/*
	 * must use only default constructor creation
	 */
	public ConstructorInfo(String className, String javaType, String modifier, String returnType, String methodName) {
		super(className, javaType, modifier, "object", methodName);
		this.setgenusName("new-object-" + className + "[]");
	}

	public void printMethods(PrintStream out, int lineNum) {
		makeIndent(out, ++lineNum);
		out.println("<MethodProperty name=\"" + "new-" + getGenusName() + "\" modifer=\"" + getModifier() + "\" returnType=\"" + getReturnType() + "\" returnJavaType=\"" + getJavaType() + "\">");
		lineNum++;
		for (String parameter : getParameters()) {
			makeIndent(out, lineNum);
			out.println("<Parameter>" + addEscapeSequence(parameter) + "</Parameter>");
		}
		makeIndent(out, --lineNum);
		out.println("</MethodProperty>");
	}

	public String getVcCommandNamen(){
		return "new-" + getGenusName();
	}


}
