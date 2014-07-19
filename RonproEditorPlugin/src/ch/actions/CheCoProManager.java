package ch.actions;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.ICommandService;

import presplugin.editors.PresExtendedJavaEditor;
import ronproeditorplugin.Activator;
import ch.conn.framework.CHConnection;
import ch.conn.framework.CHFile;
import ch.conn.framework.CHUserLogWriter;
import ch.conn.framework.packets.CHEntryRequest;
import ch.conn.framework.packets.CHEntryResult;
import ch.conn.framework.packets.CHFileRequest;
import ch.conn.framework.packets.CHFileResponse;
import ch.conn.framework.packets.CHFilelistRequest;
import ch.conn.framework.packets.CHFilelistResponse;
import ch.conn.framework.packets.CHLoginMemberChanged;
import ch.conn.framework.packets.CHLoginRequest;
import ch.conn.framework.packets.CHLoginResult;
import ch.conn.framework.packets.CHLogoutResult;
import ch.conn.framework.packets.CHSourceChanged;
import ch.library.CHFileSystem;
import ch.perspective.views.CHPreferenceView;
import ch.view.CHEntryDialog;
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

	private static CHUserLogWriter log;

	private CHConnection conn;
	private String user;
	private String password;
	private int port;
	private CHMemberSelectorFrame memberSelector;
	// private List<CHUserState> userStates = new ArrayList<CHUserState>();
	private IWorkbenchWindow window;

	public static CHUserLogWriter getLog() {
		return log;
	}

	public static void setLog(CHUserLogWriter log) {
		CheCoProManager.log = log;
	}

	public CheCoProManager(IWorkbenchWindow window) {

		System.out.println(ResourcesPlugin.getWorkspace().getRoot()
				.getProject("final").toString());
		this.window = window;

		setWorkbenchWindowToViews();
		addListners();
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
		//
		// IWorkbenchPage page = window.getActivePage();
		// CHMemberDirectoryView memberDirectoryView;
		// try {
		// memberDirectoryView = (CHMemberDirectoryView) page
		// .showView("ch.memberDirectoryView");
		// memberDirectoryView.setWindow(window);
		// } catch (PartInitException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 各種リスナの登録
	 */
	private void addListners() {
		getCHService().addExecutionListener(executionListner);

		window.getWorkbench().getActiveWorkbenchWindow().getPartService()
				.addPartListener(partListner);
	}

	public ICommandService getCHService() {
		return (ICommandService) Activator.getDefault().getWorkbench()
				.getService(ICommandService.class);
	}

	private void removeListners() {
		getCHService().removeExecutionListener(executionListner);
		// TODO 要ぬるぽ解消
		window.getWorkbench().getActiveWorkbenchWindow().getPartService()
				.removePartListener(partListner);
	}

	// private void removeListners() {
	// geCHService().removeExecutionListener(saveListener);
	//
	// window.getWorkbench().getActiveWorkbenchWindow().getPartService()
	// .removePartListener(partListner);
	// }

	/***************
	 * 各種リスナの設定
	 ***************/
	private IExecutionListener executionListner = new IExecutionListener() {

		@Override
		public void preExecute(String commandId, ExecutionEvent event) {

		}

		@Override
		public void postExecuteSuccess(String commandId, Object returnValue) {
			System.out.println(commandId);
			if (commandId.endsWith("org.eclipse.ui.file.save")
					|| commandId.endsWith("org.eclipse.ui.file.refresh")) {
				conn.write(new CHFilelistResponse(user, CHFileSystem
						.getEclipseProjectFileList()));
			} else if (commandId.endsWith("org.eclipse.ui.edit.paste")) {
				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				Transferable object = clipboard.getContents(null);
				String str = "";
				try {
					str = (String) object
							.getTransferData(DataFlavor.stringFlavor);
					log.paste(getCurrentFile().toString(), str);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
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

	private IDocumentListener documentListner = new IDocumentListener() {

		@Override
		public void documentChanged(DocumentEvent event) {
			System.out.println(getActivePresEditor().getViewer().getTopIndex());
			System.out.println(getActivePresEditor().getViewer()
					.getTextWidget().getTopPixel());
			sourceChanged(event.getDocument().get(), getActivePresEditor()
					.getViewer().getTextWidget().getTopPixel());
		}

		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {

		}
	};

	private IPartListener partListner = new IPartListener() {

		@Override
		public void partOpened(IWorkbenchPart part) {

		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
			if (part instanceof PresExtendedJavaEditor) {
				IDocument doc = getActivePresEditor().getDoc();
				doc.removeDocumentListener(documentListner);
			}
		}

		@Override
		public void partClosed(IWorkbenchPart part) {

		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {

		}

		@Override
		public void partActivated(IWorkbenchPart part) {

			if (part instanceof PresExtendedJavaEditor) {
				IDocument doc = getActivePresEditor().getDoc();
				doc.addDocumentListener(documentListner);
			}
		}
	};

	public PresExtendedJavaEditor getActivePresEditor() {
		return (PresExtendedJavaEditor) window.getActivePage()
				.getActiveEditor();
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
		// TODO Loginボタン操作
		if (memberSelector.isVisible()) {
			memberSelector.close();
		}
		removeListners();
		new Thread(new LoginButtonUpdater(false)).start();
		log.logout();
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
		} else if (obj instanceof CHEntryResult) {
			processEntryResult((CHEntryResult) obj);
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
		} else if (obj instanceof CHSourceChanged) {
			processSourceChanged((CHSourceChanged) obj);
		} else if (obj instanceof CHLogoutResult) {
			processLogoutResult((CHLogoutResult) obj);
		}
	}

	private void processLoginResult(CHLoginResult result) {
		if (result.isResult() == -1) {
			// TODO エラーダイアログを出す
			System.out.println("login failure");
			conn.close();
		} else if (result.isResult() == 1) {
			log.login();
			System.out.println("login success");
			memberSelector = new CHMemberSelectorFrame(user, conn);
			// memberSelector.setWindow(window);
			// memberSelector.setPage(window.getActivePage());
			memberSelector.open();
		} else if (result.isResult() == 0) {
			// TODO 最前面へ
			// TODO 入力間違えないように工夫
			CHEntryDialog entryDialog = new CHEntryDialog();
			entryDialog.open();
			entryDialog.setAlwaysOnTop(true);
			user = entryDialog.getUser();
			password = entryDialog.getPassword();
			if (!user.equals("")) {
				conn.write(new CHEntryRequest(user, password));
			} else {
				conn.close();
			}
		}
	}

	private void processEntryResult(CHEntryResult result) {
		if (result.isResult()) {
			// 登録成功
			conn.write(new CHLoginRequest(user, password, DEFAULT_COLOR));
		} else {
			// 登録失敗
			System.out.println("Entry failed");
			conn.close();
		}
	}

	private void processLoginMemberChanged(CHLoginMemberChanged result) {
		// new Thread(new MemberStateUpdater(result)).start();
		memberSelector.setMembers(result.getUserStates());
		memberSelector.setUserStates(result.getUserStates());
		memberSelector.userStateChanged();
	}

	private void processFilelistRequest() {
		CFileHashList fileList = CHFileSystem.getEclipseProjectFileList();
		conn.write(new CHFilelistResponse(user, fileList));
	}

	private void processFileListResponse(CHFilelistResponse response) {

		// TODO リネーム反映されない
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

	private void processSourceChanged(CHSourceChanged responce) {
		if (memberSelector.cheackCHEditor(responce.getUser(),
				responce.getCurrentFileName())) {
			memberSelector.showSource(responce.getUser(), responce.getSource(),
					responce.getTopPixel());
		}
	}

	private void processLogoutResult(CHLogoutResult result) {
		if (result.getUser().equals(user)) {
			memberSelector.close();
			removeListners();
			conn.close();
			log.logout();
			new Thread(new LoginButtonUpdater(false)).start();
		}
	}

	class LoginButtonUpdater implements Runnable {

		private boolean login;

		public LoginButtonUpdater(boolean login) {
			this.login = login;
		}

		@Override
		public void run() {
			window.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					IWorkbenchPage page = window.getActivePage();
					CHPreferenceView prefView;
					try {
						prefView = (CHPreferenceView) page
								.showView("ch.preferenceView");
						prefView.isLogined(login);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			});
		}

	}

	// class MemberStateUpdater implements Runnable {
	//
	// private CHLoginMemberChanged result;
	//
	// public MemberStateUpdater(CHLoginMemberChanged result) {
	// this.result = result;
	// }
	//
	// @Override
	// public void run() {
	// window.getWorkbench().getDisplay().asyncExec(new Runnable() {
	//
	// @Override
	// public void run() {
	// userStates = result.getUserStates();
	// IWorkbenchPage page = window.getActivePage();
	// CHMemberStateView memberStateView;
	// try {
	// memberStateView = (CHMemberStateView) page
	// .showView("ch.memberStateView");
	// memberStateView.setUserStates(userStates);
	// } catch (PartInitException e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }
	// }

	public String getCurrentFileName() {

		return getCurrentFile().getName();
	}

	public IFile getCurrentFile() {
		IFileEditorInput input = (IFileEditorInput) window.getActivePage()
				.getActiveEditor().getEditorInput();
		return input.getFile();
	}

	private void sourceChanged(String source, int topPixel) {
		conn.write(new CHSourceChanged(user, source, getCurrentFileName(),
				topPixel));
	}

	public CHConnection getConn() {
		return conn;
	}

	public String getUser() {
		return user;
	}
}
