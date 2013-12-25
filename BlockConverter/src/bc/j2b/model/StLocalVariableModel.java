package bc.j2b.model;

/**
 * @author yasui
 */
public class StLocalVariableModel extends StVariableDeclarationModel {

	private boolean argument;
	private boolean isExtendedForParameter = false;

	// public StLocalVariableModel() {
	// this(false);
	// }

	public StLocalVariableModel(boolean argument) {
		this.argument = argument;
	}

	public void setIsExtendedForParameter() {
		this.isExtendedForParameter = true;
	}

	@Override
	public String getGenusName() {
		if (isExtendedForParameter) {
			if (argument) {
				return "proc-param-" + getBlockType();
			}
			return "local-var-extentobject";
		} else {
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

}
