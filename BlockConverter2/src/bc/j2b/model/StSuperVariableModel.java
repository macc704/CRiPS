package bc.j2b.model;

public class StSuperVariableModel extends StVariableDeclarationModel implements
		Cloneable {
	private final int privateVariableBlockHeight = 40;

	public StSuperVariableModel() {
		setBlockHeight(privateVariableBlockHeight);
	}

	@Override
	public String getGenusName() {
		return "super";
	}

	public String getGenusType() {
		return "super";
	}

}
