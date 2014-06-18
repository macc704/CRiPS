/*
 * NewGUIClassCreationWizard.java
 * Created on 2007/04/30 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.plugin.gui;

import java.util.ArrayList;

import obpro.plugin.ObproPlugin;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * NewGUIClassCreationWizard
 */
public class NewGUIClassCreationWizard extends NewClassCreationWizard {

	private static final String PAGE_DESCRIPTION = "GUIクラスを作成します．（クラス名（大文字からはじめる）のみを入力してください）";
	private static final String PAGE_ICON = "icons/icon_GUIClass.gif";
	private static final String WINDOW_TITLE = "GUIクラスの作成";

	public NewGUIClassCreationWizard() {
		super(new NewGUIClassCreationWizardPage(), true);
		this.setWindowTitle(WINDOW_TITLE);
		this.setDefaultPageImageDescriptor(ObproPlugin
				.getImageDescriptor(PAGE_ICON));
		this.setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
	}

	/*
	 * @see Wizard#createPages
	 */
	@SuppressWarnings("unchecked")
	public void addPages() {

		super.addPages();

		IWizardPage[] pages = super.getPages();
		NewGUIClassCreationWizardPage page = ((NewGUIClassCreationWizardPage) pages[0]);
		page.init(getSelection());
		page.setTitle(WINDOW_TITLE);
		page.setDescription(PAGE_DESCRIPTION);

		page.setEnclosingTypeSelection(false, false);
		int modifiers = Flags.AccPublic;
		page.setModifiers(modifiers, false);
		page.setSuperInterfaces(new ArrayList(), false);
		page.setMethodStubSelection(true, false, false, false);
		page.setAddComments(true, false);
	}

}
