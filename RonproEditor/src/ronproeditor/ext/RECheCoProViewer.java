package ronproeditor.ext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import ronproeditor.REApplication;

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
	
	public RECheCoProViewer(String user) {
		this.user = user;
	}

	public void init() {
		initCHFrame();
		initCHMenuBer();
	}
	
	private void initCHFrame() {
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
	
	private void initCHMenuBer() {
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
		
		application.getFrame().setJMenuBar(menuBar);
	}
	
	public REApplication doOpenNewCH(REApplication application) {
		this.application = application.doOpenNewRE(CH_DIR_PATH + "/" + user);
		init();
		return this.application;
	}

	public REApplication getApplication() {
		return application;
	}

	public void setApplication(REApplication application) {
		this.application = application;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	
}
