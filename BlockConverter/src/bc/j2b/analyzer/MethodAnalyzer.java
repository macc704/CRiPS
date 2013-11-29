package bc.j2b.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

import ClassBlockFileModel.MethodBlockModel;

public class MethodAnalyzer extends ASTVisitor {

	private List<MethodBlockModel> methods = new ArrayList<MethodBlockModel>();

	public boolean visit(MethodDeclaration node) {
		List<String> parameters = new ArrayList<String>();
		if (!node.getName().equals("main")
				&& node.getModifiers() == Modifier.PUBLIC) {

			MethodBlockModel model = new MethodBlockModel();
			model.setName(node.getName().toString());

			model.setModifier("public");

			model.setReturnType(node.getReturnType2().toString());
			for (int i = 0; i < node.parameters().size(); i++) {
				System.out.println("add parameter:"
						+ node.parameters().get(i).toString());
				parameters.add(node.parameters().get(i).toString());
			}
			model.setParameters(parameters);
			methods.add(model);
		}
		return super.visit(node);
	}

	public List<MethodBlockModel> getMethods() {
		return methods;
	}

}
