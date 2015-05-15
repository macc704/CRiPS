package bc.j2b.model;


public class StPrivateVariableDeclarationModel extends
		StVariableDeclarationModel implements Cloneable {

	private final int privateVariableBlockHeight = 40;
	private String accessModifier = "private";

	public StPrivateVariableDeclarationModel() {
		setBlockHeight(privateVariableBlockHeight);
	}

	public void setModifer(String modifer) {
		modifiers.add(modifer);
	}

	@Override
	public String getGenusName() {
		String genusName;

		if (isProjectObject()) {
			genusName = calcProjectObjectType();
		} else {
			genusName = convertJavaTypeToBlockGenusName(super.getType());
		}

		if (genusName.equals("number")) {
			genusName = "int-number";
		}

		if (isArray()) {
			genusName += "-arrayobject";
		}
		
		return accessModifier + "-" + getModifier() + "var-" + genusName;
	}
	
	private String calcProjectObjectType(){
		String type = getType();
		if(type.contains("[]")){
			type = type.substring(0, type.indexOf("[]"));
		}
		return "object-" +type;
	}
	
	public String getModifier(){
		for(String mod : modifiers){
			if(mod.equals("final")){
				return "final-";
			}
		}
		return "";
	}

	@Override
	public String getType() {
		return super.getType();
	}

}
