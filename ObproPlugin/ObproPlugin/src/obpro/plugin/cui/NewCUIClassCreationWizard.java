/*
 * NewCUIClassCreationWizard.java
 * Created on 2007/04/30 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.plugin.cui;

import java.util.ArrayList;

import obpro.plugin.ObproPlugin;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * NewCUIClassCreationWizard
 */
public class NewCUIClassCreationWizard extends NewClassCreationWizard {

	public NewCUIClassCreationWizard() {
		super(new NewCUIClassCreationWizardPage(), true);
		this.setWindowTitle("オブプロCUIクラスの作成");
		this.setDefaultPageImageDescriptor(ObproPlugin
				.getImageDescriptor("icons/icon_CUIClass.gif"));
		this.setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
	}

	/*
	 * @see Wizard#createPages
	 */
	@SuppressWarnings("unchecked")
	public void addPages() {

		super.addPages();

		IWizardPage[] pages = super.getPages();
		NewCUIClassCreationWizardPage page = ((NewCUIClassCreationWizardPage) pages[0]);
		page.init(getSelection());
		page.setTitle("オブプロCUIクラスの作成");
		page.setDescription("オブプロCUIクラスを作成します．（クラス名（大文字からはじめる）のみを入力してください）");

		page.setEnclosingTypeSelection(false, false);
		int modifiers = Flags.AccPublic;
		page.setModifiers(modifiers, false);
		page.setSuperInterfaces(new ArrayList(), false);
		page.setMethodStubSelection(true, false, false, false);
		page.setAddComments(true, false);
	}

}
