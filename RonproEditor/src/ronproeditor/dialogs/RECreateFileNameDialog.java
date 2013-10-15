/*
 * RECreateFileNameDialog.java
 * Created on 2007/09/18 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import ronproeditor.REApplication;

/**
 * RECreateFileNameDialog
 */
public class RECreateFileNameDialog extends RECreateNameDialog {

	private static final long serialVersionUID = 1L;

	public RECreateFileNameDialog(REApplication application) {
		super(application);
		setTitle("新規ファイル（クラス）作成");
	}

	protected void validCheck() {
		String text = nameTextField.getText();
		if (text.length() == 0) {
			messageLabel.setText("エラー：名前を入力してください．");
			okButton.setEnabled(false);
		} else if (Character.isLowerCase(text.charAt(0))) {
			messageLabel.setText("エラー：大文字で開始してください．");
			okButton.setEnabled(false);
		} else if (!getApplication().getSourceManager().canCreateFile(text)) {
			messageLabel.setText("エラー：その名前のファイルはすでに存在します．");
			okButton.setEnabled(false);
		} else if (!isValidFirstCharacterUsed(text)) {
			messageLabel.setText("エラー：最初の文字にJavaで利用できない文字が含まれています．");
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
		String basename = "NewClass";
		String name = basename;
		for (int i = 2; i < 10; i++) {
			if (getApplication().getSourceManager().canCreateFile(name)) {
				break;
			}
			name = basename + i;
		}
		return name;
	}

	@Override
	protected String getInputTitle() {
		return "クラス名（.javaを除いた名前）を入力してください";
	}

}
