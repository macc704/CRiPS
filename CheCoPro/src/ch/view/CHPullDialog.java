package ch.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CHPullDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private String user;

	public CHPullDialog(String user) {
		this.user = user;
		initialize();
	}

	private void initialize() {
		this.setTitle("PULL(取り込み)");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setBounds(100, 100, 300, 200);

		JLabel label = new JLabel(user + "さんのプロジェクトをfinalに取り込みます．");

		JPanel checkBoxPanel = new JPanel(new FlowLayout());
		JCheckBox javaCheckBox = new JCheckBox("Javaのプログラム", true);
		JCheckBox materialCheckBox = new JCheckBox("素材(画像・音楽ファイル)", true);
		checkBoxPanel.add(javaCheckBox);
		checkBoxPanel.add(materialCheckBox);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton cancelButton = new JButton("キャンセル");
		JButton okButton = new JButton("OK");
		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);

		this.getContentPane().add(label, BorderLayout.NORTH);
		this.getContentPane().add(checkBoxPanel, BorderLayout.CENTER);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		open();
	}

	public void open() {
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new CHPullDialog("user");
	}
}
