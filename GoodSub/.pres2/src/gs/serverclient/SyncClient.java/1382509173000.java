package gs.serverclient;

import gs.connection.Connection;
import gs.connection.DivideRoom;
import gs.frame.GSFrame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JScrollBar;

import ronproeditor.REApplication;

public class SyncClient {

	public static void main(String[] args) {
		new SyncClient().run();
	}

	private Connection conn;
	private GSFrame frame = new GSFrame();
	private int roomNum;
	private String text;

	public void run() {

		// テキストエリアの初期化
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setBounds(100, 100, 300, 300);
		// frame.setTitle("Client4");

		// キーがリリースされた時の動作（テキストエリア）
		frame.getTextArea().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String text = frame.getTextArea().getText();
				conn.write(text);
			}
		});

		// ドラッグされた時の動作（スクロールバー）
		frame.getvBar().addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				JScrollBar vBar = frame.getvBar();
				int value = vBar.getValue();
				conn.write(value);
			}
		});

		frame.open();

		// 接続
		try (Socket sock = new Socket("localhost", 10000)) {
			conn = new Connection(sock);
			newConnectionOpened(conn);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void newConnectionOpened(Connection conn) {

		Object obj;

		conn.shakehandForClient();

		// 部屋番号設定
		DivideRoom room = new DivideRoom();
		try {
			roomNum = room.selectRoomNum();
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setTitle("Client4[" + roomNum + "]");

		// スクロールバーのvalueと識別するためにマイナスに
		conn.write(roomNum * (-1));

		System.out.println("client established");
		try {
			while (true) {
				obj = conn.read();
				if (obj instanceof String) { // Stringならテキストエリアに表示
					String text = (String) obj;
					frame.getTextArea().setText(text);
				} else if (obj instanceof Integer) { // Integerならスクロールバー操作
					int value = (int) obj;
					frame.setvBar(value);
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

	public void setFrame(GSFrame frame) {
		this.frame = frame;
	}

	public void setApplication(REApplication application) {

	}

}