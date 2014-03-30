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

		String genusName;
		if (isProjectObject()) {
			genusName = "object-" + getType();
		} else {
			genusName = convertJavaTypeToBlockGenusName(getType());
		}

		if (genusName.equals("number")) {
			genusName = "int-number";
		}

		if (isArray()) {
			genusName += "-arrayobject";
		}

		return "local-var-" + genusName;
	}

}
