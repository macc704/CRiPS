package ronproeditor.ext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import ronproeditor.IREResourceRepository;
import ronproeditor.REApplication;
import ronproeditor.views.REFrame;
import ch.conn.CHCliant;
import ch.conn.framework.CHConnection;
import ch.conn.framework.CHFile;
import ch.conn.framework.CHLoginCheck;
import ch.conn.framework.CHUserLogWriter;
import ch.conn.framework.CHUserState;
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
import ch.conn.framework.packets.CHLogoutRequest;
import ch.conn.framework.packets.CHLogoutResult;
import ch.conn.framework.packets.CHSourceChanged;
import ch.library.CHFileSystem;
import ch.util.CHBlockEditorController;
import ch.util.CHComponent;
import ch.util.CHEvent;
import ch.util.CHListener;
import ch.view.CHEntryDialog;
import ch.view.CHMemberSelectorFrame;
import ch.view.CHPullDialog;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.sync.CFileHashList;
import clib.common.system.CJavaSystem;
import clib.preference.model.CAbstractPreferenceCategory;

public class RECheCoProManager {

	public static final String APP_NAME = "CheCoPro";
	public static final String DEFAULT_NAME = "";
	public static final String DEFAULT_PASSWAOD = "";
	public static final Color DEFAULT_COLOR = Color.WHITE;
	public static final int DEFAULT_PORT = 20000;
	public static final String IP = "localhost";
	public static final int DEFAULT_LANGUAGE = 0;

	private static int CTRL_MASK = InputEvent.CTRL_MASK;
	static {
		if (CJavaSystem.getInstance().isMac()) {
			CTRL_MASK = InputEvent.META_MASK;
		}
	}

	private REApplication application;
	private CHConnection conn;
	private CHMemberSelectorFrame msFrame;
	private List<CHUserState> userStates = new ArrayList<CHUserState>();
	private String user = DEFAULT_NAME;
	private String password = DEFAULT_PASSWAOD;
	private int port = DEFAULT_PORT;
	private Color color = DEFAULT_COLOR;
	private int language = DEFAULT_LANGUAGE;
	private HashMap<String, REApplication> chFrameMap = new HashMap<String, REApplication>();
	private CHUserLogWriter logWriter;
	private HashMap<String, RECheCoProViewer> chViewers = new HashMap<String, RECheCoProViewer>();

	public static void main(String[] args) {
		new RECheCoProManager();
	}

	public RECheCoProManager(REApplication application) {
		this.application = application;
		initializePreference();
	}

	public RECheCoProManager() {
		start();
	}

	private void initializePreference() {
		application.getPreferenceManager().putCategory(
				new CheCoProPreferenceCategory());
	}

	/*******************
	 * フレーム・リスナ関係
	 *******************/

	private PropertyChangeListener rePropertyChangeListener;
	private KeyListener reKeyListener;

