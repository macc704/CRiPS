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
	private JCheckBox currentCheckBox = new JCheckBox("現在開いているjavaファイルのみ",
			false);
	private boolean javaChecked = false;
	private boolean materialChecked = false;
	private boolean currentChecked = false;
	private boolean fileOpened = false;

	public CHPullDialog(String user, boolean fileOpened) {
		this.user = user;
		this.fileOpened = fileOpened;
		initialize();
	}

	private void initialize() {
		currentCheckBox.setEnabled(fileOpened);
		this.setTitle("PULL(取り込み)");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setBounds(100, 100, 350, 200);
		this.setResizable(false);

		JLabel label = new JLabel(user + "さんのプロジェクトをfinalに取り込みます．");

		javaCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (javaCheckBox.isSelected()) {
					currentCheckBox.setSelected(false);
				}
			}
		});

		materialCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (materialCheckBox.isSelected()) {
					currentCheckBox.setSelected(false);
				}
			}
		});

		currentCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				javaCheckBox.setSelected(!currentCheckBox.isSelected());
				materialCheckBox.setSelected(!currentCheckBox.isSelected());
			}
		});

		JPanel checkBoxPanel = new JPanel(new FlowLayout());
		checkBoxPanel.add(javaCheckBox);
		checkBoxPanel.add(materialCheckBox);
		checkBoxPanel.add(currentCheckBox);

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

		// open();
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
			setCurrentChecked(false);
			this.dispose();
		} else if (actionCommand.equals("ok")) {
			setJavaChecked(javaCheckBox.isSelected());
			setMaterialChecked(materialCheckBox.isSelected());
			setCurrentChecked(currentCheckBox.isSelected());
			if (isJavaChecked() || isCurrentChecked()) {
				CHWarningDialog warningDialog = new CHWarningDialog();
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

	public boolean isMaterialChecked() {
		return materialChecked;
	}

	public boolean isCurrentChecked() {
		return currentChecked;
	}

	public void setJavaChecked(boolean javaChecked) {
		this.javaChecked = javaChecked;
	}

	public void setMaterialChecked(boolean materialChecked) {
		this.materialChecked = materialChecked;
	}

	public void setCurrentChecked(boolean currentChecked) {
		this.currentChecked = currentChecked;
	}

	public static void main(String[] args) {
		new CHPullDialog("70002222", true).open();
	}

}
