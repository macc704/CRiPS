/*
 * RERefactoringFileNameDialog.java
 * Created on 2007/09/21 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import java.io.File;

import ronproeditor.REApplication;
import ronproeditor.helpers.FileSystemUtil;

/**
 * RERefactoringFileNameDialog
 */
public class RERefactoringFileNameDialog extends RECreateFileNameDialog {

	private static final long serialVersionUID = 1L;

	private File file;

	public RERefactoringFileNameDialog(REApplication application) {
		super(application);
		setTitle("ファイル（クラス）名の変更");
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
		return FileSystemUtil.cutExtension(file);
	}

}
