package workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassRelationMap {

	private Map<String, ClassInheritanceRelation> classes = new HashMap<String, ClassInheritanceRelation>();

	public ClassRelationMap(){
		classes.put("int[]", new ClassInheritanceRelation("int[]", "Object", new ArrayList<String>()));
		classes.put("String[]", new ClassInheritanceRelation("String[]", "Object", new ArrayList<String>()));
		classes.put("double[]", new ClassInheritanceRelation("double[]", "Object", new ArrayList<String>()));
		classes.put("boolean[]", new ClassInheritanceRelation("boolean[]", "Object", new ArrayList<String>()));
		classes.put("Object[]", new ClassInheritanceRelation("Object[]", "", new ArrayList<String>()));
		classes.put("BCanvas", new ClassInheritanceRelation("BCanvas", "Object", new ArrayList<String>()));
		classes.put("BSound", new ClassInheritanceRelation("BSound", "Object", new ArrayList<String>()));
		classes.put("BWindow", new ClassInheritanceRelation("BWindow", "Object", new ArrayList<String>()));
	}

	public void addClasses(ClassInheritanceRelation relation){
		this.classes.put(relation.getClassName(), relation);
	}

	public boolean couldConnect(String plugType, String socketType){

		if(plugType.equals(socketType)){
			return true;
		}else{
			ClassInheritanceRelation relation = classes.get(plugType);
			if(relation != null && !"Object".equals(relation.getParentClass())){
				return couldConnect(relation.getParentClass(), socketType);
			}
		}
		return false;
	}


}
