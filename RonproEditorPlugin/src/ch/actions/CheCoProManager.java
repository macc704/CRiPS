package ch.actions;

import java.awt.Color;
import java.net.Socket;

import org.eclipse.ui.IWorkbenchWindow;

import ch.conn.framework.CHConnection;
import ch.conn.framework.packets.CHLoginRequest;

public class CheCoProManager {

	public static final String APP_NAME = "CheCoPro";
	public static final String DEFAULT_NAME = "guest";
	public static final String DEFAULT_PASSWAOD = "pass";
	public static final Color DEFAULT_COLOR = Color.WHITE;
	public static final int DEFAULT_PORT = 10000;
	public static final String IP = "localhost";
	
	private CHConnection conn;
	
	public CheCoProManager(IWorkbenchWindow window) {
		
		// メニュー選択テスト用
		System.out.println("CheCoPro start");
		startCheCoPro();
	}
	
	/********************
	 * クライアントメイン動作
	 ********************/

	public void startCheCoPro() {

		new Thread() {
			public void run() {
				connectServer();
			}
		}.start();
	}

	private void connectServer() {

		try (Socket sock = new Socket(IP, DEFAULT_PORT)) {
			conn = new CHConnection(sock);
			newConnectionOpened(conn);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void newConnectionOpened(CHConnection conn) {

		conn.shakehandForClient();

		if (login()) {
			System.out.println("client established");
		}

		try {
			while (conn.established()) {
				readFromServer();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		conn.close();
		System.out.println("client closed");

	}
	
	private boolean login() {
		conn.write(new CHLoginRequest(DEFAULT_NAME, DEFAULT_PASSWAOD, DEFAULT_COLOR));
		return conn.established();
	}
	
	private void readFromServer(){
		Object obj = conn.read();

		System.out.println("read: " + obj.getClass());
	}
}
