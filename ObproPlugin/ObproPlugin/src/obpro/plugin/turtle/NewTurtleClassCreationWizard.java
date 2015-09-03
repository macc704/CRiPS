/*
 * NewGUIClassCreationWizard.java
 * Created on 2007/04/30 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.plugin.turtle;

import java.util.ArrayList;

import obpro.plugin.ObproPlugin;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * NewGUIClassCreationWizard
 */
public class NewTurtleClassCreationWizard extends NewClassCreationWizard {

	public static final String TURTLE_PACKAGE_PATH = "obpro.turtle";
	public static final String TURTLE_CLASS_PATH = TURTLE_PACKAGE_PATH + ".Turtle";

	public NewTurtleClassCreationWizard() {
		super(new NewTurtleClassCreationWizardPage(), true);
		this.setWindowTitle("タートルクラスの作成");
		this.setDefaultPageImageDescriptor(ObproPlugin
				.getImageDescriptor("icons/BigIcon_TurtleClass.gif"));
		this.setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
	}

	/*
	 * @see Wizard#createPages
	 */
	@SuppressWarnings("unchecked")
	public void addPages() {

		super.addPages();

		IWizardPage[] pages = super.getPages();
		NewTurtleClassCreationWizardPage page = ((NewTurtleClassCreationWizardPage) pages[0]);
		page.init(getSelection());
		page.setTitle("タートルクラスの作成");
		page.setDescription("タートルクラスを作成します．（クラス名（大文字からはじめる）のみを入力してください）");

		page.setEnclosingTypeSelection(false, false);
		int modifiers = Flags.AccPublic;
		page.setModifiers(modifiers, false);
		page.setSuperClass(TURTLE_CLASS_PATH, false);// タートルを継承
		page.setSuperInterfaces(new ArrayList(), false);
		page.setMethodStubSelection(true, false, false, false);
		page.setAddComments(true, false);
	}

}
