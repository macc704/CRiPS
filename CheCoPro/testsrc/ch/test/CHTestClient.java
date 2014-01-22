package ch.test;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JScrollBar;

import ch.conn.framework.CHConnection;
import ch.view.CHFrame;

public class CHTestClient {

	public static void main(String[] args) {
		new CHTestClient().run();
	}

	private CHConnection conn;
	private CHFrame frame = new CHFrame();
	private int roomNum;
	private String text;

	public void run() {

		// �e�L�X�g�G���A�̏���
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setBounds(100, 100, 300, 300);
		// frame.setTitle("Client4");

		// �L�[�������[�X���ꂽ���̓���i�e�L�X�g�G���A�j
		frame.getTextArea().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String text = frame.getTextArea().getText();
				conn.write(text);
			}
		});

		// �h���b�O���ꂽ���̓���i�X�N���[���o�[�j
		frame.getvBar().addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				JScrollBar vBar = frame.getvBar();
				int value = vBar.getValue();
				conn.write(value);
			}
		});

		frame.open();

		// �ڑ�
		Socket sock = null;
		try {
			sock = new Socket("localhost", 10000);
			conn = new CHConnection(sock);
			newConnectionOpened(conn);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				sock.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void newConnectionOpened(CHConnection conn) {

		Object obj;

		conn.shakehandForClient();

		// �����ԍ��ݒ�
		// LoginDialog room = new LoginDialog();
		// try {
		// roomNum = room.selectRoomNum();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		frame.setTitle("Client4[" + roomNum + "]");

		// �X�N���[���o�[��value�Ǝ��ʂ��邽�߂Ƀ}�C�i�X��
		conn.write(roomNum * (-1));

		System.out.println("client established");
		try {
			while (true) {
				obj = conn.read();
				if (obj instanceof String) { // String�Ȃ�e�L�X�g�G���A�ɕ\��
					String text = (String) obj;
					frame.getTextArea().setText(text);
				} else if (obj instanceof Integer) { // Integer�Ȃ�X�N���[���o�[����
				// int value = (int) obj;
				// frame.setvBar(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		conn.close();
		System.out.println("client closed");
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void keyReleased() {
		conn.write(text);
	}

	public void setFrame(CHFrame frame) {
		this.frame = frame;
	}

}