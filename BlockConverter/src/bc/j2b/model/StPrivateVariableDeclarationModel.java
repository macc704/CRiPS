package bc.j2b.model;

public class StPrivateVariableDeclarationModel extends
		StVariableDeclarationModel implements Cloneable {

	private final int privateVariableBlockHeight = 40;
	private String modifer = "";

	public StPrivateVariableDeclarationModel() {
		setBlockHeight(privateVariableBlockHeight);
	}

	public void setModifer(String modifer) {
		this.modifer = modifer;
	}

	@Override
	public String getGenusName() {
		String genusName;

		if (isProjectObject()) {
			String type = getType();
			if(type.contains("[]")){
				type = type.substring(0, type.indexOf("[]"));
			}
			genusName = "object-" +type;
		} else {
			genusName = convertJavaTypeToBlockGenusName(super.getType());
		}

		if (genusName.equals("number")) {
			genusName = "int-number";
		}

		if (isArray()) {
			genusName += "-arrayobject";
		}

		return "private-" + modifer + "var-" + genusName;
	}

	@Override
	public String getType() {
		return super.getType();
	}

}
