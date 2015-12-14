package ronproeditor.ext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import pres.core.model.PRCheCoProLog;
import pres.core.model.PRLog;
import ronproeditor.REApplication;
import ronproeditor.RESourceManager;
import ch.conn.CHCliant;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHFilelistRequest;
import ch.conn.framework.packets.CHFilelistResponse;
import ch.conn.framework.packets.CHLogoutRequest;
import ch.conn.framework.packets.CHSourceChanged;
import ch.library.CHFileSystem;
import ch.library.CHLib;
import ch.util.CHComponent;
import ch.util.CHEvent;
import ch.util.CHListener;
import ch.view.CHErrorDialog;
import clib.common.filesystem.CPath;
import clib.common.system.CJavaSystem;
import clib.preference.model.CAbstractPreferenceCategory;

public class RECheCoProManager {

	public static final String APP_NAME = "CheCoPro";
	public static final int MENU_INDEX_FILE = 0;
	public static final int MENU_INDEX_EDIT = 1;
	public static final int ITEM_INDEX_PASTE = 5;
	public static final int ITEM_INDEX_SAVE = 8;
	public static final int ITEM_INDEX_REFRESH = 12;
	public static final int ITEM_INDEX_EXIT = 14;

	private static int CTRL_MASK = InputEvent.CTRL_MASK;
	static {
		if (CJavaSystem.getInstance().isMac()) {
			CTRL_MASK = InputEvent.META_MASK;
		}
	}

	private REApplication application;
	private CHCliant cliant;
	private List<CHUserState> userStates = new ArrayList<CHUserState>();
	private String user = CHCliant.DEFAULT_NAME;
	private String password = CHCliant.DEFAULT_PASSWAOD;
	private int port = CHCliant.DEFAULT_PORT;
	private Color color = CHCliant.DEFAULT_COLOR;
	private HashMap<String, RECheCoProViewer> chViewers = new HashMap<String, RECheCoProViewer>();
	
