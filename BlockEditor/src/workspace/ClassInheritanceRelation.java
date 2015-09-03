package workspace;

import java.util.ArrayList;
import java.util.List;

public class ClassInheritanceRelation {

	private String className;
	private List<String> implementsClasses = new ArrayList<String>();
	private String parentClass = "Object";

	public ClassInheritanceRelation(String className, String parentClass, List<String> implementsCasses){
		this.className = className;
		this.parentClass = parentClass;
		this.implementsClasses = implementsCasses;
	}

	public String getClassName(){
		return this.className;
	}

	public String getParentClass(){
		return this.parentClass;
	}

	public List<String> implementsClasses(){
		return this.implementsClasses;
	}
}
