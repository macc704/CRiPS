package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yasui
 * 
 */

public class ClassModel extends ElementModel {

	private String name;
	private List<StMethodDeclarationModel> methods = new ArrayList<StMethodDeclarationModel>();
	private String superClass;
	
	// #ohata added parameter
	private List<StConstructorDeclarationModel> constructors = new ArrayList<StConstructorDeclarationModel>();
	private List<StPrivateVariableDeclarationModel> privateValues = new ArrayList<StPrivateVariableDeclarationModel>();
	

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * set super class of this class
	 * 
	 * @param superClass
	 */
	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	public void addPrivateVariable(StPrivateVariableDeclarationModel privateValue){
		this.privateValues.add(privateValue);
	}
	
	public void addMethod(StMethodDeclarationModel method) {
		method.setParent(this);
		this.methods.add(method);
	}
	//#ohata added 
	public void addConstructor(StConstructorDeclarationModel constructor){
		constructor.setParent(this);
		this.constructors.add(constructor);
	}

	public List<StMethodDeclarationModel> getMethods() {
		return methods;
	}
	
	public List<StPrivateVariableDeclarationModel> getPrivateValues(){
		return privateValues;
	}
	
	public List<StConstructorDeclarationModel> getConstructors(){
		return constructors;
	}

	@Override
	public void print(PrintStream out, int indent) {

		// page
		makeIndent(out, indent);
		out.println("<Page page-name=\""
				+ name
				+ "\" page-color=\" 40 40 40\" page-width=\"4000\" page-infullview=\"yes\" page-drawer=\""
				+ name + "\" page-superClass=\"" + superClass + "\">");
		// pageBlocks
		makeIndent(out, indent + 1);
		out.println("<PageBlocks>");
		
		for (int i = 0; i < privateValues.size(); i++) {
			ElementModel child = privateValues.get(i);
			child.print(out, indent + 2);
		}

		for (int i = 0; i < constructors.size(); i++) {
			ElementModel child = constructors.get(i);
			child.print(out, indent + 2);
		}
		
		for (int i = 0; i < methods.size(); i++) {
			ElementModel child = methods.get(i);
			child.print(out, indent + 2);
		}
		// end pageBlocks
		makeIndent(out, indent + 1);
		out.println("</PageBlocks>");
		// end page
		makeIndent(out, indent);
		out.println("</Page>");
	}

}
