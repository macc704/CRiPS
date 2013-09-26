package bc.j2b.model;

/**
 * @author yasui
 */
public class StLocalVariableModel extends StVariableDeclarationModel {

	private boolean argument;

	// public StLocalVariableModel() {
	// this(false);
	// }

	public StLocalVariableModel(boolean argument) {
		this.argument = argument;
	}

	@Override
	public String getGenusName() {
		if (argument) {
			return "proc-param-" + getBlockType();
		}
		String genusName = convertJavaTypeToBlockType(getType());
		if (genusName.equals("number")) {
			genusName = "int-number";
		}
		return "local-var-" + genusName;
	}

}
