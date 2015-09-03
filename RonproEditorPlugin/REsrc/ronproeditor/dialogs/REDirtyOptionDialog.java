/*
 * REDirtyOptionDialog.java
 * Created on 2007/09/21 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ronproeditor.REApplication;

/**
 * REDirtyOptionDialog
 */
public class REDirtyOptionDialog extends REDialog {

	private static final long serialVersionUID = 1L;

	private JButton saveButton = new JButton("保存する");
	private JButton noSaveButton = new JButton("破棄する");

	private JLabel label = new JLabel();

	public REDirtyOptionDialog(REApplication application) {
		super(application.getFrame(), application);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("セーブされていないドキュメントがあります．");
		initializeViews();
	}

	private void initializeViews() {
		getContentPane().setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout());
		JPanel dummy = new JPanel();
		dummy.setPreferredSize(new Dimension(20, 50));
		northPanel.add(dummy);
		northPanel.add(label);
		getContentPane().add(northPanel, BorderLayout.NORTH);
		JPanel centerPanel = new JPanel();
		getContentPane().add(centerPanel);

		centerPanel.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getApplication().doSave();
				dispose();
			}
		});

		centerPanel.add(noSaveButton);
		noSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
	}

	private void refreshViews() {
		StringBuffer buf = new StringBuffer();
		buf.append("ファイル");
		buf.append(getApplication().getSourceManager().getCurrentFile()
				.getName());
		buf.append("は変更されています．");
		buf.append("保存しますか？");
		label.setText(buf.toString());

		pack();
	}

	public void open() {
		refreshViews();
		setWindowAtCenter();
		setVisible(true);
	}

}
