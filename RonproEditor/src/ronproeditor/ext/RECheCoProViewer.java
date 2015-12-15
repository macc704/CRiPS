package ronproeditor.ext;

import java.awt.Component;
import java.awt.Point;
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
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import pres.core.model.PRCheCoProLog;
import pres.core.model.PRLog;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileFilter;
import clib.common.system.CJavaSystem;
import clib.preference.model.ICPreferenceCategory;
import ch.conn.framework.CHConnection;
import ch.conn.framework.CHFile;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHFilelistRequest;
import ch.conn.framework.packets.CHSourceChanged;
import ch.library.CHFileSystem;
import ch.util.CHBlockEditorController;
import ch.view.CHFileChooser;
import ronproeditor.REApplication;
import ronproeditor.RESourceManager;
import ronproeditor.ext.RECheCoProManager.CheCoProPreferenceCategory;

public class RECheCoProViewer {

	public static final String APP_NAME = "CheCoProViewer";
	public static final String CH_DIR_PATH = "MyProjects/.CH";
	public static final int MENU_INDEX_FILE = 0;
	public static final int MENU_INDEX_EDIT = 1;
	public static final int MENU_INDEX_TOOLS = 3;
	public static final int MENU_INDEX_SYNC = 5;
	public static final int ITEM_INDEX_CUT = 3;
	public static final int ITEM_INDEX_COPY = 4;
	
	private static int CTRL_MASK = InputEvent.CTRL_MASK;
	static {
		if (CJavaSystem.getInstance().isMac()) {
			CTRL_MASK = InputEvent.META_MASK;
		}
	}
	
	private REApplication application;
	private REApplication baseApplication;
	private CHConnection conn;
	private String user;
	private List<CHUserState> userStates = new ArrayList<CHUserState>();
	
	private boolean synchronizing;
	private String property = "";
	private String copyFilePath = "";
	private String copyCode = "";
	
	public RECheCoProViewer(String user, CHConnection conn) {
		this.user = user;
		this.conn = conn;
	}

	/***********
	 * 初期化関連
	 ***********/
	
	public void initialize() {
		initializePref();
		initializeFrame();
		initializeListeners();
		initializeMenuBer();
		setEnabledForMenuBar(!synchronizing);
	}
	
	private void initializePref() {
		for (ICPreferenceCategory aCategory : application.getPreferenceManager().getCategories()) {
			if (aCategory instanceof CheCoProPreferenceCategory) {
				((CheCoProPreferenceCategory) aCategory).getPage().setEnabled(false);
			}
		}
	}
	