	private CheCoProPreferenceCategory pref;

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
		pref = new CheCoProPreferenceCategory();
		pref.getPage().setEnabled(true);
		application.getPreferenceManager().putCategory(pref);
	}

	/*******************
	 * フレーム・リスナ関係
	 *******************/

	private PropertyChangeListener rePropertyChangeListener;
	private KeyListener reKeyListener;
	private WindowListener reWindowListener;
	private WindowFocusListener reWindowFocusListener;

	private void initializeREListener() {

		initializeREMenuListener();
		initializeREPropertyChangeListener();
		initializeREWindowListener();
		initializeREWindowFocusListener();
	}
	
	private void initializeREPropertyChangeListener() {
		rePropertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				
				if(evt.getPropertyName().equals(RESourceManager.PREPARE_DOCUMENT_CLOSE)) {
					removeREKeyListner();
				} else if (evt.getPropertyName().equals(RESourceManager.DOCUMENT_OPENED)) {
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
						writePartialImportLog();
					}
				}
			}
		};

		application.getFrame().getEditor().getViewer().getTextPane()
				.addKeyListener(reKeyListener);
	}
	
	private void initializeREWindowListener() {
		removeWindowListeners();
		reWindowListener = new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				logout();
				application.doExit();
			}
		};
		
		application.getFrame().addWindowListener(reWindowListener);
	}
	
	private void removeWindowListeners() {
		WindowListener[] listeners = application.getFrame().getWindowListeners();
		for (int i = 0; i <listeners.length ; i++) {
			application.getFrame().removeWindowListener(listeners[i]);
		}
	}
	
	private void initializeREWindowFocusListener() {
		reWindowFocusListener = new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				writeFocusLostLog();
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				writeFocusGainedLog();
			}
		};
		application.getFrame().addWindowFocusListener(reWindowFocusListener);
	}
	
	private ActionListener pasteListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			writePartialImportLog();
		}
	};
	
	private ActionListener saveListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			cliant.getConn().write(new CHFilelistResponse(user
					, CHFileSystem.createFileList(CHFileSystem.getSyncProjectDir())));
			send();
		}
	};
	
	private ActionListener refreshListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			processFilelistRequest(new CHFilelistRequest(user));
		}
	};
	
	private ActionListener exitListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			logout();
		}
	};
	
	private void initializeREMenuListener() {
		
		application.getFrame().getJMenuBar().getMenu(MENU_INDEX_EDIT).getItem(ITEM_INDEX_PASTE)
				.addActionListener(pasteListener);		
		application.getFrame().getJMenuBar().getMenu(MENU_INDEX_FILE).getItem(ITEM_INDEX_SAVE)
				.addActionListener(saveListener);
		application.getFrame().getJMenuBar().getMenu(MENU_INDEX_FILE).getItem(ITEM_INDEX_REFRESH)
				.addActionListener(refreshListener);
		application.getFrame().getJMenuBar().getMenu(MENU_INDEX_FILE).getItem(ITEM_INDEX_EXIT)
				.addActionListener(exitListener);
	}

	/********************
	 * クライアントメイン動作
	 ********************/

	public void start() {
		if (canStart()) {
			cliant = new CHCliant(port, user, password, color);
			cliant.setComponent(addCHListener());
			cliant.start();
		}
	}
	
	/**
	 * クライアントからのイベントを受け取るリスナの追加
	 * @return
	 */
	private CHComponent addCHListener() {
		CHComponent component = new CHComponent();
		component.addCHListener(new CHListener() {
			
			@Override
			public void processChanged(CHEvent e) {
				// クライアント状態変化
				processConnectionChanged(component, e.getMessage());
			}
			
			@Override
			public void memberSelectorChanged(CHEvent e) {
				// メンバセレクタの操作
				processMemberSelectorChanged(component, e.getMessage());
			}
		});
		return component;
	}
	
	/**
	 * 各プロセスの処理
	 * @param component
	 * @param message
	 */
	private void processConnectionChanged(CHComponent component, String message) {
		if (message.equals("LoginResultReceived")) {
			processLoginResult();
		} else if (message.equals("LoginMemberChanged")) {
			processLoginMemberChanged(component.getUserStates());
		} else if (message.equals("SourceChanged")) {
			processSourceChanged(component.getScPacket());
		} else if (message.equals("FileResponseReceived")) {
			processFileResponse(component.getUser());
		} else if (message.equals("LogoutResultReceived")) {
			processLogoutResult();
		} else if (message.equals("ConnectionKilled")) {
			connectionKilled();
		}
	}
	
	/**
	 * メンバセレクタの各状態の処理
	 * @param component
	 * @param message
	 */
	private void processMemberSelectorChanged(CHComponent component, String message) {
		if (message.equals("MyNameClicked")) {
			// 自分の名前がクリックされたら論プロを前面に
			application.getFrame().toFront();
		} else if (message.equals("AlreadyOpened")) {
			// 既に開いているメンバだったら前面に
			chViewers.get(component.getUser()).getApplication().getFrame().toFront();
		} else if (message.equals("NewOpened")) {
			// 開いていなかったら開く
			cliant.getConn().write(new CHFilelistRequest(component.getUser()));
			doOpenNewCH(component.getUser());
		} else if (message.equals("WindowClosing")) {
			// 開いているCHEditorを閉じる
			closeCHEditors();
			logout();
		}
	}
	
	public void doOpenNewCH(String user) {
		RECheCoProViewer chViewer = new RECheCoProViewer(user, cliant.getConn());
		chViewer.setUserStates(userStates);
		chViewer.doOpenNewCH(application);
		chViewer.getApplication().getFrame().addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				if (chViewers.containsKey(user)) {
					chViewers.remove(user);
					cliant.getProcessManager().getMemberSelector().removeEditorOpens(user);
				}
			}
		});
		chViewers.put(user, chViewer);
	}

	/**********************
	 * 受信したコマンド別の処理
	 **********************/
	
	private void processLoginResult() {
		initializeREListener();
		writeLoginLog();
		application.doRefresh();
		pref.getPage().setEnabled(false);
	}

	private void processLoginMemberChanged(List<CHUserState> userStates) {
		this.userStates = userStates;
		for (CHUserState aUserState : userStates) {
			if (chViewers.containsKey(aUserState.getUser())) {
				// ログイン状態に合わせて同期ボタン操作
				chViewers.get(aUserState.getUser()).setUserStates(userStates);
				chViewers.get(aUserState.getUser()).setEnabledForSyncButton(aUserState.isLogin());
			}
		}
	}

	private void processSourceChanged(CHSourceChanged response) {
		if (chViewers.containsKey(response.getUser())) {
			chViewers.get(response.getUser()).setText(response);
		}
	}

	private void processFileResponse(String user) {
		if (chViewers.containsKey(user)) {
			chViewers.get(user).doRefresh();
		}
	}

	private void processFilelistRequest(CHFilelistRequest request) {
		cliant.getProcessManager().doProcess(request);
	}
	
	private void processLogoutResult() {
		connectionKilled();
	}
	
	/**
	 * textとfileを送信する
	 */
	public void send() {
		String source = application.getFrame().getEditor().getViewer().getText();
		String currentFileName = application.getSourceManager().getCurrentFile().getName();
		Point point = application.getFrame().getEditor().getViewer()
				.getScroll().getViewport().getViewPosition();
		
		cliant.getProcessManager().sendText(source, currentFileName, point);
	}
	
	private void logout() {
		if (cliant.getConn().established()) {
			cliant.getConn().write(new CHLogoutRequest(user));
		}
		writeLogoutLog();
	}

	/*********
	 * 切断処理
	 *********/

	private void connectionKilled() {
		closeCHEditors();
		removeListeners();
		pref.getPage().setEnabled(true);
	}

	private void removeListeners() {
		application.getSourceManager().removePropertyChangeListener(
				rePropertyChangeListener);
		removeREKeyListner();
		removeREMenuListener();
		resetREWindowListener();
		removeREWindowFocusListener();
	}
	
	private void removeREKeyListner() {
		if (reKeyListener != null && application.getFrame().getEditor() != null) {
			application.getFrame().getEditor().getViewer().getTextPane()
					.removeKeyListener(reKeyListener);
		}
	}
	
	private void removeREMenuListener() {
		
		application.getFrame().getJMenuBar().getMenu(MENU_INDEX_EDIT).getItem(ITEM_INDEX_PASTE)
				.removeActionListener(pasteListener);	
		application.getFrame().getJMenuBar().getMenu(MENU_INDEX_FILE).getItem(ITEM_INDEX_SAVE)
				.removeActionListener(saveListener);
		application.getFrame().getJMenuBar().getMenu(MENU_INDEX_FILE).getItem(ITEM_INDEX_REFRESH)
				.removeActionListener(refreshListener);
		application.getFrame().getJMenuBar().getMenu(MENU_INDEX_FILE).getItem(ITEM_INDEX_EXIT)
				.removeActionListener(exitListener);
	}
	
	private void resetREWindowListener() {
		application.getFrame().removeWindowListener(reWindowListener);
		application.getFrame().addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				application.doExit();
			}
		});
	}
	
	private void removeREWindowFocusListener() {
		application.getFrame().removeWindowFocusListener(reWindowFocusListener);
	}

	private void closeCHEditors() {
		// TODO userState正しい？
		for (CHUserState userState : userStates) {
			if (chViewers.containsKey(userState.getUser())) {
				chViewers.get(userState.getUser()).getApplication().getFrame().setVisible(false);
				closeCHBlockEditor(chViewers.get(userState.getUser()).getApplication());
				chViewers.remove(userState.getUser());
			}
		}
	}
	
	/**
	 * Viewer用BlockEditorを閉じる
	 */
	private void closeCHBlockEditor(REApplication application) {
		application.getChBlockEditorController().close();
	}

	/**********
	 * 判定関係
	 **********/
	
	/**
	 * ペーストされたコードがviewerからのものかか調べる
	 * @param copyCode
	 * @return
	 */
	public boolean isCopiedCode(String copyCode) {
		return copyCode.equals(getClipbordString()) && !copyCode.equals("");
	}
	
	public boolean canStart() {
		if (!CHLib.isCorrectID(user)) {
			new CHErrorDialog(CHErrorDialog.ILLEGAL_ID).doOpen();
			return false;
		} else if (!CHLib.isCorrectPass(password)) {
			new CHErrorDialog(CHErrorDialog.ILLEGAL_PASS).doOpen();
			return false;
		} else if (!CHFileSystem.existSyncProject()) {
			new CHErrorDialog(CHErrorDialog.PROJECT_MISSING).doOpen();
			return false;
		}
		return true;
	}
	
	/********************
	 * getter and setter
	 ********************/
	
	/**
	 * 部分取込されたらそれに関する情報を返す
	 * @return 部分取込元のユーザ，部分取込されたコード，部分取込されたコードがあるファイル名
	 */
	public List<String> getPartialImportInfo() {
		// TODO 要テスト
		for (CHUserState aUserState : userStates) {
			if (chViewers.containsKey(aUserState.getUser())) {
				if (isCopiedCode(chViewers.get(aUserState.getUser()).getCopyCode())) {
					List<String> info = new ArrayList<String>();
					info.add(aUserState.getUser());
					info.add(getClipbordString());
					info.add(chViewers.get(aUserState.getUser()).getCopyFilePath());
					return info;
				}
			}
		}
		return null;
	}
	
	/**
	 * クリップボードに格納されている文字列を返す
	 * @return
	 */
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

	/******
	 * LOG
	 ******/
	
	public void writePresLog(PRCheCoProLog.SubType subType, String... message) {
		try {
			CPath path = null;
			if (application.getSourceManager().getCCurrentFile() != null) {
				path = application.getSourceManager().getCCurrentFile()
						.getRelativePath(application.getSourceManager().getCCurrentProject());
			}
			PRLog log = new PRCheCoProLog(subType, path, message);
			application.writePresLog(log, CHFileSystem.getSyncProjectDir());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 部分取込のログを書く
	 */
	public void writePartialImportLog() {
		List<String> info = getPartialImportInfo();
		if (info != null) {
			String user = info.get(0);
			String copyCode = info.get(1);
			String copyFilePath = info.get(2);
			writePresLog(PRCheCoProLog.SubType.PARTIAL_IMPORT, user, copyFilePath, copyCode);
		}
	}
	
	public void writeFocusGainedLog() {
		writePresLog(PRCheCoProLog.SubType.FOCUS_GAINED, user);
	}
	
	public void writeFocusLostLog() {
		writePresLog(PRCheCoProLog.SubType.FOCUS_LOST, user);
	}
	
	public void writeLoginLog() {
		writePresLog(PRCheCoProLog.SubType.LOGIN);
	}
	
	public void writeLogoutLog() {
		writePresLog(PRCheCoProLog.SubType.LOGOUT);
	}

	/****************
	 * preference関係
	 ****************/

	private static final String LOGINID_LABEL = "CheCoPro.loginid";
	private static final String PASSWORD_LABEL = "CheCoPro.password";
	private static final String PORTNUMBER_LABEL = "CheCoPro.portnumber";
	private static final String COLOR_LABEL = "CheCoPro.color";

	class CheCoProPreferenceCategory extends CAbstractPreferenceCategory {

		private static final long serialVersionUID = 1L;

		private JTextField nameField = new JTextField(15);
		private JPasswordField passField = new JPasswordField(15);
		private JComboBox<Integer> portBox = new JComboBox<Integer>();
		private JComboBox<String> colorBox = new JComboBox<String>();
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
			
			getRepository().put(LOGINID_LABEL, user);
			getRepository().put(PASSWORD_LABEL, password);
			getRepository().put(PORTNUMBER_LABEL,
					Integer.toString(portBox.getSelectedIndex()));
			getRepository().put(COLOR_LABEL,
					(String) colorBox.getSelectedItem());
		}

		class CheCoProPreferencePanel extends JPanel {

			private static final long serialVersionUID = 1L;

			public CheCoProPreferencePanel() {
				FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);

				JPanel namePanel = new JPanel(flowLayout);
				namePanel.setPreferredSize(new Dimension(300, 50));
				namePanel.add(new JLabel("ID : "));
				namePanel.add(nameField);
				namePanel.add(new JLabel("例 : 70511000"));

				JPanel passPanel = new JPanel(flowLayout);
				passPanel.setPreferredSize(new Dimension(300, 50));
				passPanel.add(new JLabel("Password : "));
				passPanel.add(passField);
				passPanel.add(new JLabel("4〜12文字"));

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
				
				this.add(namePanel, BorderLayout.CENTER);
				this.add(passPanel, BorderLayout.CENTER);
				this.add(portPanel, BorderLayout.CENTER);
				this.add(colorPanel, BorderLayout.CENTER);
			}
			
			public void setEnabled(boolean enabled) {
				nameField.setEnabled(enabled);
				passField.setEnabled(enabled);
				portBox.setEnabled(enabled);
				colorBox.setEnabled(enabled);
			}
		}

	}

}