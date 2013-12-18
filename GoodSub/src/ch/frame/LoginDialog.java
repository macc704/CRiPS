package ch.frame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginDialog {
	public static void main(String[] args) throws IOException {
		new LoginDialog().openLoginDialog();
	}

	private String name;

	public void openLoginDialog() {

		final JDialog dialog = new JDialog();
		final JTextField nameField = new JTextField(10);
		JPanel btnPanel = new JPanel();
		JPanel textPanel = new JPanel();
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("キャンセル");

		dialog.setTitle("ログイン画面");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setBounds(100, 100, 200, 150);

		textPanel.add(nameField);
		btnPanel.add(okButton);
		btnPanel.add(cancelButton);

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				name = nameField.getText();
				dialog.dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});

		dialog.add(textPanel, BorderLayout.CENTER);
		dialog.add(btnPanel, BorderLayout.PAGE_END);

		dialog.setModal(true);
		dialog.setVisible(true);

	}

	public String getName() {
		return name;
	}

}
