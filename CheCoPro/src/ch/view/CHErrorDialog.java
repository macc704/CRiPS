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
	
	private int errorCode;
	
	public CHErrorDialog(int errorCode) {
		this.errorCode = errorCode;
		init();
	}
	
	public void init() {
		setTitle("Error");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setBounds(100, 100, 300, 150);
		setResizable(false);
		CWindowCentraizer.centerWindow(this);
		
		JPanel panel = new JPanel();
		panel.add(createMassagePane(), BorderLayout.CENTER);
		panel.add(createOKButton(), BorderLayout.SOUTH);
		
		getContentPane().add(panel);
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
	
	public JScrollPane createMassagePane() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(createMessageArea());
		scrollPane.setPreferredSize(new Dimension(250, 80));
		return scrollPane;
	}
	
	public JTextArea createMessageArea() {
		JTextArea textArea = new JTextArea();
		textArea.setText(getErrorMessage());
		textArea.setEditable(false);
		return textArea;
	}
	
	public String getErrorMessage() {
		switch (errorCode) {
		case ILLEGAL_ID:
			return "IDの形式が不正です．\nIDは学生番号を入力してください．\n例：70511000";
		case ILLEGAL_PASS:
			return "パスワードの文字数が不正です．\nパスワードは4〜12文字で入力してください．";
		case CONNECTION_FAILED:
			return "接続に失敗しました．";
		case CONNECTION_KILLED:
			return "接続が切れました．";
		}
		return "";
	}
	
	public static void main(String[] args) {
		new CHErrorDialog(ILLEGAL_PASS).doOpen();
	}
}
