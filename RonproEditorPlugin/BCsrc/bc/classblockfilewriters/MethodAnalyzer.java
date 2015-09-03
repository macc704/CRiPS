package bc.classblockfilewriters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class MethodAnalyzer extends ASTVisitor {

	private List<PublicMethodInfo> methods = new ArrayList<PublicMethodInfo>();
	private String superClassName = "Object";
	private List<String> interfacesNames = new LinkedList<String>();
	private List<String> classes = new LinkedList<String>();
	private List<ClassVariable> instanceVariables = new ArrayList<ClassVariable>();

	public String getSuperClassName() {
		return this.superClassName;
	}

	public List<String> getInterfacesNames() {
		return this.interfacesNames;
	}

	public List<String> getClasses() {
		return this.classes;
	}

	public List<ClassVariable> getinstanceVariables(){
		return this.instanceVariables;
	}

	public boolean visit(TypeDeclaration node) {
		classes.add(node.getName().toString());
		if (node.getSuperclassType() != null) {
			this.superClassName = node.getSuperclassType().toString();
			for (int i = 0; i < node.superInterfaceTypes().size(); i++) {
				interfacesNames.add(node.superInterfaceTypes().get(i).toString());
			}
		}

		parseFieldVariable(node.getFields());

		return super.visit(node);
	}

	public void parseFieldVariable(FieldDeclaration[] fields){
		for(FieldDeclaration field : fields){
			VariableDeclarationFragment fragment = (VariableDeclarationFragment) field.fragments().get(0);
			String variableType = field.getType().toString();
			String variableName = fragment.getName().toString();
			List<String> modifers = new ArrayList<String>();
			for (Object modifer : field.modifiers()) {
				modifers.add(modifer.toString());
			}

			ClassVariable instancevariable = new ClassVariable(variableType, variableName, modifers);
			instanceVariables.add(instancevariable);
		}
	}



	public boolean visit(MethodDeclaration node) {

		// publicメソッドをmethodsに登録する
		if (!node.getName().toString().equals("main") && !(node.getModifiers() == Modifier.PRIVATE)) {
			// メソッドのモデルに情報を登録する

			PublicMethodInfo model = new PublicMethodInfo();
			// メソッド名をセットする
			setMethodName(model, node);
			// メソッドのパラメータ情報をも出るに登録する
			setMethodParameterInfo(model, node);

			setMethod(model);

		}
		return super.visit(node);
	}

	public void setMethodName(PublicMethodInfo model, MethodDeclaration node) {
		if (node.isConstructor()) {
			model.setName("new-" + node.getName().toString().toLowerCase());
			model.setInitialLabel(node.getName().toString());
			model.setReturnType("object");
			model.setJavaType(node.getName().toString());
		} else {
			model.setName(node.getName().toString());
		}

		// オーバーロード対応版のメソッドの名前をセット
		model.setModifier("public");
		if (node.getReturnType2() != null) {
			model.setReturnType(convertBlockConnectorType(node.getReturnType2().toString()));
			model.setJavaType(node.getReturnType2().toString());
		}
	}

	public void setMethodParameterInfo(PublicMethodInfo model, MethodDeclaration node) {
		List<String> parameters = new ArrayList<String>();
		String fullName = model.getName() + "[";
		for (int i = 0; i < node.parameters().size(); i++) {
			parameters.add(node.parameters().get(i).toString());
			SingleVariableDeclaration param = (SingleVariableDeclaration) node.parameters().get(i);
			String paramType = param.getType().toString();
			if (paramType.equals("double")) {
				paramType = "int";
			}
			fullName += "@" + convertBlockConnectorType(paramType);
		}
		fullName += "]";

		model.setFullName(fullName);

		model.setParameters(parameters);

		model.setParameterJavaType(parameters);
	}

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

