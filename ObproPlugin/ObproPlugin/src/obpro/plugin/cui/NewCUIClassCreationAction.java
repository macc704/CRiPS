/*
 * NewCUIClassCreationAction.java
 * Created on 2007/05/01 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.plugin.cui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.ui.actions.AbstractOpenWizardAction;
import org.eclipse.ui.INewWizard;

/**
 * NewCUIClassCreationAction
 */
public class NewCUIClassCreationAction extends AbstractOpenWizardAction {

	public NewCUIClassCreationAction() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.actions.AbstractOpenWizardAction#createWizard()
	 */
	protected INewWizard createWizard() throws CoreException {
		return new NewCUIClassCreationWizard();
	}

}
