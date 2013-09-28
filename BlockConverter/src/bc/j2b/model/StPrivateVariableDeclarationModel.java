package bc.j2b.model;

public class StPrivateVariableDeclarationModel extends StVariableDeclarationModel implements Cloneable{

	private final int privateVariableBlockHeight = 40;
	
	public StPrivateVariableDeclarationModel(){
		setBlockHeight(privateVariableBlockHeight);
	}
	
	@Override
	public String getGenusName() {
		String genusName = convertJavaTypeToBlockType(getType());
		if (genusName.equals("number")) {
			genusName = "int-number";
		}
		return "private-var-" + genusName;
	}

}
