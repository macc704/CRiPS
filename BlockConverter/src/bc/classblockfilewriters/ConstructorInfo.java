package bc.classblockfilewriters;

import java.io.PrintStream;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ConstructorInfo extends PublicMethodInfo {

	public ConstructorInfo(MethodDeclaration node) {
		super(node);
	}

	public void printMethods(PrintStream out, int lineNum) {
		makeIndent(out, ++lineNum);
		out.println("<MethodProperty name=\"" + "new-" + getName() + "\" modifer=\"" + getModifier() + "\" returnType=\"" + getReturnType() + "\" returnJavaType=\"" + getJavaType() + "\">");
		lineNum++;
		for (String parameter : getParameters()) {
			makeIndent(out, lineNum);
			out.println("<Parameter>" + addEscapeSequence(parameter) + "</Parameter>");
		}
		makeIndent(out, --lineNum);
		out.println("</MethodProperty>");
	}

	public String getVcCommandNamen(){
		return "new-" + getName();
	}

}
