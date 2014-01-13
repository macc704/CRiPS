package bc.j2b.model;

/**
 * @author yasui
 */
public class StLocalVariableModel extends StVariableDeclarationModel {

	private boolean argument;
	private boolean isArray = false;
	private boolean isProjectObject = false;

	// public StLocalVariableModel() {
	// this(false);
	// }

	public StLocalVariableModel(boolean argument) {
		this.argument = argument;
	}

	public void setArray(boolean array) {
		this.isArray = array;
	}

	public void setProjectObject(boolean projectObject) {
		this.isProjectObject = projectObject;
	}

	@Override
	public String getGenusName() {
		if (argument) {
			return "proc-param-" + getBlockType();
		}

		String genusName;
		if (isProjectObject) {
			genusName = "object-" + getType();
		} else {
			genusName = convertJavaTypeToBlockType(getType());
		}

		if (genusName.equals("number")) {
			genusName = "int-number";
		}

		// object型の場合は、typeによりけりで名前をリネームしよう

		if (isArray) {
			genusName += "-arrayobject";
		}

		return "local-var-" + genusName;
	}

}
