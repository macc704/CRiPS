/*
 * PPInputLabelDialog.java
 * Created on 2012/05/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import clib.view.dialogs.CDialog;

/**
 * @author macchan
 * 
 */
public class PPInputLabelDialog extends CDialog {

	private static final long serialVersionUID = 1L;

	public static enum State {
		INPUTTING, INPUTTED, CANCELED
	};

	private State state = State.CANCELED;
	private String input = "";

	protected JTextField nameTextField = new JTextField("");
	protected JButton okButton = new JButton("OK");
	protected JButton cancelButton = new JButton("Cancel");

	public PPInputLabelDialog(Frame owner) {
		super(owner);
		initializeViews();
	}

	private void initializeViews() {

		setPreferredSize(new Dimension(300, 150));

		nameTextField.setBorder(BorderFactory.createTitledBorder("作業内容"));

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
		state = State.INPUTTING;
		setVisible(true);
	}

	private void okFinish() {
		input = nameTextField.getText();
		state = State.INPUTTED;
		dispose();
	}

	private void cancelFinish() {
		state = State.CANCELED;
		dispose();
	}

	public boolean isInput() {
		return state == State.INPUTTED;
	}

	public State getState() {
		return state;
	}

	public String getInput() {
		return input;
	}

}