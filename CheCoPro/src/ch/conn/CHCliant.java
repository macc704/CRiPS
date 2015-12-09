package ch.conn;

import java.awt.Color;
import java.net.Socket;

import ch.conn.framework.CHConnection;
import ch.conn.framework.CHProcessManager;
import ch.conn.framework.packets.CHLoginRequest;
import ch.library.CHFileSystem;
import ch.library.CHLib;
import ch.util.CHComponent;
import ch.view.CHErrorDialog;

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
	
	private CHProcessManager processManager;
	private CHConnection conn;
	private CHComponent component;
	
	public CHCliant(int port, String user, String password, Color color) {
		this.port = port;
		this.user = user;
		this.password = password;
		this.color = color;
	}
	
	public void start() {

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
			connectionFailed();
		}
	}

	private void newConnectionOpened(CHConnection conn) {

		conn.shakehandForClient();

		if (login()) {
			System.out.println("client established");
			CHFileSystem.getSyncProjectDir();
		} else {
			connectionFailed();
		}

		processManager = new CHProcessManager(user, password, color, conn);
		processManager.setComponent(component);
		
		try {
			while (conn.established()) {		
				processManager.doProcess(readFromServer());
			}
		} catch (Exception ex) {
			connectionKilled();
			ex.printStackTrace();
		}
		conn.close();
		System.out.println("client closed");

	}

	private boolean login() {
		conn.write(new CHLoginRequest(user, CHLib.encrypt(password), color));
		return conn.established();
	}
	
	private Object readFromServer() {
		return conn.read();
	}
	
	public void connectionFailed() {
		new CHErrorDialog(CHErrorDialog.CONNECTION_FAILED).doOpen();
	}
	
	public void connectionKilled() {
		closeMemberSelector();
		component.fireConnectionKilled();
		new CHErrorDialog(CHErrorDialog.CONNECTION_KILLED).doOpen();
	}
	
	public void closeMemberSelector() {
		getProcessManager().getMemberSelector().doClose();
	}
	
	public CHProcessManager getProcessManager() {
		return processManager;
	}
	
	public CHConnection getConn() {
		return conn;
	}
	
	public CHComponent getComponent() {
		return component;
	}

	public void setComponent(CHComponent component) {
		this.component = component;
	}

	public static void main(String[] args) {
		CHCliant cliant1 = new CHCliant(DEFAULT_PORT, "Taro", "joho315", Color.BLUE);
		cliant1.setComponent(new CHComponent());
		cliant1.start();
		CHCliant cliant2 = new CHCliant(DEFAULT_PORT, "Hanako", "joho315", Color.RED);
		cliant2.setComponent(new CHComponent());
		cliant2.start();
	}
	
}
