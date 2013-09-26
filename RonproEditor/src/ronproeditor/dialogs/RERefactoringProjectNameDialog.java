/*
 * RERefactoringProjectNameDialog.java
 * Created on 2007/09/21 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import java.io.File;

import ronproeditor.REApplication;

/**
 * RERefactoringProjectNameDialog
 */
public class RERefactoringProjectNameDialog extends RECreateProjectNameDialog {

	private static final long serialVersionUID = 1L;

	private File file;

	public RERefactoringProjectNameDialog(REApplication application) {
		super(application);
		setTitle("プロジェクト名の変更");
	}

	public void open(File file) {
		this.file = file;
		open();
	}

	@Override
	public String getDefaultName() {
		if (file == null) {
			return super.getDefaultName();
		}
		return file.getName();
	}

}
