package bc.classblockfilewriters;

import java.util.ArrayList;
import java.util.List;

public class ClassVariable {

	private String type;
	private String name;
	private List<String> modifers = new ArrayList<String>();

	public ClassVariable(String type, String name, List<String> modifers){
		this.type = type;
		this.name = name;
		this.modifers = modifers;
	}


	public String getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getModifer() {
		return this.modifers;
	}

	public boolean isConstantVariable() {
		for (String modifer : modifers) {
			if ("final".equals(modifer) || "static".equals(modifer)) {
				return true;
			}
		}
		return false;
	}

	public String toString(){
		String s = "type:" + type + " name:" + name + "modifer";
		for(String modifer : modifers){
			s += modifer + ",";
		}
		return s;
	}
}
