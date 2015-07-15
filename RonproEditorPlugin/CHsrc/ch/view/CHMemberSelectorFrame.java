package ch.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import ronproeditor.REApplication;
import ch.actions.CheCoProManager;
import ch.conn.framework.CHConnection;
import ch.conn.framework.CHUserLogWriter;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHFilelistRequest;
import ch.conn.framework.packets.CHLogoutRequest;
import ch.library.CHFileSystem;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFileSystem;

public class CHMemberSelectorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String CH_DIR_PATH = "workspace/.ch";
	private static final int SYNC_BUTTON_INDEX = 5;
	private static final int PULL_BUTTON_INDEX = 6;
	private static int CTRL_MASK = InputEvent.CTRL_MASK;

	private String user;
	private List<JButton> buttons = new ArrayList<JButton>();
	private CHConnection conn;
	private HashMap<String, REApplication> openedCHEditors = new HashMap<>();
	private HashMap<REApplication, String> openedUsers = new HashMap<>();
	private REApplication application = new REApplication();
	private List<CHUserState> userStates = new ArrayList<>();
	// private IWorkbenchWindow window;
	// private IWorkbenchPage page;
	private JToggleButton syncButton;
	private JButton pullButton;

	public CHMemberSelectorFrame(String user) {
		this.user = user;
	}

	public CHMemberSelectorFrame(String user, CHConnection conn) {
		this.user = user;
		this.conn = conn;
	}

	public void open() {
		this.setTitle("CheCoProMemberSelector " + user);
		this.setBounds(100, 100, 150, 500);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(msWindowListner);
		this.setVisible(true);
	}

	private WindowListener msWindowListner = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {
			conn.write(new CHLogoutRequest(user));
		}

		@Override
		public void windowClosed(WindowEvent e) {

		}
	};

	public void close() {
		for (CHUserState userState : userStates) {
			if (openedCHEditors.containsKey(userState.getUser())) {
				openedUsers.remove(openedCHEditors.get(userState.getUser()));
				openedCHEditors.get(userState.getUser()).getFrame().dispose();
				openedCHEditors.remove(userState.getUser());
			}
		}
		this.dispose();
	}

	public void setMembers(List<CHUserState> userStates) {

		this.getContentPane().removeAll();
		List<JPanel> panels = new ArrayList<JPanel>();
		JPanel basePanel = new JPanel();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		this.getContentPane().add(basePanel);

		for (final CHUserState aUserState : userStates) {

			JPanel panel = new JPanel();

			JButton button = new JButton(aUserState.getUser());
			button.setBackground(aUserState.getColor());

			JLabel lastLoginTime = new JLabel("online");
			lastLoginTime.setForeground(Color.GREEN);

			panel.setLayout(new BorderLayout());
			panel.add(button, BorderLayout.NORTH);
			panel.add(lastLoginTime, BorderLayout.CENTER);
			panels.add(panel);

			if (!aUserState.isLogin()) {
				button.setForeground(Color.RED);
				if (aUserState.getLastLogin() != null) {
					lastLoginTime.setText(formatter.format(aUserState
							.getLastLogin()));
				} else {
					lastLoginTime.setText("offline");
				}
				lastLoginTime.setForeground(Color.RED);
			}

			button.addActionListener(buttonAction);

			buttons.add(button);
		}

		for (JPanel aPanel : panels) {
			basePanel.add(aPanel);
		}

		this.getContentPane().validate();

	}

	private ActionListener buttonAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String pushed = e.getActionCommand();

			if (pushed.equals(user)) {
				// TODO eclipse active
			} else if (openedCHEditors.containsKey(pushed)) {
				openedCHEditors.get(pushed).getFrame().toFront();
			} else {
				conn.write(new CHFilelistRequest(pushed));

				// TODO デフォルトフォントおかしい（無理やり対処中）
				REApplication chApplication = application
						.doOpenNewRE(CH_DIR_PATH + "/" + pushed);
				openedCHEditors.put(pushed, chApplication);
				openedUsers.put(chApplication, pushed);
				initCHEditor(chApplication);
			}
		}
	};

	public void initCHEditor(REApplication application) {
		initCHEMenubar(application);
		initCHWindow(application);
		addCHPropertyChangeListners(application);
	}

	private void initCHEMenubar(final REApplication application) {

		JMenuBar menuBar = application.getFrame().getJMenuBar();

		// Fileメニューのsaveとrefresh以外削除
		for (int i = 14; i >= 0; i--) {
			if (i != 8 && i != 12) {
				menuBar.getMenu(0).remove(i);
			}
		}

		menuBar.getMenu(2).setEnabled(false);
		menuBar.getMenu(3).setEnabled(false);
		menuBar.add(initSyncButton(application));
		menuBar.add(initPullButton(application));
		application.getFrame().setJMenuBar(menuBar);
	}

	private JToggleButton initSyncButton(final REApplication application) {

		syncButton = new JToggleButton("同期中", true);
		for (CHUserState userState : userStates) {
			if (userState.getUser().equals(openedUsers.get(application))
					&& !userState.isLogin()) {
				syncButton.doClick();
				syncButton.setEnabled(false);
				syncButton.setText("非同期中");
			}
		}

		syncButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (syncButton.isSelected()) {
					conn.write(new CHFilelistRequest(openedUsers
							.get(application)));
					if (application.getFrame().getEditor() != null) {
						if (application.getFrame().getEditor().isDirty()) {
							application.doSave();
						}
					}
					// application.doRefresh();
					((JButton) application.getFrame().getJMenuBar()
							.getComponent(PULL_BUTTON_INDEX)).setEnabled(false);
					syncButton.setText("同期中");
				} else if (!syncButton.isSelected()) {
					((JButton) application.getFrame().getJMenuBar()
							.getComponent(PULL_BUTTON_INDEX)).setEnabled(true);
					syncButton.setText("非同期中");
				}

				if (application.getFrame().getEditor() != null) {
					application.getFrame().getEditor().getViewer()
							.getTextPane()
							.setEditable(!syncButton.isSelected());
				}

				if (application.getFrame().getJMenuBar()
						.getMenu(PULL_BUTTON_INDEX) != null) {
					pullButton.setEnabled(!syncButton.isSelected());
				}
			}
		});

		return syncButton;
	}

	public void userStateChanged() {
		for (CHUserState userState : userStates) {
			if (openedCHEditors.containsKey(userState.getUser())) {
				JToggleButton syncButton = (JToggleButton) openedCHEditors
						.get(userState.getUser()).getFrame().getJMenuBar()
						.getComponent(SYNC_BUTTON_INDEX);
				if (syncButton.isSelected() && !userState.isLogin()) {
					syncButton.doClick();
					syncButton.setEnabled(false);
				} else if (!syncButton.isSelected() && userState.isLogin()) {
					syncButton.setEnabled(true);
					syncButton.doClick();
				}
			}
		}
	}

	private JButton initPullButton(final REApplication application) {

		pullButton = new JButton("取り込み↓");
		pullButton.setEnabled(!syncButton.isSelected());
		pullButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CHPullDialog pullDialog = new CHPullDialog(openedUsers
						.get(application));
				pullDialog.open();

				boolean java = pullDialog.isJavaChecked();
				boolean material = pullDialog.isMaterialCecked();
				if (material) {
					doPull(openedUsers.get(application),
							makeCFileFilter(java, material));
					writePullLog(java, material, openedUsers.get(application));
				} else if (java && !material) {
					doPullForJava(openedUsers.get(application), CHFileSystem
							.getEclipseMemberDir(openedUsers.get(application)));
				}
			}
		});

		return pullButton;
	}

	private void writePullLog(boolean java, boolean material, String user) {
		if (java && material) {
			CheCoProManager.getLog().pull(user, CHUserLogWriter.PULL_ALL);
		} else if (java && !material) {
			CheCoProManager.getLog().pull(user, CHUserLogWriter.PULL_JAVA);
		} else if (!java && material) {
			CheCoProManager.getLog().pull(user, CHUserLogWriter.PULL_MATERIAL);
		}
	}

	private CFileFilter makeCFileFilter(boolean java, boolean material) {
		if (java && material) {
			return CFileFilter.IGNORE_BY_NAME_FILTER(".*", "*.class", "*.xml");
		} else if (java && !material) {
			// 再帰的に読み込んでいないから別途処理
			return CFileFilter.ACCEPT_BY_EXTENSION_FILTER("java");
		} else if (!java && material) {
			return CFileFilter.IGNORE_BY_NAME_FILTER(".*", "*.class", "*.xml",
					"*.java");
		}
		return null;
	}

	private void doPullForJava(String user, CDirectory dir) {
		List<CDirectory> children = dir.getDirectoryChildren(CFileFilter
				.IGNORE_BY_NAME_FILTER(".*"));
		if (!children.isEmpty()) {
			for (CDirectory childe : children) {
				doPullForJava(user, childe);
			}
		}
		String toPath = CHFileSystem.PROJECTPATH + "/"
				+ dir.getRelativePath(CHFileSystem.getEclipseMemberDir(user));
		CDirectory toDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory(toPath);
		CHFileSystem.pull(dir, toDir,
				CFileFilter.ACCEPT_BY_EXTENSION_FILTER("java"));
	}

	private void doPull(String user, CFileFilter filter) {
		CDirectory from = CHFileSystem.getEclipseMemberDir(user);
		CDirectory to = CHFileSystem.getEclipseProjectDir();
		CHFileSystem.pull(from, to, filter);
		// refreshPackageExplorer();
	}

	private void initCHWindow(final REApplication application) {

		application.getFrame().setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		List<WindowListener> listeners = new ArrayList<WindowListener>();
		listeners = Arrays.asList(application.getFrame().getWindowListeners());
		for (WindowListener aListener : listeners) {
			application.getFrame().removeWindowListener(aListener);
		}

		application.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (application.getFrame().getEditor() != null) {
					application.getFrame().getEditor().getViewer()
							.getTextPane().removeKeyListener(keyListner);
				}
				application.getSourceManager().removePropertyChangeListener(
						propertyChangeListner);
				String user = openedUsers.get(application);
				openedUsers.remove(application);
				openedCHEditors.remove(user);
				setMembers(userStates);
			}
		});
	}

	private PropertyChangeListener propertyChangeListner;

	private void addCHPropertyChangeListners(final REApplication application) {

		propertyChangeListner = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("prepareDocumentClose")) {
					application.getFrame().getEditor().getViewer()
							.getTextPane().removeKeyListener(keyListner);
				}
				if (evt.getPropertyName().equals("documentOpened")) {
					// フォント無理やり
					application.getFrame().getEditor().getViewer()
							.changeFont(new Font("Osaka-Mono", Font.PLAIN, 12));
					addCHKeyListner(application);
					application.getFrame().getEditor().getViewer()
							.getTextPane()
							.setEditable(!syncButton.isSelected());
				}
			}
		};

		application.getSourceManager().addPropertyChangeListener(
				propertyChangeListner);
	}

	private KeyListener keyListner;

	private void addCHKeyListner(final REApplication application) {

		keyListner = new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				int mod = e.getModifiers();
				if (e.getKeyCode() == KeyEvent.VK_C
						|| e.getKeyCode() == KeyEvent.VK_X) {
					if ((mod & CTRL_MASK) != 0) {
						CheCoProManager.getLog().copy(
								application.getSourceManager()
										.getCCurrentFile(),
								application.getFrame().getEditor().getViewer()
										.getTextPane().getSelectedText());
					}
				}
			}
		};

		application.getFrame().getEditor().getViewer().getTextPane()
				.addKeyListener(keyListner);
	}

	public boolean cheackCHEditor(String sender, String currentFileName) {

		// senderのCHEditorが開かれていない
		if (!openedCHEditors.containsKey(sender)) {
			return false;
		} else {
			JToggleButton syncButton = (JToggleButton) openedCHEditors
					.get(sender).getFrame().getJMenuBar()
					.getComponent(SYNC_BUTTON_INDEX);
			// 非同期中
			if (!syncButton.isSelected()) {
				return false;
			}
		}

		// editorが開いていない
		if (openedCHEditors.get(sender).getFrame().getEditor() == null) {
			return false;
		}

		// 開いているファイル名が一致していない
		if (!openedCHEditors.get(sender).getSourceManager().getCurrentFile()
				.getName().equals(currentFileName)) {
			return false;
		}
		return true;
	}

	public void showSource(final String sender, final String source,
			final int topPixel) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				openedCHEditors.get(sender).getFrame().getEditor()
						.setText(source);

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						// System.out.println(topPixel);
						openedCHEditors.get(sender).getFrame().getEditor()
								.getViewer().getScroll().getVerticalScrollBar()
								.setValue(topPixel);
						// System.out.println(openedCHEditors.get(sender)
						// .getFrame().getEditor().getViewer().getScroll()
						// .getVerticalScrollBar().getValue());
						openedCHEditors.get(sender).doSave();
					}
				});
			}

		});

	}

	// public void refreshPackageExplorer() {
	// new Thread(new PackageExplorerUpdater()).start();
	// }

	// TODO リフレッシュ途中
	// class PackageExplorerUpdater implements Runnable {
	//
	// @Override
	// public void run() {
	// window.getWorkbench().getDisplay().asyncExec(new Runnable() {
	//
	// @Override
	// public void run() {
	// IPackagesViewPart packagesView;
	// TreeItem[] treeItems;
	// try {
	// packagesView = ((IPackagesViewPart) page
	// .showView(JavaUI.ID_PACKAGES));
	// treeItems = packagesView.getTreeViewer().getTree()
	// .getItems();
	// for (TreeItem treeItem : treeItems) {
	// if (treeItem.getText().equals("final")) {
	// packagesView.getTreeViewer().getTree()
	// .setSelection(treeItem);
	// break;
	// }
	// }
	// } catch (PartInitException e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }
	// }

	public List<JButton> getButtons() {
		return buttons;
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

	public HashMap<String, REApplication> getOpenedCHEditors() {
		return openedCHEditors;
	}

	// public void setWindow(IWorkbenchWindow window) {
	// this.window = window;
	// }

	// public void setPage(IWorkbenchPage page) {
	// this.page = page;
	// }

	public static void main(String[] args) {
		CHMemberSelectorFrame frame = new CHMemberSelectorFrame("name");
		List<CHUserState> userStates = new ArrayList<CHUserState>();
		// userStates.add(new CHUserState("user1", true, Color.CYAN));
		// userStates.add(new CHUserState("name", true, Color.LIGHT_GRAY));
		// userStates.add(new CHUserState("user2", false, Color.MAGENTA));
		frame.open();
		frame.setMembers(userStates);
		// userStates.add(new CHUserState("user3", true, Color.YELLOW));
		frame.setMembers(userStates);

	}
}
