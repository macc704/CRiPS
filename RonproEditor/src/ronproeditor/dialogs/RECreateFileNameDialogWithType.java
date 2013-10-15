/*
 * RECreateFileNameDialog.java
 * Created on 2007/09/18 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import ronproeditor.REApplication;
import ronproeditor.RESourceTemplate;

/**
 * RECreateFileNameDialog
 */
public class RECreateFileNameDialogWithType extends RECreateFileNameDialog {

	private static final long serialVersionUID = 1L;

	private JComboBox<RESourceTemplate> combobox = new JComboBox<RESourceTemplate>();

	public RECreateFileNameDialogWithType(REApplication application) {
		super(application);
		initializeComboBox();
	}

	private void initializeComboBox() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("テンプレート："));
		combobox.setPreferredSize(new Dimension(150, 27));
		panel.add(combobox);

		getContentPane().add(panel);

		pack();
	}

	public void open() {
		combobox.removeAllItems();
		for (RESourceTemplate template : getApplication().getTemplateManager()
				.getTemplates()) {
			combobox.addItem(template);

			// Turtleがあればそれに初期化する
			if (template.getName().equals("Turtle")) {
				combobox.setSelectedItem(template);
			}
		}

		super.open();
	}

	public RESourceTemplate getSelectedTemplate() {
		return (RESourceTemplate) combobox.getSelectedItem();
	}

}
