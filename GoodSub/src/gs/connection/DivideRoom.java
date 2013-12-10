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
	public static void main(String[] args) throws IOException {
		new DivideRoom().selectRoomNum();
	}

	private static List<Integer> rooms = new ArrayList<Integer>();
	private JDialog dialog = new JDialog();
	private JTextField name = new JTextField(10);
	// private JTextField tf = new JTextField(10);
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
		// textPanel.add(tf);
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
		String pushed = e.getActionCommand();
		if (pushed.equals("OK")) {
			// String gn = tf.getText();
			// tfnum = Integer.parseInt(gn);
			userName = name.getText();
			dialog.dispose();
		} else if (pushed.equals("cansel")) {
			dialog.dispose();
		}
	}

	public String getUserName() {
		return userName;
	}

}
