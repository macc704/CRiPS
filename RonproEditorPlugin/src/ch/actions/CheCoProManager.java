package ch.actions;

import java.awt.Color;
import java.net.Socket;
import java.util.List;

import org.eclipse.ui.IWorkbenchWindow;

import clib.common.filesystem.sync.CFileHashList;
import ch.conn.framework.CHConnection;
import ch.conn.framework.CHFile;
import ch.conn.framework.CHLoginCheck;
import ch.conn.framework.packets.CHFileRequest;
import ch.conn.framework.packets.CHFileResponse;
import ch.conn.framework.packets.CHFilelistRequest;
import ch.conn.framework.packets.CHFilelistResponse;
import ch.conn.framework.packets.CHLoginRequest;
import ch.conn.framework.packets.CHLoginResult;
import ch.library.CHFileSystem;

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
		
		if (obj instanceof CHLoginResult) {
			processLoginResult((CHLoginResult) obj);
		} else if (obj instanceof CHFilelistRequest){
			processFilelistRequest();
		} else if (obj instanceof CHFileRequest){
			processFileRequest((CHFileRequest) obj);
		}
	}
	
	private void processLoginResult(CHLoginResult result) {
		if (result.isResult() == CHLoginCheck.FAILURE) {
			System.out.println("login failure");
			conn.close();
		} else if (result.isResult() == CHLoginCheck.SUCCESS) {
			System.out.println("login success");
		}
	}
	
	private void processFilelistRequest() {
		CFileHashList fileList = CHFileSystem.getEclipseProjectFileList();
		conn.write(new CHFilelistResponse(DEFAULT_NAME, fileList));
	}
	
	private void processFileRequest(CHFileRequest request) {
		List<CHFile> files = CHFileSystem.getCHFiles(
				request.getRequestFilePaths(),
				CHFileSystem.getEclipseProjectDir());
		conn.write(new CHFileResponse(DEFAULT_NAME, files));
	}

}
