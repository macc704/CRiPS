package bc.j2b.model;

public class StThisVariableModel extends StVariableDeclarationModel implements
		Cloneable {
	private final int privateVariableBlockHeight = 40;

	public StThisVariableModel() {
		setBlockHeight(privateVariableBlockHeight);
	}

	@Override
	public String getGenusName() {
		return "this";
	}

	public String getGenusType() {
		return "this";
	}

}
