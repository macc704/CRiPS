/*
 * NewGUIClassCreationWizardPage.java
 * Created on 2007/04/30 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.plugin.gui;

import obpro.plugin.common.NewObproClassWizardPage;
import obpro.plugin.common.Template;

/**
 * NewGUIClassCreationWizardPage
 */
public class NewGUIClassCreationWizardPage extends NewObproClassWizardPage {

	private static final String[] IMPORTS = new String[] { "java.awt.Color",
			"obpro.gui.BCanvas", "obpro.gui.BWindow" };
	private static final String TEMPLATE_PATH = "template/gui/lv5";

	/*
	 * @see obpro.plugin.common.NewObproClassWizardPage#getImportText()
	 */
	protected String getImportText() {
		return Template.createImportText(IMPORTS);
	}

	/*
	 * @see obpro.plugin.common.NewObproClassWizardPage#getTemplateURL()
	 */
	protected String getTemplatePath() {
		return TEMPLATE_PATH;
	}

}
