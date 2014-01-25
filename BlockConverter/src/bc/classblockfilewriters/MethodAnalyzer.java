package bc.classblockfilewriters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class MethodAnalyzer extends ASTVisitor {

	private List<PublicMethodInfo> methods = new ArrayList<PublicMethodInfo>();
	private String superClassName;
	private List<String> interfacesNames = new LinkedList<String>();
	private List<String> classes = new LinkedList<String>();

	public String getSuperClassName() {
		return this.superClassName;
	}

	public List<String> getInterfacesNames() {
		return this.interfacesNames;
	}

	public List<String> getClasses() {
		return this.classes;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		classes.add(node.getName().toString());
		// TODO Auto-generated method stub
		if (node.getSuperclassType() != null) {
			this.superClassName = node.getSuperclassType().toString();
			for (int i = 0; i < node.superInterfaceTypes().size(); i++) {
				interfacesNames.add(node.superInterfaceTypes().get(i)
						.toString());
			}
		}

		return super.visit(node);
	}

	public boolean visit(MethodDeclaration node) {
		List<String> parameters = new ArrayList<String>();
		if (!node.getName().toString().equals("main")
				&& !(node.getModifiers() == Modifier.PRIVATE)) {

			PublicMethodInfo model = new PublicMethodInfo();
			model.setName(node.getName().toString());

			// オーバーロード対応版のメソッドの名前をセット

			model.setModifier("public");
			if (node.getReturnType2() != null) {
				model.setReturnType(convertBlockConnectorType(node
						.getReturnType2().toString()));
				model.setJavaType(node.getReturnType2().toString());
			}

			String fullName = node.getName().toString() + "[";
			for (int i = 0; i < node.parameters().size(); i++) {
				parameters.add(node.parameters().get(i).toString());
				SingleVariableDeclaration param = (SingleVariableDeclaration) node
						.parameters().get(i);
				String paramType = param.getType().toString();
				if (paramType.equals("double")) {
					paramType = "int";
				}
				fullName += "@" + convertBlockConnectorType(paramType);

			}
			fullName += "]";

			model.setFuLLName(fullName);

			model.setParameters(parameters);
			methods.add(model);
		}
		return super.visit(node);
	}

	// private String convertBlockType(String s) {
	// if ("int".equals(s)) {
	// return "number";
	// } else if ("String".equals(s)) {
	// return "string";
	// } else if ("double".equals(s)) {
	// return "double-number";
	// } else if ("boolean".equals(s)) {
	// return "boolean";
	// } else if ("void".equals(s)) {
	// return "void";
	// }
	// return s;
	// }

	public static String convertBlockConnectorType(String s) {
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
