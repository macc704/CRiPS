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

import clib.view.windowmanager.CWindowCentraizer;

public class CHPullDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private String user;
	private JCheckBox javaCheckBox = new JCheckBox("Javaのプログラム", true);
	private JCheckBox materialCheckBox = new JCheckBox("素材(画像・音楽ファイル)", true);
	private boolean javaChecked;
	private boolean materialChecked;
	private int language = 0;

	public CHPullDialog(String user, int language) {
		this.user = user;
		this.language = language;
		initialize(language);
	}

	private void initialize(int language) {
		
		JLabel label = new JLabel();
		JButton cancelButton = new JButton();
		JButton okButton = new JButton("OK");
		
		if (language == 0) {
			this.setTitle("PULL(取り込み)");
			label.setText(user + "さんのプロジェクトをfinalに取り込みます．");
			cancelButton.setText("キャンセル");
			javaCheckBox.setText("Javaのプログラム");
			materialCheckBox.setText("素材(画像・音楽ファイル)");
		} else {
			this.setTitle("PULL (Import)");
			label.setText("Import " + user + "'s project into final.");
			cancelButton.setText("CANCEL");
			javaCheckBox.setText("Source (Java)");
			materialCheckBox.setText("Materials (Image & Music files)");
		}
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setBounds(100, 100, 300, 150);
		this.setResizable(false);
		CWindowCentraizer.centerWindow(this);

		JPanel checkBoxPanel = new JPanel(new FlowLayout());
		checkBoxPanel.add(javaCheckBox);
		checkBoxPanel.add(materialCheckBox);

		JPanel buttonPanel = new JPanel(new FlowLayout());
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
				CHWarningDialog warningDialog = new CHWarningDialog(language);
				if (warningDialog.isOk()) {
					this.dispose();
				}
			} else {
				this.dispose();
			}
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
		new CHPullDialog("user", 1);
	}

}
