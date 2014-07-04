package ch.actions;

import java.awt.Color;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.ICommandService;

import ronproeditorplugin.Activator;
import ch.conn.framework.CHConnection;
import ch.conn.framework.CHFile;
import ch.conn.framework.CHLoginCheck;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHFileRequest;
import ch.conn.framework.packets.CHFileResponse;
import ch.conn.framework.packets.CHFilelistRequest;
import ch.conn.framework.packets.CHFilelistResponse;
import ch.conn.framework.packets.CHLoginMemberChanged;
import ch.conn.framework.packets.CHLoginRequest;
import ch.conn.framework.packets.CHLoginResult;
import ch.library.CHFileSystem;
import ch.perspective.views.CHMemberDirectoryView;
import ch.perspective.views.CHMemberStateView;
import ch.view.CHMemberSelectorFrame;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.sync.CFileHashList;
import clib.common.table.CCSVFileIO;

public class CheCoProManager {

	public static final String APP_NAME = "CheCoPro";
	public static final String DEFAULT_NAME = "guest";
	public static final String DEFAULT_PASSWAOD = "pass";
	public static final Color DEFAULT_COLOR = Color.WHITE;
	public static final int DEFAULT_PORT = 10000;
	public static final String IP = "localhost";

	private CHConnection conn;
	private String user;
	private String password;
	private int port;
	private CHMemberSelectorFrame msFrame;
	private List<CHUserState> userStates = new ArrayList<CHUserState>();

	private IWorkbenchWindow window;

	public CheCoProManager(IWorkbenchWindow window) {

		this.window = window;

		setWorkbenchWindowToViews();
		addCHListeners();
		String[][] table = new String[1][3];
		table = CCSVFileIO.load(CHFileSystem.getPrefFile());
		if (table.length == 0) {
			System.out.println("Preference未入力");
		} else {
			user = table[0][0];
			password = table[0][1];
			port = DEFAULT_PORT + Integer.parseInt(table[0][2]);
			startCheCoPro();
		}
	}

	private void setWorkbenchWindowToViews() {

		IWorkbenchPage page = window.getActivePage();
		CHMemberDirectoryView memberDirectoryView;
		try {
			memberDirectoryView = (CHMemberDirectoryView) page
					.showView("ch.memberDirectoryView");
			memberDirectoryView.setWindow(window);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	private void addCHListeners() {
		ICommandService service = (ICommandService) Activator.getDefault()
				.getWorkbench().getService(ICommandService.class);
		service.addExecutionListener(saveListener);
	}

	private IExecutionListener saveListener = new IExecutionListener() {

		@Override
		public void preExecute(String commandId, ExecutionEvent event) {

		}

		@Override
		public void postExecuteSuccess(String commandId, Object returnValue) {
			if (commandId.endsWith("org.eclipse.ui.file.save")) {
				conn.write(new CHFilelistResponse(user, CHFileSystem
						.getEclipseProjectFileList()));
			}
		}

		@Override
		public void postExecuteFailure(String commandId,
				ExecutionException exception) {

		}

		@Override
		public void notHandled(String commandId, NotHandledException exception) {

		}
	};

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
		System.out.println("user:" + user + " password:" + password);
		conn.write(new CHLoginRequest(user, password, DEFAULT_COLOR));
		return conn.established();
	}

	private void readFromServer() {
		Object obj = conn.read();

		if (obj instanceof CHLoginResult) {
			processLoginResult((CHLoginResult) obj);
		} else if (obj instanceof CHLoginMemberChanged) {
			processLoginMemberChanged((CHLoginMemberChanged) obj);
		} else if (obj instanceof CHFilelistRequest) {
			processFilelistRequest();
		} else if (obj instanceof CHFilelistResponse) {
			processFileListResponse((CHFilelistResponse) obj);
		} else if (obj instanceof CHFileRequest) {
			processFileRequest((CHFileRequest) obj);
		} else if (obj instanceof CHFileResponse) {
			processFileResponse((CHFileResponse) obj);
		}
	}

	private void processLoginResult(CHLoginResult result) {
		if (result.isResult() == CHLoginCheck.FAILURE) {
			System.out.println("login failure");
			conn.close();
		} else if (result.isResult() == CHLoginCheck.SUCCESS) {
			System.out.println("login success");
			msFrame = new CHMemberSelectorFrame(user, conn);
			msFrame.open();
		}
	}

	private void processLoginMemberChanged(CHLoginMemberChanged result) {
		new Thread(new MemberStateUpdater(result)).start();
		msFrame.setMembers(result.getUserStates());
	}

	private void processFilelistRequest() {
		CFileHashList fileList = CHFileSystem.getEclipseProjectFileList();
		conn.write(new CHFilelistResponse(user, fileList));
	}

	private void processFileListResponse(CHFilelistResponse response) {

		String user = response.getUser();
		CDirectory copyDir = CHFileSystem.getEclipseMemberDir(user);

		List<String> requestFilePaths = CHFileSystem.getRequestFilePaths(
				response.getFileList(), copyDir);

		conn.write(new CHFileRequest(user, requestFilePaths));
	}

	private void processFileRequest(CHFileRequest request) {
		List<CHFile> files = CHFileSystem.getCHFiles(
				request.getRequestFilePaths(),
				CHFileSystem.getEclipseProjectDir());
		conn.write(new CHFileResponse(user, files));
	}

	private void processFileResponse(CHFileResponse response) {
		CHFileSystem.saveFiles(response.getFiles(),
				CHFileSystem.getEclipseMemberDir(response.getUser()));
	}

	class MemberStateUpdater implements Runnable {

		private CHLoginMemberChanged result;

		public MemberStateUpdater(CHLoginMemberChanged result) {
			this.result = result;
		}

		@Override
		public void run() {
			window.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					userStates = result.getUserStates();
					IWorkbenchPage page = window.getActivePage();
					CHMemberStateView memberStateView;
					try {
						memberStateView = (CHMemberStateView) page
								.showView("ch.memberStateView");
						memberStateView.setUserStates(userStates);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
