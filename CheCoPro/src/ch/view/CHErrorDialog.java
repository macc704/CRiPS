package ch.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ch.library.CHFileSystem;
import clib.view.windowmanager.CWindowCentraizer;

public class CHErrorDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int ILLEGAL_ID = 0;
	public static final int ILLEGAL_PASS = 1;
	public static final int CONNECTION_FAILED = 2;
	public static final int CONNECTION_KILLED = 3;
	public static final int PROJECT_MISSING = 4;
	
	private int errorCode;
	private JTextArea textArea = new JTextArea();
	
	public CHErrorDialog(int errorCode) {
		this.errorCode = errorCode;
		init(300, 180);
	}
	
	public CHErrorDialog(int errorCode, Exception ex) {
		this.errorCode = errorCode;
		init(600, 280);
		printException(ex);
	}
	
	public void init(int width, int height) {
		setTitle("Error");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(width, height);
		setModal(true);
		setResizable(false);
		CWindowCentraizer.centerWindow(this);
		
		JPanel panel = new JPanel();
		panel.add(createMassagePane(width-50, height-80), BorderLayout.CENTER);
		panel.add(createOKButton(), BorderLayout.SOUTH);
		
		getContentPane().add(panel);
	}
	
	public void setBounds(int width, int height) {
		setBounds(100, 100, width, height);
	}
	
	public void doOpen() {
		setVisible(true);
	}
	
	public void doClose() {
		dispose();
	}
	
	public JButton createOKButton() {
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doClose();
			}
		});
		return button;
	}
	
	public JScrollPane createMassagePane(int width, int height) {
		JScrollPane scrollPane = new JScrollPane();
		createMessageArea();
		scrollPane.getViewport().setView(textArea);
		scrollPane.setPreferredSize(new Dimension(width, height));
		return scrollPane;
	}
	
	public void createMessageArea() {
		textArea = new JTextArea();
		textArea.setText(getErrorMessage());
		textArea.setEditable(false);
	}
	
	public void addText(String text) {
		String buf = textArea.getText();
		buf = buf + "\n" + text;
		textArea.setText(buf);
	}
	
	public String getErrorMessage() {
		switch (errorCode) {
		case ILLEGAL_ID:
			return "IDの形式が不正です．\nIDは学生番号を入力してください．\n例：70511000";
		case ILLEGAL_PASS:
			return "パスワードの文字数が不正です．\nパスワードは4〜12文字で入力してください．";
		case CONNECTION_FAILED:
			return "接続に失敗しました．\n";
		case CONNECTION_KILLED:
			return "接続が切れました．\n";
		case PROJECT_MISSING:
			return "プロジェクト「" + CHFileSystem.SYNCPROJECTNAME + "」が見つかりません．";
		}
		return "";
	}
	
	public void printException(Exception ex) {
		if (ex.getCause() != null) {
			for (StackTraceElement ste : ex.getCause().getStackTrace()) {
				addText(ste.toString());
			}
		} else {
			for (StackTraceElement ste : ex.getStackTrace()) {
				addText(ste.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		new CHErrorDialog(ILLEGAL_PASS).doOpen();
	}
}
