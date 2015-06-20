package workspace;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ClassRelationCaluculator extends ASTVisitor{
	private List<ClassInheritanceRelation> classesRelations = new ArrayList<ClassInheritanceRelation>();

	public boolean visit(MethodDeclaration node){
		return super.visit(node);
	}

	public boolean visit(TypeDeclaration node) {
		String className = node.getName().toString();
		String superClassName = "Object";
		List<String> interfaces = new ArrayList<String>();
		List<String> interfacesArray = new ArrayList<String>();
		if (node.getSuperclassType() != null) {
			superClassName = node.getSuperclassType().toString();
			for (int i = 0; i < node.superInterfaceTypes().size(); i++) {
				interfaces.add(node.superInterfaceTypes().get(i).toString());
				interfacesArray.add(node.superInterfaceTypes().get(i).toString() + "[]");
			}
		}

		classesRelations.add(new ClassInheritanceRelation(className, superClassName, interfaces));
		classesRelations.add(new ClassInheritanceRelation(className + "[]", superClassName + "[]", interfaces));
		return super.visit(node);
	}

	public List<ClassInheritanceRelation> getClassRelations(){
		return this.classesRelations;
	}

}
