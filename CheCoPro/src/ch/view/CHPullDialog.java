package ch.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CHPullDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private String user;
	private JCheckBox javaCheckBox = new JCheckBox("Javaのプログラム", true);
	private JCheckBox materialCheckBox = new JCheckBox("素材(画像・音楽ファイル)", true);
	private boolean javaChecked;
	private boolean materialChecked;

	public CHPullDialog(String user) {
		this.user = user;
		initialize();
	}

	private void initialize() {
		this.setTitle("PULL(取り込み)");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setBounds(100, 100, 300, 150);
		this.setResizable(false);

		JLabel label = new JLabel(user + "さんのプロジェクトをfinalに取り込みます．");

		JPanel checkBoxPanel = new JPanel(new FlowLayout());
		checkBoxPanel.add(javaCheckBox);
		checkBoxPanel.add(materialCheckBox);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton cancelButton = new JButton("キャンセル");
		JButton okButton = new JButton("OK");
		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);

		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		okButton.addActionListener(this);
		okButton.setActionCommand("ok");

		this.getContentPane().add(label, BorderLayout.NORTH);
		this.getContentPane().add(checkBoxPanel, BorderLayout.CENTER);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		open();
	}

	public void open() {
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("cancel")) {
			setJavaChecked(false);
			setMaterialChecked(false);
			this.dispose();
		} else if (actionCommand.equals("ok")) {
			setJavaChecked(javaCheckBox.isSelected());
			setMaterialChecked(materialCheckBox.isSelected());
			if (isJavaChecked()) {
				CHWarningDialog warningDialog = new CHWarningDialog();
				if (warningDialog.isOk()) {
					this.dispose();
				}
			}
			this.dispose();
		}
	}

	public boolean isJavaChecked() {
		return javaChecked;
	}

	public boolean isMaterialCecked() {
		return materialChecked;
	}

	public void setJavaChecked(boolean javaChecked) {
		this.javaChecked = javaChecked;
	}

	public void setMaterialChecked(boolean materialChecked) {
		this.materialChecked = materialChecked;
	}

	public static void main(String[] args) {
		new CHPullDialog("user");
	}

}
