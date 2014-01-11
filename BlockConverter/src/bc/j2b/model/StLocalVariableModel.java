package bc.j2b.model;

/**
 * @author yasui
 */
public class StLocalVariableModel extends StVariableDeclarationModel {

	private boolean argument;
	private boolean isArray = false;

	// public StLocalVariableModel() {
	// this(false);
	// }

	public StLocalVariableModel(boolean argument) {
		this.argument = argument;
	}

	public void setArray(boolean array) {
		this.isArray = array;
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

		if (isArray) {
			genusName += "-arrayobject";
		}

		return "local-var-" + genusName;
	}

}
