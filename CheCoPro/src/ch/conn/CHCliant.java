package ch.conn;

import java.awt.Color;
import java.net.Socket;

import ch.conn.framework.CHConnection;
import ch.conn.framework.CHProcessManager;
import ch.conn.framework.packets.CHLoginRequest;
import ch.library.CHFileSystem;
import ch.util.CHComponent;
import ch.util.CHEvent;
import ch.util.CHListener;

public class CHCliant {
	
	public static final String DEFAULT_NAME = "";
	public static final String DEFAULT_PASSWAOD = "";
	public static final Color DEFAULT_COLOR = Color.WHITE;
	public static final int DEFAULT_PORT = 20000;
	public static final String IP = "localhost";
	public static final int DEFAULT_LANGUAGE = 0;
	
	private int port = DEFAULT_PORT;
	private String user = DEFAULT_NAME;
	private String password = DEFAULT_PASSWAOD;
	private Color color = DEFAULT_COLOR;
	
	private CHConnection conn;
	private CHComponent component;
	
	public CHCliant(int port, String user, String password, Color color) {
		this.port = port;
		this.user = user;
		this.password = password;
		this.color = color;
	}
	
	public void start() {
		
		// TODO 一時的にログ機能切断
		addCHListeners();
		
		new Thread() {
			public void run() {
				connectServer();
			}
		}.start();
	}

	private void connectServer() {
		
		try (Socket sock = new Socket(IP, port)) {
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
			CHFileSystem.getFinalProjectDir();
			// TODO アプリケーションのリフレッシュ
		}

		CHProcessManager processManager = new CHProcessManager(user, password, color, conn);
		processManager.setComponent(component);
		
		try {
			while (conn.established()) {		
				processManager.doProcess(readFromServer());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		conn.close();
		connectionKilled();
		System.out.println("client closed");

	}

	private boolean login() {
		conn.write(new CHLoginRequest(user, password, color));
		return conn.established();
	}
	
	private Object readFromServer() {

		return conn.read();
	}
	
	private void connectionKilled() {

	}
	
	public void addCHListeners() {
		component.addCHListener(new CHListener() {
			
			@Override
			public void processChanged(CHEvent e) {
			}
			
			@Override
			public void memberSelectorChanged(CHEvent e) {
				if (e.getMessage().equals("WindowClosing")){
					conn.close();
				}
			}
		});
	}
	
	public CHComponent getComponent() {
		return component;
	}

	public void setComponent(CHComponent component) {
		this.component = component;
	}

	public static void main(String[] args) {
		new CHCliant(DEFAULT_PORT, "Taro", "joho315", Color.BLUE).start();
		new CHCliant(DEFAULT_PORT, "Hanako", "joho315", Color.RED).start();
	}
	
}
