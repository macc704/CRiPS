/*
 * RECreateNameDialog.java
 * Created on 2007/09/14 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import ronproeditor.REApplication;

/**
 * RECreateNameDialog
 */
public abstract class RECreateNameDialog extends REDialog {

	private static final long serialVersionUID = 1L;

	public static enum State {
		INPUTTING, INPUTTED, CANCELED
	};

	private State state = State.CANCELED;
	private String inputtedName;

	private String defaultName = "Default";

	protected JLabel messageLabel = new JLabel("　");
	protected JTextField nameTextField = new JTextField("　");
	protected JButton okButton = new JButton("OK");
	protected JButton cancelButton = new JButton("cancel");

	public RECreateNameDialog(REApplication application) {
		super(application.getFrame(), application);
		initializeViews();
	}

	private void initializeViews() {

		messageLabel.setPreferredSize(new Dimension(300, 50));

		nameTextField.setBorder(BorderFactory
				.createTitledBorder(getInputTitle() + "："));
		nameTextField.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				validCheck();
			}
		});

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okFinish();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancelFinish();
			}
		});

		// Layouting
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(messageLabel);
		northPanel.add(nameTextField, BorderLayout.SOUTH);
		contentPane.add(northPanel, BorderLayout.NORTH);

		JPanel southPanel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.RIGHT);
		southPanel.setLayout(layout);
		southPanel.add(okButton);
		southPanel.add(cancelButton);
		contentPane.add(southPanel, BorderLayout.SOUTH);

		pack();
	}

	public void open() {
		setWindowAtCenter();
		startDialog();
	}

	private void startDialog() {
		nameTextField.setText(getDefaultName());
		state = State.INPUTTING;
		validCheck();
		setVisible(true);
	}

	private void okFinish() {
		inputtedName = nameTextField.getText();
		state = State.INPUTTED;
		dispose();
	}

	private void cancelFinish() {
		state = State.CANCELED;
		dispose();
	}

	public State getState() {
		return state;
	}

	public String getInputtedName() {
		return inputtedName;
	}

	public String getDefaultName() {
		return this.defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	protected abstract String getInputTitle();

	protected abstract void validCheck();

	protected boolean isValidFirstCharacterUsed(String text) {
		if (text == null || text.length() == 0) {
			return false;
		}
		return Character.isJavaIdentifierStart(text.charAt(0));
	}

	protected boolean isValidCharacterUsed(String text) {
		if (text == null || text.length() == 0) {
			return false;
		}
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isJavaIdentifierPart(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
