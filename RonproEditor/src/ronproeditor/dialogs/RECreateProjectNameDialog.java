/*
 * RECreateProjectNameDialog.java
 * Created on 2007/09/14 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import ronproeditor.REApplication;

/**
 * RECreateProjectNameDialog
 */
public class RECreateProjectNameDialog extends RECreateNameDialog {

	private static final long serialVersionUID = 1L;

	public RECreateProjectNameDialog(REApplication application) {
		super(application);
		setTitle("新規プロジェクト作成");
	}

	protected void validCheck() {
		String text = nameTextField.getText();
		if (text.length() == 0) {
			messageLabel.setText("エラー：名前を入力してください．");
			okButton.setEnabled(false);
		} else if (!getApplication().getSourceManager().canCreateProject(text)) {
			messageLabel.setText("エラー：その名前のプロジェクトはすでに存在します．");
			okButton.setEnabled(false);
		} else if (!isValidCharacterUsed(text)) {
			messageLabel.setText("エラー：Javaで利用できない文字が含まれています．");
			okButton.setEnabled(false);
		} else {
			messageLabel.setText("　");
			okButton.setEnabled(true);
		}
	}

	@Override
	public String getDefaultName() {
		String basename = "NewProject";
		String name = basename;
		for (int i = 2; i < 10; i++) {
			if (getApplication().getSourceManager().canCreateProject(name)) {
				break;
			}
			name = basename + i;
		}
		return name;
	}

	@Override
	protected String getInputTitle() {
		return "プロジェクト名を入力してください";
	}

}
