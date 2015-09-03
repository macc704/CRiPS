package edu.mit.blocks.codeblocks;

public class MethodInformation {
	private String genusName;
	private String label;

	public MethodInformation(String genusName,
			String label) {
		this.genusName = genusName;
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

//	public String getModifer() {
//		return this.modifer;
//	}
//
//	public String getReturnType() {
//		return this.returnType;
//	}
//
	public String getGenusName() {
		return this.genusName;
	}
//
//	public List<String> getParameters() {
//		return this.parameters;
//	}

}