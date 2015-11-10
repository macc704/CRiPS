package ronproeditor.ext;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileFilter;
import clib.common.system.CJavaSystem;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHSourceChanged;
import ch.library.CHFileSystem;
import ch.util.CHBlockEditorController;
import ch.view.CHPullDialog;
import ronproeditor.REApplication;
import ronproeditor.RESourceManager;

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
	private String user;
	private List<CHUserState> userStates = new ArrayList<CHUserState>();
	
	private boolean synchronizing;
	private String property = "";
	
	public RECheCoProViewer(String user) {
		this.user = user;
	}

	/***********
	 * 初期化関連
	 ***********/
	
	public void initialize() {
		initializeFrame();
		initializeListeners();
		initializeMenuBer();
		setEnabledForMenuBar(!synchronizing);
	}
	
	private void initializeFrame() {
		application.getFrame().setTitle(user + "-" + APP_NAME);
		removeWindowListeners();
		application.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public void removeWindowListeners() {
		WindowListener[] listeners = application.getFrame().getWindowListeners();
		for (int i = 0; i <listeners.length ; i++) {
			application.getFrame().removeWindowListener(listeners[i]);
		}
	}
	
	private ActionListener copyListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Copyのログを出力
		}
	};
	
	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			property = evt.getPropertyName();
			setEnabledForTextPane(!synchronizing);
			reloadBlockEditor();
		}
	};
	
	
	private void initializeListeners() {
		application.getSourceManager().addPropertyChangeListener(propertyChangeListener);
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
				String langDefFilePath = "";
				if (application.getChBlockEditorController().isFileOpened()){
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
					// TODO ファイルリストリクエスト
					syncButton.setText(syncLabel);
					synchronizing = true;
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
				CHPullDialog pullDialog = new CHPullDialog(user, 0);
				boolean java = pullDialog.isJavaChecked();
				boolean material = pullDialog.isMaterialCecked();
				if (java || material) {
					doPull(user, CHFileSystem.createCFileFilterForPull(java, material));
				}
				application.doRefresh();
			}

		});
		return pullButton;
	}
	
	/***************
	 * Viewer操作関連
	 ***************/
	
	public REApplication doOpenNewCH(REApplication application) {
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
					application.getFrame().setTitle(user + "-" + APP_NAME);
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							// スクロールバー位置設定
							application.getFrame().getEditor().getViewer().getScroll()
							.getViewport().setViewPosition(point);
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
	}
	
	private void reloadBlockEditor() {
		CHBlockEditorController bc = application.getChBlockEditorController();
		File selectedFile = null;
		String langDefFilePath = "";
		String xmlFilePath = "";
		
		if (property.equals(RESourceManager.DOCUMENT_OPENED)) {
			application.getChBlockEditorController().setFileOpened(true);		
			selectedFile = application.getResourceRepository().getCCurrentFile().toJavaFile();
			langDefFilePath = application.getResourceRepository()
					.getCCurrentProject().getAbsolutePath().toString() + "/lang_def_project.xml";
			xmlFilePath = bc.createXmlFromJava(selectedFile, REApplication.SRC_ENCODING,
					application.getLibraryManager().getLibsAsArray());
		} else {
			application.getChBlockEditorController().setFileOpened(false);
		}
		bc.reloadBlockEditor(langDefFilePath, xmlFilePath);
	}
	
	/*******************
	 * 取り込みボタンの関連
	 *******************/
	
	private void doPull(String user, CFileFilter filter) {
		CDirectory from = CHFileSystem.getUserDirForClient(user);
		CDirectory to = CHFileSystem.getFinalProjectDir();
		CHFileSystem.pull(from, to, filter);
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
		for (int i = 0; i < getMenuCount(); i++) {
			if (i != MENU_INDEX_SYNC){
				application.getFrame().getJMenuBar().getMenu(i).setEnabled(enabled);
			}
		}
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
	
}
