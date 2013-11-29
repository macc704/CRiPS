package gs.connection;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DivideRoom implements ActionListener {
	public static void main(String[] args) {
		// new DivideRoom().makeFrame();
	}

	private static List<Integer> rooms = new ArrayList<Integer>();
	private JDialog dialog = new JDialog();
	private JTextField name = new JTextField(10);
	private JTextField tf = new JTextField(10);
	private int tfnum;
	private String userName;

	public int selectRoomNum() throws IOException {
		JPanel btnPanel = new JPanel();
		JPanel textPanel = new JPanel();
		JButton okbtn = new JButton("OK");
		JButton canbtn = new JButton("キャンセル");

		dialog.setTitle("ログイン画面");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setBounds(100, 100, 200, 150);

		textPanel.add(name);
		textPanel.add(tf);
		btnPanel.add(okbtn);
		btnPanel.add(canbtn);

		okbtn.addActionListener(this);
		okbtn.setActionCommand("OK");
		canbtn.addActionListener(this);
		canbtn.setActionCommand("cancel");

		dialog.add(textPanel, BorderLayout.CENTER);
		dialog.add(btnPanel, BorderLayout.PAGE_END);

		dialog.setModal(true);
		dialog.setVisible(true);

		// System.out.print("部屋番号 : ");
		// BufferedReader input = new BufferedReader(new InputStreamReader(
		// System.in));
		// String str = input.readLine();
		// int roomNum = Integer.parseInt(str);
		return tfnum;
	}

	public boolean checkRoomNum(int roomNum) {
		for (int number : rooms) {
			if (roomNum == number) {
				// 部屋が存在したらtrue
				return true;
			}
		}
		// 部屋番号がなかったらリストに加える
		rooms.add(roomNum);
		// 存在しなかったらfalse
		return false;
	}

	// 部屋がリストの何番目にあるかを調べる
	public int countRoom(int roomNum) {
		int i = 0;
		for (int number : rooms) {
			if (roomNum == number) {
				return i;
			}
			i++;
		}
		return -1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String pushed = e.getActionCommand();
		if (pushed.equals("OK")) {
			String gn = tf.getText();
			tfnum = Integer.parseInt(gn);
			userName = name.getText();
			dialog.dispose();
		} else if (pushed.equals("cansel")) {
			dialog.dispose();
		}
	}

	public String getUserName() {
		return userName;
	}
	/*
	 * public void makeFrame() { frame.setTitle("CheCoPro -集会所-");
	 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 * frame.setBounds(100, 100, 300, 500); frame.getContentPane().setLayout(
	 * new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS)); JButton plus
	 * = new JButton("+"); plus.setMaximumSize(new Dimension(300, 32));
	 * plus.addActionListener(this); plus.setActionCommand("plus");
	 * frame.add(plus); frame.setVisible(true);
	 * 
	 * }
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { String pushed =
	 * e.getActionCommand(); if (pushed.equals("plus")) { makeDialog(); } else
	 * if (pushed.equals("cancel")) { dialog.dispose(); } else if
	 * (pushed.equals("OK")) { String gn = tf.getText(); tfnum =
	 * Integer.parseInt(gn); dialog.dispose(); //
	 * System.out.println("group name is " + gn); // dialog.dispose(); //
	 * makeRoomButton(gn); } else { System.out.println("log in " + pushed);
	 * frame.dispose(); } }
	 * 
	 * public void makeDialog() { JLabel label = new JLabel("グループ名"); JButton
	 * okbtn = new JButton("OK"); JButton canbtn = new JButton("キャンセル"); JPanel
	 * gnPanel = new JPanel(); JPanel btnPanel = new JPanel();
	 * 
	 * dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	 * dialog.setTitle("新規集会所の作成"); dialog.setBounds(150, 150, 200, 150);
	 * gnPanel.add(label); gnPanel.add(tf); btnPanel.add(okbtn);
	 * btnPanel.add(canbtn); dialog.add(gnPanel, BorderLayout.CENTER);
	 * dialog.add(btnPanel, BorderLayout.PAGE_END);
	 * 
	 * okbtn.addActionListener(this); okbtn.setActionCommand("OK");
	 * canbtn.addActionListener(this); canbtn.setActionCommand("cancel");
	 * 
	 * dialog.setModal(true); dialog.setVisible(true); }
	 */

	/*
	 * public void makeRoomButton(String gn) { JButton btn = new JButton(gn);
	 * 
	 * btn.setMaximumSize(new Dimension(300, 32)); btn.addActionListener(this);
	 * btn.setActionCommand(gn); frame.add(btn); frame.setVisible(true); }
	 */

}
