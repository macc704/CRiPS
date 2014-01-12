package ch.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class CHEntryDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private String user;
	private String password;
	// private String confirm;

	private JTextField userField = new JTextField(15);
	private JPasswordField passwordField = new JPasswordField(15);
	private JPasswordField confirmField = new JPasswordField(15);

	public static void main(String[] args) {
		new CHEntryDialog().open();
	}

	public CHEntryDialog() {
		initialize();
	}

	private void initialize() {
		this.setTitle("CheCoPro entry");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setBounds(100, 100, 400, 200);

		JPanel gridPanel = new JPanel(new GridLayout(3, 0));

		FlowLayout layout = new FlowLayout(FlowLayout.CENTER);

		JPanel userPanel = new JPanel(layout);
		JPanel passwordPanel = new JPanel(layout);
		JPanel confirmPanel = new JPanel(layout);

		userPanel.add(new JLabel("Name : "));
		userPanel.add(userField);
		passwordPanel.add(new JLabel("Password : "));
		passwordPanel.add(passwordField);
		confirmPanel.add(new JLabel("Confirm Password ; "));
		confirmPanel.add(confirmField);

		gridPanel.add(userPanel);
		gridPanel.add(passwordPanel);
		gridPanel.add(confirmPanel);

		JPanel buttonPanel = new JPanel(layout);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(this);
		okButton.setActionCommand("OK");
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("Cancel");
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		this.add(gridPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.PAGE_END);
	}

	public void open() {
		this.setVisible(true);
	}

	public void close() {
		this.dispose();
	}

	private boolean inputData() {
		String user = userField.getText();
		String password = String.valueOf(passwordField.getPassword());
		String confirm = String.valueOf(confirmField.getPassword());
		if (!password.equals(confirm)) {
			showWarningLabel();
			return false;
		} else if (user.equals("")) {
			showWarningLabel();
			return false;
		} else if (password.equals("") && confirm.equals("")) {
			showWarningLabel();
			return false;
		}
		this.user = user;
		this.password = password;
		return true;
	}

	// 警告文を出す必要あり
	private void showWarningLabel() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("OK")) {
			if (inputData()) {
				user = userField.getText();
				password = String.valueOf(passwordField.getPassword());
				close();
			}
		} else if (actionCommand.equals("Cancel")) {
			close();
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