	private void initializeREListener() {

		initializeREMenuListener(application);

		rePropertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				
				if(evt.getPropertyName().equals("prepareDocumentClose")) {
					removeREKeyListner();
				} else if (evt.getPropertyName().equals("documentOpened")) {
					initializeREKeyListener();
					processFilelistRequest(new CHFilelistRequest(user));
				}
				
			}
		};

		application.getSourceManager().addPropertyChangeListener(
				rePropertyChangeListener);
	}

	private void initializeREKeyListener() {

		reKeyListener = new KeyAdapter() {
			int mod;

			@Override
			public void keyPressed(KeyEvent e) {
				mod = e.getModifiers();
				if (e.getKeyCode() == KeyEvent.VK_V) {
					if ((mod & CTRL_MASK) != 0) {
						System.out.println("paste");
						writePasteLog(application.getSourceManager()
								.getCCurrentFile(), getClipbordString());
					}
				}
			}
		};

		application.getFrame().getEditor().getViewer().getTextPane()
				.addKeyListener(reKeyListener);
	}
	
	public String getClipbordString() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		
		try {
			return (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void send() {
		sendFiles();
		sendText();
	}
	
	public void sendFiles() {
		if (conn != null && isConnect()) {
			processFilelistRequest(new CHFilelistRequest(user));
		}
	}
	
	public void sendText() {
		
		if (conn != null && isConnect()) {
			conn.write(new CHSourceChanged(user, application.getFrame()
				.getEditor().getViewer().getText(), application
				.getSourceManager().getCurrentFile().getName(), 
				application.getFrame().getEditor().getViewer().getScroll()
				.getViewport().getViewPosition()));
		}
	}

	private ActionListener copyListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			writeCopyLog(application);
		}
	};
	
	private ActionListener pasteListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			writePasteLog(application.getSourceManager().getCCurrentFile(), getClipbordString());
		}
	};
	
	private ActionListener saveListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			sendText();
			processFilelistRequest(new CHFilelistRequest(user));
		}
	};
	
	private void initializeREMenuListener(final REApplication application) {
		
		application.getFrame().getJMenuBar().getMenu(1).getItem(5)
				.addActionListener(pasteListener);
		
		application.getFrame().getJMenuBar().getMenu(0).getItem(8)
				.addActionListener(saveListener);
	}
	
	private void removeREMenuListener(){
		
		application.getFrame().getJMenuBar().getMenu(1).getItem(5)
				.removeActionListener(pasteListener);
		
		application.getFrame().getJMenuBar().getMenu(0).getItem(8)
				.removeActionListener(saveListener);
	}

	private void writeCopyLog(REApplication application) {
		String code = application.getFrame().getEditor().getViewer()
				.getTextPane().getSelectedText();
		logWriter.writeCommand(CHUserLogWriter.COPY_CODE);
		logWriter.writeFrom(application.getSourceManager().getCCurrentFile());
		logWriter.writeCode(code);
		logWriter.addRowToTable();
	}

	private void writePasteLog(CFile file, String code) {
		logWriter.writeCommand(CHUserLogWriter.PASTE_CODE);
		logWriter.writeTo(file);
		logWriter.writeCode(code);
		logWriter.addRowToTable();
	}

	private void setMemberSelectorListner() {
		List<JButton> buttons = new ArrayList<JButton>(msFrame.getButtons());
		for (JButton aButton : buttons) {
			aButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String user = e.getActionCommand();
					msFrame.setMembers(userStates);
					setMemberSelectorListner();

					if (user.equals(msFrame.getUser())) {
						application.getFrame().toFront();
					} else {
						if (chFrameMap.get(user) != null) {
							chFrameMap.get(user).getFrame().toFront();
						} else {
							doOpenNewCH(user);
							conn.write(new CHFilelistRequest(user));
						}
					}
				}
			});
		}

		msFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeCHEditor();
				conn.write(new CHLogoutRequest(user));
			}
		});
	}

	public void doOpenNewCH(String user) {
		REApplication chApplication = application.doOpenNewRE("MyProjects/.CH/"
				+ user);
		chApplication.setChBlockEditorController(new CHBlockEditorController(user));
		initializeCHEditor(chApplication, user);
		chFrameMap.put(user, chApplication);
	}

	private void initializeCHEditor(REApplication chApplication, String user) {
		chApplication.getFrame().setTitle(user + "-" + APP_NAME + " Editor");
		chApplication.getFrame().setDefaultCloseOperation(
				JFrame.DISPOSE_ON_CLOSE);
		initializeCHListeners(chApplication, user);
		initializeCHMenu(chApplication, user);
	}

	private void initializeCHMenu(final REApplication chApplication,
			final String user) {
		JMenuBar menuBar = chApplication.getFrame().getJMenuBar();
		for (int i = 7; i >= 0; i--) {
			menuBar.getMenu(0).remove(i);
		}
		menuBar.getMenu(0).remove(1);
		menuBar.getMenu(0).remove(1);
		menuBar.getMenu(0).remove(4);
		
		menuBar.getMenu(1).getItem(3).addActionListener(copyListener);
		menuBar.getMenu(1).getItem(4).addActionListener(copyListener);

		int menuCount = menuBar.getMenu(3).getItemCount() - 1;
		for (int i = menuCount; i >= 0; i--) {
			menuBar.getMenu(3).remove(i);
		}
		
		// --BlockEditor
		Action actionOpenBlockEditor;
		actionOpenBlockEditor = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				File selectedFile = null;
				String langDefFilePath = "";
				if (chApplication.getChBlockEditorController().isFileOpened()){
					selectedFile = chApplication.getResourceRepository().getCCurrentFile().toJavaFile();
					langDefFilePath = chApplication.getResourceRepository().getCCurrentProject()
							.getAbsolutePath().toString() + "/lang_def_project.xml";
				}
				openBlockEditorForCH(chApplication.getChBlockEditorController(), selectedFile, langDefFilePath);
			}
		};
		
		actionOpenBlockEditor.putValue(Action.NAME, "Open BlockEditor");
		actionOpenBlockEditor.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL_MASK));
		actionOpenBlockEditor.setEnabled(true);
		
		menuBar.getMenu(3).add(actionOpenBlockEditor);
		
		menuBar.getMenu(4).remove(0);

		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			menuBar.getMenu(i).setBackground(getUserColor(user));
		}

		final String syncLabel;
		final String nonSyncLabel;
		if (language == 0){
			syncLabel = "同期中";
			nonSyncLabel = "非同期中";
		} else {
			syncLabel = "sync";
			nonSyncLabel = "async";
		}
		
		final JToggleButton connButton = new JToggleButton(syncLabel, true);
		for (CHUserState aUserState : userStates) {
			if (user.equals(aUserState.getUser()) && !aUserState.isLogin()) {
				connButton.doClick();
				connButton.setEnabled(false);
				connButton.setText(nonSyncLabel);
			}
		}

		connButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (connButton.isSelected()) {
					conn.write(new CHFilelistRequest(user));
					connButton.setText(syncLabel);
				} else {
					connButton.setText(nonSyncLabel);
				}
				if (chApplication.getFrame().getEditor() != null) {
					chApplication.getFrame().getEditor().getViewer()
							.getTextPane()
							.setEditable(!connButton.isSelected());
				}
				changeCHMenubar(chApplication, connButton.isSelected());
			}
		});

		menuBar.add(connButton);

		JButton pullButton = new JButton();
		
		if (language == 0) {
			pullButton.setText("取り込み↓");
		} else {
			pullButton.setText("Import↓");
		}
		
		pullButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CHPullDialog pullDialog = new CHPullDialog(user, language);
				boolean java = pullDialog.isJavaChecked();
				boolean material = pullDialog.isMaterialCecked();
				if (java || material) {
					doPull(user, createCFileFilter(java, material));
				}
				application.doRefresh();
			}

		});

		menuBar.add(pullButton);

		menuBar.setBackground(getUserColor(user));
		chApplication.getFrame().setJMenuBar(menuBar);

		changeCHMenubar(chApplication, connButton.isSelected());
	}

	private void doPull(final String user, CFileFilter filter) {
		logWriter.writeFrom(user);
		logWriter.addRowToTable();
		CDirectory from = CHFileSystem.getUserDirForClient(user);
		CDirectory to = CHFileSystem.getFinalProjectDir();
		CHFileSystem.pull(from, to, filter);
	}

	private CFileFilter createCFileFilter(boolean java, boolean material) {
		if (java && material) {
			logWriter.writeCommand(CHUserLogWriter.COPY_ALL_FILE);
			return CFileFilter.IGNORE_BY_NAME_FILTER(".*", "*.class", ".*xml");
		} else if (java && !material) {
			logWriter.writeCommand(CHUserLogWriter.COPY_JAVA_FILE);
			return CFileFilter.ACCEPT_BY_NAME_FILTER("*.java");
		} else if (!java && material) {
			logWriter.writeCommand(CHUserLogWriter.COPY_MATERIAL_FILE);
			return CFileFilter.IGNORE_BY_NAME_FILTER(".*", "*.class", "*.xml",
					"*.java");
		}
		return null;
	}

	private void initializeCHListeners(final REApplication chApplication,
			final String user) {

		removeListeners(chApplication.getFrame());

		chApplication.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				chApplication.getChBlockEditorController().close();
				chFrameMap.remove(user);
				msFrame.setMembers(userStates);
				setMemberSelectorListner();
			}
		});

		chApplication.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						setCHTitleBar(chApplication, user);
						if (evt.getPropertyName().equals(IREResourceRepository.DOCUMENT_OPENED)) {
							initializeCHKeyListener(chApplication, user);
							JToggleButton connButton = (JToggleButton) chApplication
									.getFrame().getJMenuBar().getComponent(5);
							chApplication.getFrame().getEditor().getViewer()
									.getTextPane()
									.setEditable(!connButton.isSelected());
							changeCHMenubar(chApplication,
									connButton.isSelected());
							
							chApplication.getChBlockEditorController().setFileOpened(true);
							
							File selectedFile = chApplication.getResourceRepository().getCCurrentFile().toJavaFile();
							String langDefFilePath = chApplication.getResourceRepository()
									.getCCurrentProject().getAbsolutePath().toString() + "/lang_def_project.xml";
							reloadBlockEditor(chApplication.getChBlockEditorController(), selectedFile, langDefFilePath);
						} else {
							chApplication.getChBlockEditorController().setFileOpened(false);
							reloadBlockEditor(chApplication.getChBlockEditorController(), null, "");
						}
					}
				});
	}
	
	private void reloadBlockEditor(CHBlockEditorController bc, File selectedFile, String langDefFilePath) {
		String xmlFilePaht = "";
		
		if (selectedFile != null) {
			xmlFilePaht = bc.createXmlFromJava(selectedFile, REApplication.SRC_ENCODING,
					application.getLibraryManager().getLibsAsArray());
		}
		
		bc.reloadBlockEditor(langDefFilePath, xmlFilePaht);
	}
	
	public void openBlockEditorForCH(CHBlockEditorController bc,File selectedFile, String langDefFilePath) {
		String xmlFilePath = "";
		
		if (selectedFile != null) {
			xmlFilePath = bc.createXmlFromJava(selectedFile, REApplication.SRC_ENCODING,
					application.getLibraryManager().getLibsAsArray());
		}
		bc.openBlockEditor(langDefFilePath, xmlFilePath);
	}

	private void initializeCHKeyListener(final REApplication chApplication, final String user) {
		List<KeyListener> keyListeners = Arrays.asList(chApplication.getFrame()
				.getEditor().getViewer().getTextPane().getKeyListeners());

		for (KeyListener aKeyListener : keyListeners) {
			chApplication.getFrame().getEditor().getViewer().getTextPane()
					.removeKeyListener(aKeyListener);
		}

		chApplication.getFrame().getEditor().getViewer().getTextPane()
				.addKeyListener(new KeyAdapter() {

					@Override
					public void keyPressed(KeyEvent e) {
						int mod = e.getModifiers();
						if (e.getKeyCode() == KeyEvent.VK_C
								|| e.getKeyCode() == KeyEvent.VK_X) {
							if ((mod & CTRL_MASK) != 0) {
								writeCopyLog(chApplication);
							}
						} else if (e.getKeyCode() == KeyEvent.VK_S) {
							if ((mod & CTRL_MASK) != 0) {
								setCHTitleBar(chApplication, user);
							}
						}
					}

					@Override
					public void keyTyped(KeyEvent e) {
						setCHTitleBar(chApplication, user);
					}

					@Override
					public void keyReleased(KeyEvent e) {
						setCHTitleBar(chApplication, user);
					}
				});
	}

	private void removeListeners(REFrame frame) {
		List<WindowListener> listeners = new ArrayList<WindowListener>();
		listeners = Arrays.asList(frame.getWindowListeners());
		for (WindowListener aListener : listeners) {
			frame.removeWindowListener(aListener);
		}
	}

	private void setCHTitleBar(REApplication chApplication, String user) {
		String title = user + "-" + APP_NAME + " Editor";
		if (chApplication.getFrame().getEditor() != null) {
			if (chApplication.getFrame().getEditor().isDirty()) {
				title += "*";
			}
		}
		chApplication.getFrame().setTitle(title);
	}

	private Color getUserColor(String user) {
		for (CHUserState aUserState : userStates) {
			if (user.equals(aUserState.getUser())) {
				return aUserState.getColor();
			}
		}
		return DEFAULT_COLOR;
	}

	private void changeCHMenubar(REApplication chApplication, boolean isSelected) {
		for (int i = 0; i < 7; i++) {
			if (i != 5) {
				chApplication.getFrame().getJMenuBar().getComponent(i)
						.setEnabled(!isSelected);
			}
		}
	}

	/********************
	 * クライアントメイン動作
	 ********************/

	public void start() {		
		CHCliant client = new CHCliant(port, user, password, color);
		client.setComponent(addCHListener());
		client.start();
	}
	
	private CHComponent addCHListener() {
		CHComponent component = new CHComponent();
		component.addCHListener(new CHListener() {
			
			@Override
			public void processChanged(CHEvent e) {
				// プロセスの変化を受け取る
			}
			
			@Override
			public void memberSelectorChanged(CHEvent e) {
				String message = e.getMessage();
				if (message.equals("MyNameClicked")) {
					// 自分の名前がクリックされたら論プロを前面に
					application.getFrame().toFront();
				} else if (message.equals("AlreadyOpened")) {
					// 既に開いているメンバだったら前面に
					chViewers.get(component.getUser()).getApplication().getFrame().toFront();
				} else if (message.equals("NewOpened")) {
					// 開いていなかったら開く
					RECheCoProViewer chViewer = new RECheCoProViewer(component.getUser());
					chViewer.doOpenNewCH(application);
					chViewers.put(component.getUser(), chViewer);
				}
			}
		});
		return component;
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
			logWriter.writeCommand(CHUserLogWriter.LOGIN);
			logWriter.addRowToTable();
			initializeREListener();
			msFrame = new CHMemberSelectorFrame(user);
			msFrame.open();
		}
	}

	private void processEntryResult(CHEntryResult result) {
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
		msFrame.setMembers(result.getUserStates());
		setMemberSelectorListner();
		for (CHUserState aUserState : userStates) {
			if (chFrameMap.containsKey(aUserState.getUser())) {
				controlSync(chFrameMap.get(aUserState.getUser()),
						aUserState.isLogin());
			}
		}
	}

	private void controlSync(REApplication chApplication, boolean login) {
		JToggleButton connButton = (JToggleButton) chApplication.getFrame()
				.getJMenuBar().getComponent(5);
		if (connButton.isSelected()) {
			if (!login) {
				connButton.doClick();
				connButton.setEnabled(false);
			}
		} else {
			if (login) {
				connButton.setEnabled(true);
				connButton.doClick();
			}
		}
	}

	private void processSourceChanged(CHSourceChanged response) {
		final String sender = response.getUser();
		final String source = response.getSource();
		final String senderCurrentFile = response.getCurrentFileName();
		final Point point = response.getPoint();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (shouldPrintSource(sender, senderCurrentFile)) {
					chFrameMap.get(sender).getFrame().getEditor()
							.setText(source);
					chFrameMap.get(sender).doSave();
					chFrameMap.get(sender).getFrame().setTitle(sender + "-" + APP_NAME + " Editor");
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							chFrameMap.get(sender).getFrame().getEditor()
							.getViewer().getScroll().getViewport()
							.setViewPosition(point);
						}
					});
				}
			}
		});
	}

	private void processLogoutResult(CHLogoutResult result) {
		if (user.equals(result.getUser())) {
			logWriter.writeCommand(CHUserLogWriter.LOGOUT);
			logWriter.addRowToTable();
			logWriter.saveTableToFile();
			conn.close();
			removeListeners();
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
		if (chFrameMap.containsKey(response.getUser())) {
			chFrameMap.get(response.getUser()).doRefresh();
		}
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
		int fileSize = notice.getFileSize();
		System.out.println("size ; " + fileSize);
	}

	/*********
	 * 切断処理
	 *********/

	private void connectionKilled() {
		logWriter.writeCommand(CHUserLogWriter.LOGOUT);
		logWriter.addRowToTable();
		logWriter.saveTableToFile();
		resetMenubar();
		closeCHEditor();
		if (msFrame != null) {
			msFrame.dispose();
		}
		removeListeners();
	}

	private void removeListeners() {
		application.getSourceManager().removePropertyChangeListener(
				rePropertyChangeListener);
		removeREKeyListner();
		removeREMenuListener();
	}
	
	private void removeREKeyListner() {
		if (reKeyListener != null) {
			application.getFrame().getEditor().getViewer().getTextPane()
					.removeKeyListener(reKeyListener);
		}
	}

	private void resetMenubar() {
		JMenuBar menubar = application.getFrame().getJMenuBar();
		application.getFrame().setJMenuBar(menubar);
	}

	private void closeCHEditor() {
		for (CHUserState userState : userStates) {
			if (chFrameMap.containsKey(userState.getUser())) {
				chFrameMap.get(userState.getUser()).getFrame()
						.setVisible(false);
				closeCHBlockEditor(chFrameMap.get(userState.getUser()));
				chFrameMap.remove(userState.getUser());
			}
		}
	}
	
	private void closeCHBlockEditor(REApplication chApplication) {
		chApplication.getChBlockEditorController().close();
	}

	/**********
	 * 判定関係
	 **********/

	public boolean shouldPrintSource(String sender, String senderCurrentFile) {
		if (!chFrameMap.containsKey(sender)) {
			return false;
		} else {
			JToggleButton button = (JToggleButton) chFrameMap.get(sender)
					.getFrame().getJMenuBar().getComponent(5);
			if (!button.isSelected()) {
				return false;
			}
		}
		if (chFrameMap.get(sender).getFrame().getEditor() == null) {
			return false;
		}
		if (!chFrameMap.get(sender).getSourceManager().getCurrentFile()
				.getName().equals(senderCurrentFile)) {
			return false;
		}
		return true;
	}
	
	public boolean isConnect() {
		return conn.established();
	}

	/****************
	 * preference関係
	 ****************/

	private static final String LOGINID_LABEL = "CheCoPro.loginid";
	private static final String PASSWORD_LABEL = "CheCoPro.password";
	private static final String PORTNUMBER_LABEL = "CheCoPro.portnumber";
	private static final String COLOR_LABEL = "CheCoPro.color";
	private static final String LANGUAGE_LABEL = "CheCoPro.language";

	class CheCoProPreferenceCategory extends CAbstractPreferenceCategory {

		private static final long serialVersionUID = 1L;

		private JTextField nameField = new JTextField(15);
		private JPasswordField passField = new JPasswordField(15);
		private JComboBox<Integer> portBox = new JComboBox<Integer>();
		private JComboBox<String> colorBox = new JComboBox<String>();
		private JComboBox<String> languageBox = new JComboBox<String>();
		private JPanel panel = new CheCoProPreferencePanel();

		@Override
		public String getName() {
			return "CheCoPro";
		}

		@Override
		public JPanel getPage() {
			return panel;
		}

		@Override
		public void load() {
			if (getRepository().exists(LOGINID_LABEL)) {
				user = getRepository().get(LOGINID_LABEL);
				nameField.setText(user);
			}
			if (getRepository().exists(PASSWORD_LABEL)) {
				password = getRepository().get(PASSWORD_LABEL);
				passField.setText(password);
			}
			if (getRepository().exists(PORTNUMBER_LABEL)) {
				portBox.setSelectedIndex(Integer.parseInt(getRepository().get(
						PORTNUMBER_LABEL)));
				port = Integer.parseInt(getRepository().get(PORTNUMBER_LABEL));
			}
			if (getRepository().exists(COLOR_LABEL)) {
				colorBox.setSelectedItem(getRepository().get(COLOR_LABEL));
				changeStringToColor(getRepository().get(COLOR_LABEL));
			}
			if (getRepository().exists(LANGUAGE_LABEL)) {
				languageBox.setSelectedIndex(Integer.parseInt(getRepository()
						.get(LANGUAGE_LABEL)));
				language = Integer.parseInt(getRepository().get(LANGUAGE_LABEL));
			}

			port += 20000;
		}

		private void changeStringToColor(String str) {
			if (str.equals("GRAY")) {
				color = Color.GRAY;
			} else if (str.equals("BLUE")) {
				color = Color.BLUE;
			} else if (str.equals("PINK")) {
				color = Color.PINK;
			} else if (str.equals("YELLOW")) {
				color = Color.YELLOW;
			} else if (str.equals("ORANGE")) {
				color = Color.ORANGE;
			} else if (str.equals("CYAN")) {
				color = Color.CYAN;
			}
		}

		@Override
		public void save() {
			user = nameField.getText();
			password = String.valueOf(passField.getPassword());
			port = portBox.getSelectedIndex() + 20000;
			changeStringToColor((String) colorBox.getSelectedItem());
			language = languageBox.getSelectedIndex();
			
			getRepository().put(LOGINID_LABEL, user);
			getRepository().put(PASSWORD_LABEL, password);
			getRepository().put(PORTNUMBER_LABEL,
					Integer.toString(portBox.getSelectedIndex()));
			getRepository().put(COLOR_LABEL,
					(String) colorBox.getSelectedItem());
			getRepository().put(LANGUAGE_LABEL, 
					Integer.toString(languageBox.getSelectedIndex()));
		}

		class CheCoProPreferencePanel extends JPanel {

			private static final long serialVersionUID = 1L;

			public CheCoProPreferencePanel() {
				FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);

				JPanel namePanel = new JPanel(flowLayout);
				namePanel.add(new JLabel("Name : "));
				namePanel.add(nameField);

				JPanel passPanel = new JPanel(flowLayout);
				passPanel.add(new JLabel("Password : "));
				passPanel.add(passField);

				JPanel portPanel = new JPanel(flowLayout);
				portPanel.add(new JLabel("Group : "));
				for (int i = 0; i < 51; i++) {
					portBox.addItem(i);
				}
				portPanel.add(portBox);

				JPanel colorPanel = new JPanel(flowLayout);
				colorPanel.add(new JLabel("Color : "));
				colorBox.addItem("GRAY");
				colorBox.addItem("BLUE");
				colorBox.addItem("PINK");
				colorBox.addItem("YELLOW");
				colorBox.addItem("ORANGE");
				colorBox.addItem("CYAN");				
				colorPanel.add(colorBox);
				
				JPanel languagePanel = new JPanel(flowLayout);
				languagePanel.add(new JLabel("Language : "));
				languageBox.addItem("日本語");
				languageBox.addItem("English");
				languagePanel.add(languageBox);
				
				this.add(namePanel, BorderLayout.CENTER);
				this.add(passPanel, BorderLayout.CENTER);
				this.add(portPanel, BorderLayout.CENTER);
				this.add(colorPanel, BorderLayout.CENTER);
				this.add(languagePanel, BorderLayout.CENTER);
			}
		}

	}

}