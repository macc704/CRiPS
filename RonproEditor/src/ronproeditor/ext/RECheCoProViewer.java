package ronproeditor.ext;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHSourceChanged;
import ronproeditor.REApplication;
import ronproeditor.RESourceManager;

public class RECheCoProViewer {

	public static final String APP_NAME = "CheCoProViewer";
	public static final String CH_DIR_PATH = "MyProjects/.CH";
	public static final int MENU_INDEX_FILE = 0;
	public static final int MENU_INDEX_EDIT = 1;
	public static final int MENU_INDEX_TOOLS = 3;
	public static final int ITEM_INDEX_CUT = 3;
	public static final int ITEM_INDEX_COPY = 4;
	
	private REApplication application;
	private String user;
	private List<CHUserState> userStates = new ArrayList<CHUserState>();
	private boolean synchronizing;
	private String property;
	
	public RECheCoProViewer(String user) {
		this.user = user;
	}

	public void initialize() {
		initializeFrame();
		initializeListeners();
		initializeMenuBer();
	}
	
	private void initializeFrame() {
		application.getFrame().setTitle(user + "-" + APP_NAME);
		// TODO CHEditor閉じたら論プロも閉じる不具合要修正
		application.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
		}
	};
	
	
	private void initializeListeners() {
		application.getSourceManager().addPropertyChangeListener(propertyChangeListener);
	}
	
	private void initializeMenuBer() {
		JMenuBar menuBar = application.getFrame().getJMenuBar();
		
		// Fileメニューの初期化
		int itemCount = menuBar.getMenu(MENU_INDEX_FILE).getItemCount() - 1;
		for ( ; itemCount >= 0; itemCount--) {
			if (itemCount != 8 && itemCount != 12) {
				menuBar.getMenu(MENU_INDEX_FILE).remove(itemCount);
			}
		}
		menuBar.getMenu(MENU_INDEX_FILE).insertSeparator(1);
		
		// Editメニューの初期化
		menuBar.getMenu(MENU_INDEX_EDIT).getItem(ITEM_INDEX_CUT).addActionListener(copyListener);
		menuBar.getMenu(MENU_INDEX_EDIT).getItem(ITEM_INDEX_COPY).addActionListener(copyListener);
		
		// Toolsメニューの初期化
		itemCount = menuBar.getMenu(MENU_INDEX_TOOLS).getItemCount() - 1;
		for ( ; itemCount >= 0; itemCount--) {
			menuBar.getMenu(MENU_INDEX_TOOLS).remove(itemCount);
		}
		// TODO BlockEditor
		
		menuBar.add(initializeSyncButton());
		application.getFrame().setJMenuBar(menuBar);
	}
	
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
				setEnabledForTextPane(synchronizing);
				setEnabledForMenuBar(!synchronizing);
				
			}
		});
		return syncButton;
	}
	
	public REApplication doOpenNewCH(REApplication application) {
		this.application = application.doOpenNewRE(CH_DIR_PATH + "/" + user);
		initialize();
		return this.application;
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
	
	
	public void setEnabledForTextPane(boolean enabled) {
		if (property.equals(RESourceManager.DOCUMENT_OPENED)) {
			application.getFrame().getEditor().getViewer().getTextPane().setEditable(enabled);
		}
	}
	
	public void setEnabledForMenuBar(boolean enabled) {
		int menuCount = application.getFrame().getJMenuBar().getComponentCount();
		for (int i = 0; i < menuCount; i++) {
			application.getFrame().getJMenuBar().getMenu(i).setEnabled(enabled);
		}
	}
	
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
	
}