	private void initializeFrame() {
		setTitile();
		removeWindowListeners();
		application.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private void setTitile() {
		String title = user + "-" + APP_NAME;
		if(property.equals(RESourceManager.DOCUMENT_OPENED)) {
			String currentFile = application.getSourceManager().getCCurrentFile().getNameByString();
			title = title + "-" + currentFile;
		}
		application.getFrame().setTitle(title);
	}
	
	// Viewerを閉じてもシステムが終了しないようにするため
	public void removeWindowListeners() {
		WindowListener[] listeners = application.getFrame().getWindowListeners();
		for (int i = 0; i <listeners.length ; i++) {
			application.getFrame().removeWindowListener(listeners[i]);
		}
	}
	
	// ログ用にコピーしたコード取得（メニューバー）
	private ActionListener copyListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			doneCopyAction();
		}
	};
	
	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			property = evt.getPropertyName();
			setEnabledForTextPane(!synchronizing);
			
			// エディタが開かれていたらKeyListener追加，閉じられたらリムーブ
			if(property.equals(RESourceManager.DOCUMENT_OPENED)) {
				reloadBlockEditor();
				addKeyListener();
			} else if (property.equals(RESourceManager.PREPARE_DOCUMENT_CLOSE)) {
				removeKeyListener();
			}
			setTitile();
		}
	};
	
	private WindowFocusListener windowFocuslistener = new WindowFocusListener() {
		
		@Override
		public void windowLostFocus(WindowEvent e) {
			writeFocusLostLog("JAVA");
		}
		
		@Override
		public void windowGainedFocus(WindowEvent e) {
			writeFocusGainedLog("JAVA");
		}
	};
	
	// ログ用にコピーしたコード取得（ショートカットキー）
	private KeyListener keyListener = new KeyAdapter() {
		int mod;
		
		@Override
		public void keyPressed(KeyEvent e) {
			mod = e.getModifiers();
			if (e.getKeyCode() == KeyEvent.VK_C) {
				if ((mod & CTRL_MASK) != 0) {
					doneCopyAction();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_X) {
				if ((mod & CTRL_MASK) != 0) {
					doneCopyAction();
				}
			}
		}
	};
	
	private void initializeListeners() {
		initializePropertyChangeListener();
		initializeWindowFocusListener();
	}
	
	private void initializePropertyChangeListener() {
		application.getSourceManager().addPropertyChangeListener(propertyChangeListener);
	}
	
	private void initializeWindowFocusListener() {
		application.getFrame().addWindowFocusListener(windowFocuslistener);
	}
	
	private void addKeyListener() {
		application.getFrame().getEditor().getViewer().getTextPane().addKeyListener(keyListener);
	}
	
	private void removeKeyListener() {
		application.getFrame().getEditor().getViewer().getTextPane().removeKeyListener(keyListener);
	}
	
	private void initializeMenuBer() {
		JMenuBar menuBar = application.getFrame().getJMenuBar();
		
		initializeFileMenu();
		initializeEditMenu();
		initializeToolsMenu();
		
		// BlockEditorの初期化
		getMenu(MENU_INDEX_TOOLS).add(initializeBlockEditorCommand());
		
		// 同期ボタンの初期化
		menuBar.add(initializeSyncButton());
		
		menuBar.add(initializePullButton());
		
		application.getFrame().setJMenuBar(menuBar);
	}
	
	/**
	 * Fileメニューの初化
	 */
	private void initializeFileMenu() {
		int itemCount = getMenu(MENU_INDEX_FILE).getItemCount() - 1;
		for ( ; itemCount >= 0; itemCount--) {
			if (itemCount != 8 && itemCount != 12) {
				getMenu(MENU_INDEX_FILE).remove(itemCount);
			}
		}
		getMenu(MENU_INDEX_FILE).insertSeparator(1);
	}
	
	/**
	 * Editメニューの初期化
	 */
	private void initializeEditMenu() {
		getMenu(MENU_INDEX_EDIT).getItem(ITEM_INDEX_CUT).addActionListener(copyListener);
		getMenu(MENU_INDEX_EDIT).getItem(ITEM_INDEX_COPY).addActionListener(copyListener);
	}
	
	/**
	 * Toolsメニューの初期化
	 */
	private void initializeToolsMenu() {
		int itemCount = getMenu(MENU_INDEX_TOOLS).getItemCount() - 1;
		for ( ; itemCount >= 0; itemCount--) {
			getMenu(MENU_INDEX_TOOLS).remove(itemCount);
		}
	}
	
	/**
	 * chViewer用BlockEditorActionの初期化
	 * @return actionOpenBlockEditor
	 */
	private Action initializeBlockEditorCommand() {
		application.setChBlockEditorController(new CHBlockEditorController(user));
		Action actionOpenBlockEditor = new AbstractAction() {
			
			private static final long serialVersionUID = 1L;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				File selectedFile = null;
				String langDefFilePath = CHBlockEditorController.DEFAULT_LANGDEF_PATH;
				if (property.equals(RESourceManager.DOCUMENT_OPENED)){
					selectedFile = application.getResourceRepository().getCCurrentFile().toJavaFile();
					langDefFilePath = application.getResourceRepository().getCCurrentProject()
							.getAbsolutePath().toString() + "/lang_def_project.xml";
				} 
				doOpenBlockEditorForCH(application.getChBlockEditorController(), selectedFile, langDefFilePath);
			}
		};
		
		actionOpenBlockEditor.putValue(Action.NAME, "Open BlockEditor");
		actionOpenBlockEditor.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL_MASK));
		actionOpenBlockEditor.setEnabled(true);
		
		return actionOpenBlockEditor;
	}

	/**
	 * 同期ボタンの初期化
	 * @return syncButton
	 */
	private JToggleButton initializeSyncButton() {
		String syncLabel = "同期中";
		String asyncLabel = "非同期中";
		JToggleButton syncButton = new JToggleButton(syncLabel, true);
		synchronizing = true;
		
		for (CHUserState aUserState : userStates) {
			if (user.equals(aUserState.getUser()) && !aUserState.isLogin()) {
				syncButton.doClick();
				syncButton.setEnabled(false);
				syncButton.setText(asyncLabel);
				synchronizing = false;
			}
		}
		
		syncButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (syncButton.isSelected()) {
					conn.write(new CHFilelistRequest(user));
					syncButton.setText(syncLabel);
					synchronizing = true;
					application.doRefresh();
				} else {
					syncButton.setText(asyncLabel);
					synchronizing = false;
				}
				setEnabledForTextPane(!synchronizing);
				setEnabledForMenuBar(!synchronizing);
				
			}
		});
		return syncButton;
	}
	
	/**
	 * 取り込みボタンの初期化
	 * @return pullButton
	 */
	private JButton initializePullButton() {
		JButton pullButton = new JButton("取り込み↓");
		pullButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CHFileChooser fileChooser = new CHFileChooser(CHFileSystem.getSyncProjectDir(), 
						CHFileSystem.getUserDirForClient(user));
				fileChooser.doOpen();
				if (fileChooser.getAcceptFilter() != null) {
					doPull(user, fileChooser.getAcceptFilter());
					baseApplication.doRefresh();
				}
			}

		});
		return pullButton;
	}
	
	/***************
	 * Viewer操作関連
	 ***************/
	
	public REApplication doOpenNewCH(REApplication application) {
		baseApplication = application;
		this.application = application.doOpenNewRE(CH_DIR_PATH + "/" + user);
		initialize();
		return this.application;
	}
	
	public void doRefresh() {
		application.doRefresh();
	}
	
	/**
	 * 受け取ったテキストをViewerに表示
	 * @param scPacket
	 */
	public void setText(CHSourceChanged scPacket) {
		String source = scPacket.getSource();
		String currentFileName = scPacket.getCurrentFileName();
		Point point = scPacket.getPoint();
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if (canSetText(currentFileName)) {
					application.getFrame().getEditor().setText(source);
					application.doSave();
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							// スクロールバー位置設定
							application.getFrame().getEditor().getViewer().getScroll()
							.getViewport().setViewPosition(point);
							setTitile();
						}
					});
				}
			}
		});
	}

	/********************
	 * BlockEditor操作関連
	 ********************/
	
	private void doOpenBlockEditorForCH(CHBlockEditorController bc,File selectedFile, String langDefFilePath) {
		String xmlFilePath = "";
		if (selectedFile != null) {
			xmlFilePath = bc.createXmlFromJava(selectedFile, REApplication.SRC_ENCODING,
					application.getLibraryManager().getLibsAsArray());
		}
		bc.openBlockEditor(langDefFilePath, xmlFilePath);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				addBlockEditorWindowListener(bc);
				addBlockEditorWindowForcusListener(bc);
				if (bc.getWorkspaceController().isOpened()) {
					setEnabledForSyncButton(false);
				}
			}
		});
	}
	
	private void reloadBlockEditor() {
		CHBlockEditorController bc = application.getChBlockEditorController();
		File selectedFile = null;
		String langDefFilePath = CHBlockEditorController.DEFAULT_LANGDEF_PATH;
		String xmlFilePath = "";
		
		if (bc.getWorkspaceController().isOpened()) {		
			selectedFile = application.getResourceRepository().getCCurrentFile().toJavaFile();
			langDefFilePath = application.getResourceRepository()
						.getCCurrentProject().getAbsolutePath().toString() + "/lang_def_project.xml";
			xmlFilePath = bc.createXmlFromJava(selectedFile, REApplication.SRC_ENCODING,
						application.getLibraryManager().getLibsAsArray());
			bc.reloadBlockEditor(langDefFilePath, xmlFilePath, property);
		}
	}
	
	private void addBlockEditorWindowListener(CHBlockEditorController bc) {
		bc.getBlockEditorFrame().addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				for (CHUserState aUserState : userStates) {
					if (aUserState.getUser().equals(user) && aUserState.isLogin()) {
						setEnabledForSyncButton(true);
					}
				}
			}
		});
	}
	
	private void addBlockEditorWindowForcusListener(CHBlockEditorController bc) {
		bc.getBlockEditorFrame().addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				writeFocusLostLog("BLOCK");
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				writeFocusGainedLog("BLOCK");
			}
		});
	}
	
	/*******************
	 * 取り込みボタンの関連
	 *******************/
	
	private void doPull(String user, CFileFilter filter) {
		CDirectory from = CHFileSystem.getUserDirForClient(user);
		CDirectory to = CHFileSystem.getSyncProjectDir();
		writeAllImportLog(CHFileSystem.pull(from, to, filter));
	}
	
	/**************
	 * setEnabled
	 **************/
	
	/**
	 * ViewerのTextPane操作の可否を設定
	 * @param enabled
	 */
	public void setEnabledForTextPane(boolean enabled) {
		if (property.equals(RESourceManager.DOCUMENT_OPENED)) {
			application.getFrame().getEditor().getViewer().getTextPane().setEditable(enabled);
		}
	}
	
	/**
	 * Viewerのメニューバー操作の可否を設定
	 * @param enabled
	 */
	public void setEnabledForMenuBar(boolean enabled) {
		for (int i = 0; i < MENU_INDEX_SYNC; i++) {
			application.getFrame().getJMenuBar().getMenu(i).setEnabled(enabled);
		}
		getPullButton().setEnabled(enabled);
	}
	
	/**
	 * Viewerの同期ボタン操作の可否を設定
	 * @param enabled
	 */
	public void setEnabledForSyncButton(boolean enabled) {
		if (!enabled && getSyngButton().getText().equals("同期中")) {
				getSyngButton().doClick();
		}
		getSyngButton().setEnabled(enabled);
	}
	
	/*********
	 * 判定関連
	 *********/
	
	/**
	 * 受け取ったテキストをViewerに表示できるか判定
	 * @param currentFileName
	 * @return 判定結果
	 */
	public boolean canSetText(String currentFileName) {
		if (property.equals(RESourceManager.DOCUMENT_CLOSED)) {
			return false;
		} else {
			return application.getSourceManager().getCurrentFile()
				.getName().equals(currentFileName) && synchronizing;
		}
	}
	
	/******
	 * LOG
	 ******/
	
	public void writePresLog(PRCheCoProLog.SubType subType, String... message) {
		try {
			PRLog log = new PRCheCoProLog(subType, null, message);
			baseApplication.writePresLog(log, CHFileSystem.getSyncProjectDir());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void writeAllImportLog(List<CHFile> files) {
		String[] paths = new String[files.size()];
		for (int i = 0; i < files.size(); i++) {
			paths[i] = files.get(i).getPath();
		}
		writePresLog(PRCheCoProLog.SubType.ALL_IMPORT, paths);
	}
	
	public void writeFocusGainedLog(String viewType) {
		writePresLog(PRCheCoProLog.SubType.FOCUS_GAINED, user, viewType);
	}
	
	public void writeFocusLostLog(String viewType) {
		writePresLog(PRCheCoProLog.SubType.FOCUS_LOST, user, viewType);
	}
	
	public void doneCopyAction() {
		copyCode = getSelectedText();
		copyFilePath = getCurrentFileRelativePaht();
	}
	
	/********************
	 * setter and getter
	 ********************/

	public REApplication getApplication() {
		return application;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public void setUserStates(List<CHUserState> userStates) {
		this.userStates = userStates;
	}
	
	public String getCopyCode() {
		String copyCode = this.copyCode;
		this.copyCode = "";
		return copyCode;
	}
	
	public String getCopyFilePath() {
		return copyFilePath;
	}
	
	public int getMenuCount() {
		return application.getFrame().getJMenuBar().getMenuCount();
	}
	
	public JMenu getMenu(int index) {
		return application.getFrame().getJMenuBar().getMenu(index);
	}
	
	public Component getComponent(int index) {
		return application.getFrame().getJMenuBar().getComponent(index);
	}
	
	public JToggleButton getSyngButton() {
		for (int i = 0; i <= getMenuCount(); i++) {
			if (getComponent(i) instanceof JToggleButton) {
				JToggleButton button = (JToggleButton) getComponent(i);
				if (button.getText().equals("同期中") || button.getText().equals("非同期中")) {
					return button;
				}
			}
		}
		return null;
	}
	
	public JButton getPullButton() {
		for (int i = 0; i < getMenuCount(); i++) {
			if (getComponent(i) instanceof JButton) {
				JButton button = (JButton) getComponent(i);
				return button;
			}
		}
		return null;
	}
	
	public String getSelectedText() {
		return application.getFrame().getEditor().getViewer().getTextPane().getSelectedText();
	}
	
	public String getCurrentFileRelativePaht() {
		return application.getSourceManager().getCCurrentFile()
				.getRelativePath(application.getSourceManager().getCRootDirectory()).toString();
	}
}
