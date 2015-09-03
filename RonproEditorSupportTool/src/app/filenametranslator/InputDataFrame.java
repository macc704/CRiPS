package app.filenametranslator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InputDataFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField textField = new JTextField();
	private JButton okButton = new JButton("OK");

	private String lectureNumber = "";

	public InputDataFrame() {
		initializeViews();
	}

	private void initializeViews() {
		setSize(200, 140);

		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doClose();
			}
		});

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(new JLabel("課題番号を入力してください"), BorderLayout.NORTH);
		main.add(textField, BorderLayout.CENTER);

		pane.add(main, BorderLayout.CENTER);
		pane.add(okButton, BorderLayout.SOUTH);

		getContentPane().add(pane);
	}

	public void open() {
		setVisible(true);
	}

	private void doClose() {
		if (textField.getText().length() == 0) {
			JOptionPane.showMessageDialog(this, "課題番号がありません");
			return;
		} else {
			lectureNumber = textField.getText();
			setVisible(false);
		}
	}

	public String getLectureNumber() {
		return lectureNumber;
	}

}
