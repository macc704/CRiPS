package bc.j2b.analyzer;

import java.util.ArrayList;
import java.util.List;

import bc.j2b.model.StLocalVariableModel;
import bc.j2b.model.StPrivateVariableDeclarationModel;
import bc.j2b.model.StThisVariableModel;
import bc.j2b.model.StVariableDeclarationModel;

public class VariableResolver implements Cloneable {

	private List<StPrivateVariableDeclarationModel> globalVariables = new ArrayList<StPrivateVariableDeclarationModel>();
	private List<StLocalVariableModel> localVariables = new ArrayList<StLocalVariableModel>();
	private StThisVariableModel thisValue = new StThisVariableModel();

	public VariableResolver() {
	}

	public void setThisValue(StThisVariableModel model) {
		thisValue = model;
	}

	public void addGlobalVariable(StPrivateVariableDeclarationModel var) {
		globalVariables.add(var);
	}

	public void addLocalVariable(StLocalVariableModel var) {
		localVariables.add(var);
	}

	public StVariableDeclarationModel resolve(String name) {
		if (name.equals("this")) {
			return thisValue;
		}

		for (int i = 0; i < localVariables.size(); i++) {
			if (name.equals(localVariables.get(i).getName())) {
				return localVariables.get(i);
			}
		}

		for (int i = 0; i < globalVariables.size(); i++) {
			if (name.equals(globalVariables.get(i).getName())) {
				return globalVariables.get(i);
			}
		}

		return null;

		// throw new RuntimeException("cannot resolved name = " + name);
	}

	public StVariableDeclarationModel resolveThisGetter(String name) {
		for (int i = 0; i < globalVariables.size(); i++) {
			if (name.equals(globalVariables.get(i).getName())) {
				return globalVariables.get(i);
			}
		}
		return null;
	}

	public void resetGlobalVariable() {
		globalVariables.clear();
	}

	public void resetLocalVariable() {
		localVariables.clear();
	}

	// TODO ディープクローン作成
	public Object clone() {
		try {
			VariableResolver checkVariableClone = (VariableResolver) super
					.clone();

			checkVariableClone.globalVariables = new ArrayList<StPrivateVariableDeclarationModel>();
			for (StPrivateVariableDeclarationModel element : this.globalVariables) {
				checkVariableClone.globalVariables
						.add((StPrivateVariableDeclarationModel) element
								.clone());
			}

			checkVariableClone.localVariables = new ArrayList<StLocalVariableModel>();
			for (StLocalVariableModel element : this.localVariables) {
				checkVariableClone.localVariables
						.add((StLocalVariableModel) element.clone());
			}

			return checkVariableClone;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
