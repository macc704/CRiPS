package ch.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import clib.view.windowmanager.CWindowCentraizer;

public class CHWarningDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private boolean ok;

	public CHWarningDialog(int language) {
		initialize(language);
	}

	private void initialize(int language) {
		this.setTitle("Warning!");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setBounds(100, 100, 375, 150);
		this.setResizable(false);
		CWindowCentraizer.centerWindow(this);

		JLabel label = new JLabel();
		JButton cancelButton = new JButton("キャンセル");
		JButton okButton = new JButton("OK");
		
		if (language == 0) {
			label.setText("あなたのプロジェクトに上書きしてもよろしいですか？");
			cancelButton.setText("キャンセル");
		} else {
			label.setText("If you wish to overwrite your existing project, press OK.");
			cancelButton.setText("CANCEL");
		}
		
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(cancelButton);
		panel.add(okButton);

		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		okButton.addActionListener(this);
		okButton.setActionCommand("ok");

		this.getContentPane().add(label, BorderLayout.CENTER);
		this.getContentPane().add(panel, BorderLayout.SOUTH);

		open();
	}

	public void open() {
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new CHWarningDialog(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("cancel")) {
			setOk(false);
		} else if (actionCommand.equals("ok")) {
			setOk(true);
		}
		this.dispose();
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}
}
