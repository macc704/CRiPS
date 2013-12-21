package bc.j2b.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

import ClassBlockFileModel.PublicMethodInfo;

public class MethodAnalyzer extends ASTVisitor {

	private List<PublicMethodInfo> methods = new ArrayList<PublicMethodInfo>();

	public boolean visit(MethodDeclaration node) {
		List<String> parameters = new ArrayList<String>();
		if (!node.getName().equals("main")
				&& !(node.getModifiers() == Modifier.PRIVATE)) {

			PublicMethodInfo model = new PublicMethodInfo();
			model.setName(node.getName().toString());

			model.setModifier("public");
			if (node.getReturnType2() != null) {
				model.setReturnType(convertBlockType(node.getReturnType2()
						.toString()));
			}
			for (int i = 0; i < node.parameters().size(); i++) {
				parameters.add(node.parameters().get(i).toString());
			}
			model.setParameters(parameters);
			methods.add(model);
		}
		return super.visit(node);
	}

	private String convertBlockType(String s) {
		if ("int".equals(s)) {
			return "number";
		} else if ("String".equals(s)) {
			return "string";
		} else if ("double".equals(s)) {
			return "double-number";
		} else if ("boolean".equals(s)) {
			return "boolean";
		} else if ("void".equals(s)) {
			return "void";
		}
		return "object";
	}

	public List<PublicMethodInfo> getMethods() {
		return methods;
	}

	public void setMethod(PublicMethodInfo method) {
		methods.add(method);
	}
}
