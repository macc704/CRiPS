package bc.j2b.model;


/**
 * @author yasui
 * 
 */
public class StGlobalVariableModel extends StVariableDeclarationModel implements Cloneable {

	@Override
	public String getGenusName() {
		return "global-var-" + getType();
	}

}