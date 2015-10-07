package ch.conn;

import java.awt.Color;
import java.net.Socket;

import ch.conn.framework.CHConnection;
import ch.conn.framework.packets.CHEntryResult;
import ch.conn.framework.packets.CHFileRequest;
import ch.conn.framework.packets.CHFileResponse;
import ch.conn.framework.packets.CHFilelistRequest;
import ch.conn.framework.packets.CHFilelistResponse;
import ch.conn.framework.packets.CHFilesizeNotice;
import ch.conn.framework.packets.CHLoginMemberChanged;
import ch.conn.framework.packets.CHLoginRequest;
import ch.conn.framework.packets.CHLoginResult;
import ch.conn.framework.packets.CHLogoutResult;
import ch.conn.framework.packets.CHSourceChanged;
import ch.library.CHFileSystem;
import ch.util.CHComponent;

public class CHCliant {
	
	public static final String IP = "163.43.140.82";
	
	private int port;
	private String user;
	private String password;
	private Color color;
	
	private CHConnection conn;
	private CHComponent component = new CHComponent();
	
	
	public CHCliant(int port, String user, String password, Color color) {
		this.port = port;
		this.user = user;
		this.password = password;
		this.color = color;
	}
	
	public void start() {
		
		// TODO 一時的にログ機能切断

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

		try {
			while (conn.established()) {
				readFromServer();
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
	
	private void readFromServer() {

		Object obj = conn.read();

		if (obj instanceof CHLoginResult) {
			processLoginResult((CHLoginResult) obj);
			component.fireLoginResult();
		} else if (obj instanceof CHEntryResult) {
			processEntryResult((CHEntryResult) obj);
			component.fireEntryResult();
		} else if (obj instanceof CHLoginMemberChanged) {
			processLoginMemberChanged((CHLoginMemberChanged) obj);
			component.fireLoginMemberChanged();
		} else if (obj instanceof CHSourceChanged) {
			processSourceChanged((CHSourceChanged) obj);
			component.fireSourceChanged();
		} else if (obj instanceof CHLogoutResult) {
			processLogoutResult((CHLogoutResult) obj);
			component.fireLoguoutResult();
		} else if (obj instanceof CHFileRequest) {
			processFileRequest((CHFileRequest) obj);
			component.fireFileRequest();
		} else if (obj instanceof CHFileResponse) {
			processFileResponse((CHFileResponse) obj);
			component.fireFileResponse();
		} else if (obj instanceof CHFilelistRequest) {
			processFilelistRequest((CHFilelistRequest) obj);
			component.fireFileListRequest();
		} else if (obj instanceof CHFilelistResponse) {
			processFilelistResponse((CHFilelistResponse) obj);
			component.fireFileListResponse();
		} else if (obj instanceof CHFilesizeNotice) {
			processFilesizeNotice((CHFilesizeNotice) obj);
			component.fireFileSizeNotice();
		}
	}
	
	/**********************
	 * 受信したコマンド別の処理
	 **********************/

	private void processLoginResult(CHLoginResult result) {

	}

	private void processEntryResult(CHEntryResult result) {

	}

	private void processLoginMemberChanged(CHLoginMemberChanged result) {

	}

	private void processSourceChanged(CHSourceChanged response) {

	}

	private void processLogoutResult(CHLogoutResult result) {

	}

	private void processFileRequest(CHFileRequest request) {

	}

	private void processFileResponse(CHFileResponse response) {

	}

	private void processFilelistResponse(CHFilelistResponse response) {

	}

	private void processFilelistRequest(CHFilelistRequest request) {

	}

	private void processFilesizeNotice(CHFilesizeNotice notice) {

	}
	
	private void connectionKilled() {

	}
	
	
	
}
