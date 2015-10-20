package ch.conn.framework;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.sync.CFileHashList;
import ch.conn.framework.packets.CHEntryRequest;
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
import ch.view.CHEntryDialog;
import ch.view.CHMemberSelectorFrame;

public class CHProcessManager {

	private Object process;
	private CHComponent component;
	
	private String user;
	private String password;
	private Color color;
	private CHConnection conn;
	
	private CHMemberSelectorFrame memberSelector;
	private List<CHUserState> userStates = new ArrayList<CHUserState>();
	
	public CHProcessManager(String user, String password, Color color, CHConnection conn) {
		this.user = user;
		this.password = password;
		this.conn = conn;
	}
	
	public void setProcess(Object process) {
		this.process = process;
	}
	
	public void doProcess() {
		doProcess(process);
	}
	
	public void doProcess(Object process) {

		if (process instanceof CHLoginResult) {
			processLoginResult((CHLoginResult) process);
			component.fireLoginResult();
		} else if (process instanceof CHEntryResult) {
			processEntryResult((CHEntryResult) process);
			component.fireEntryResult();
		} else if (process instanceof CHLoginMemberChanged) {
			processLoginMemberChanged((CHLoginMemberChanged) process);
			component.fireLoginMemberChanged();
		} else if (process instanceof CHSourceChanged) {
			processSourceChanged((CHSourceChanged) process);
			component.fireSourceChanged();
		} else if (process instanceof CHLogoutResult) {
			processLogoutResult((CHLogoutResult) process);
			component.fireLoguoutResult();
		} else if (process instanceof CHFileRequest) {
			processFileRequest((CHFileRequest) process);
			component.fireFileRequest();
		} else if (process instanceof CHFileResponse) {
			processFileResponse((CHFileResponse) process);
			component.fireFileResponse();
		} else if (process instanceof CHFilelistRequest) {
			processFilelistRequest((CHFilelistRequest) process);
			component.fireFileListRequest();
		} else if (process instanceof CHFilelistResponse) {
			processFilelistResponse((CHFilelistResponse) process);
			component.fireFileListResponse();
		} else if (process instanceof CHFilesizeNotice) {
			processFilesizeNotice((CHFilesizeNotice) process);
			component.fireFileSizeNotice();
		}
	}
	
	/**********************
	 * 受信したコマンド別の処理
	 **********************/

	private void processLoginResult(CHLoginResult result) {
		if (result.isResult() == CHLoginCheck.FAILURE) {
			conn.close();
		} else if (result.isResult() == CHLoginCheck.NEW_ENTRY) {
			CHEntryDialog entryDialog = new CHEntryDialog(user, password);
			entryDialog.open();
			user = entryDialog.getUser();
			password = entryDialog.getPassword();
			if (!user.equals("")) {
				conn.write(new CHEntryRequest(user, password));
			} else {
				conn.close();
			}
		} else if (result.isResult() == CHLoginCheck.SUCCESS) {
			// TODO ログ
			memberSelector = new CHMemberSelectorFrame(user);
			memberSelector.setComponent(component);
			memberSelector.open();
		}
	}

	private void processEntryResult(CHEntryResult result) {
		// TODO ダイアログ
		if (result.isResult()) {
			// 登録成功
			conn.write(new CHLoginRequest(user, password, color));
		} else {
			// 登録失敗
			System.out.println("Entry failed");
			conn.close();
		}
	}

	private void processLoginMemberChanged(CHLoginMemberChanged result) {
		userStates = result.getUserStates();
		component.setUserStates(userStates);
		memberSelector.setMembers(result.getUserStates());
		memberSelector.initListener();
	}

	private void processSourceChanged(CHSourceChanged response) {
		component.setScPakcet(response);
	}

	private void processLogoutResult(CHLogoutResult result) {
		if (user.equals(result.getUser())) {
			// TODO ログ
			conn.close();
			// TODO リスナのリムーブ
		}
	}

	private void processFileRequest(CHFileRequest request) {
		List<CHFile> files = CHFileSystem.getCHFiles(
				request.getRequestFilePaths(),
				CHFileSystem.getFinalProjectDir());
		conn.write(new CHFileResponse(user, files));
	}

	private void processFileResponse(CHFileResponse response) {
		CHFileSystem.saveFiles(response.getFiles(),
				CHFileSystem.getUserDirForClient(response.getUser()));
		// TODO 対象のCHEditorが開いていたらリフレッシュ
	}

	private void processFilelistResponse(CHFilelistResponse response) {
		String user = response.getUser();
		CDirectory copyDir = CHFileSystem.getUserDirForClient(user);
		List<String> requestFilePaths = CHFileSystem.getRequestFilePaths(
				response.getFileList(), copyDir);
		conn.write(new CHFileRequest(user, requestFilePaths));
	}

	private void processFilelistRequest(CHFilelistRequest request) {
		CFileHashList fileList = CHFileSystem.getFinalProjectFileList();
		conn.write(new CHFilelistResponse(user, fileList));
	}

	private void processFilesizeNotice(CHFilesizeNotice notice) {
		// TODO ひとまずいらない？
	}
	
	public void sendText(String source, String currentFileName, Point point) {
		if(conn != null && conn.established()) {
			conn.write(new CHSourceChanged(user, source, currentFileName, point));
		}
	}
	
	public void setComponent(CHComponent component) {
		this.component = component;
	}
}
