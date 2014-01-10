package ronproeditor.ext;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ronproeditor.REApplication;
import ronproeditor.views.REFrame;
import ch.conn.framework.CHConnection;
import ch.conn.framework.CHFile;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHFileRequest;
import ch.conn.framework.packets.CHFileResponse;
import ch.conn.framework.packets.CHFilelistRequest;
import ch.conn.framework.packets.CHFilelistResponse;
import ch.conn.framework.packets.CHLoginMemberChanged;
import ch.conn.framework.packets.CHLoginRequest;
import ch.conn.framework.packets.CHLoginResult;
import ch.conn.framework.packets.CHLogoutRequest;
import ch.conn.framework.packets.CHLogoutResponse;
import ch.conn.framework.packets.CHSourcesendResponse;
import ch.view.CHMemberSelectorFrame;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.filesystem.sync.CFileList;
import clib.common.filesystem.sync.CFileListDifference;
import clib.common.filesystem.sync.CFileListUtils;
import clib.preference.model.CAbstractPreferenceCategory;

public class RECheCoProManager {

	public static final String APP_NAME = "CheCoPro";
	public static final String DEFAULT_NAME = "guest";
	public static final int DEFAULT_PORT = 10000;
	public static final String IP = "localhost";

	private REApplication application;
	private CHConnection conn;
	private CHMemberSelectorFrame msFrame;
	private List<String> members = new ArrayList<String>();
	private String user = DEFAULT_NAME;
	private int port = DEFAULT_PORT;
	private HashMap<String, REApplication> chFrameMap = new HashMap<String, REApplication>();
	private JToggleButton connButton = new JToggleButton("同期中", true);

	public static void main(String[] args) {
		new RECheCoProManager();
	}

	public RECheCoProManager(REApplication application) {
		this.application = application;
		initialize();
	}

	public RECheCoProManager() {
		connectServer();
	}

	private void initialize() {
		application.getPreferenceManager().putCategory(
				new CheCoProPreferenceCategory());
	}

	/*******************
	 * フレーム・リスナ関係
	 *******************/

	private void initializeREListener() {
		// final RESourceViewer viewer;
		// viewer = application.getFrame().getEditor().getViewer();
		// viewer.getTextPane().addKeyListener(new KeyAdapter() {
		// @Override
		// public void keyReleased(KeyEvent e) {
		//
		// conn.write(new CHSourceChanged(user, viewer.getText(),
		// application.getSourceManager().getCurrentFile()
		// .getName()));
		//
		// }
		// });
	}

	private void setMemberSelectorListner() {
		List<JButton> buttons = new ArrayList<JButton>(msFrame.getButtons());
		for (JButton aButton : buttons) {
			aButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String name = e.getActionCommand();
					msFrame.setDisable(name);
					msFrame.setMembers(members);
					setMemberSelectorListner();

					conn.write(new CHFilelistRequest(name));

					if (application != null) {
						doOpenNewCH(name);
					}
				}
			});
		}

		msFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				conn.write(new CHLogoutRequest(user));
			}
		});
	}

	public void doOpenNewCH(String user) {
		REApplication chApplication = application.doOpenNewRE("MyProjects/.CH/"
				+ user);
		initializeCHEditor(chApplication, user);
		chFrameMap.put(user, chApplication);
	}

	private void initializeCHEditor(REApplication chApplication, String user) {
		chApplication.getFrame().setTitle(APP_NAME + " Editor");
		chApplication.getFrame().setDefaultCloseOperation(
				JFrame.DISPOSE_ON_CLOSE);
		initializeCHListeners(chApplication, user);
		initializeCHMenu(chApplication);
	}

	private void initializeCHMenu(REApplication chApplication) {
		JMenuBar menuBar = chApplication.getFrame().getJMenuBar();
		menuBar.getMenu(3).remove(4);

		menuBar.add(connButton);
		chApplication.getFrame().setJMenuBar(menuBar);
	}

	private void initializeCHListeners(final REApplication chApplication,
			final String user) {

		removeListeners(chApplication.getFrame());

		chApplication.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				chFrameMap.remove(user);
				msFrame.setEnable(user);
				msFrame.setMembers(members);
				setMemberSelectorListner();
			}
		});

		chApplication.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						setCHTitleBar(chApplication);
					}
				});

		connButton.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (connButton.isSelected()) {
					connButton.setText("同期中");
				} else {
					connButton.setText("非同期");
				}
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

	private void setCHTitleBar(REApplication chApplication) {
		String title = APP_NAME + " Editor";
		if (chApplication.getFrame().getEditor() != null) {
			String currentFileName = chApplication.getSourceManager()
					.getCurrentFile().getName();
			title = title + "-" + currentFileName;
		}
		chApplication.getFrame().setTitle(title);
	}

	/********************
	 * クライアントメイン動作
	 ********************/

	public void startCheCoPro() {

		initializeREListener();

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
			msFrame = new CHMemberSelectorFrame(user);
			msFrame.open();
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
		conn.write(new CHLoginRequest(user, "xxxx"));
		return conn.established();
	}

	private void readFromServer() {
		Object obj = conn.read();

		if (obj instanceof CHLoginResult) {
			processLoginResult((CHLoginResult) obj);
		} else if (obj instanceof CHLoginMemberChanged) {
			processLoginMemberChanged((CHLoginMemberChanged) obj);
		} else if (obj instanceof CHSourcesendResponse) {
			processSourcesendResponse((CHSourcesendResponse) obj);
		} else if (obj instanceof CHLogoutResponse) {
			processLogoutResult((CHLogoutResponse) obj);
		} else if (obj instanceof CHFileRequest) {
			processFileRequest((CHFileRequest) obj);
		} else if (obj instanceof CHFileResponse) {
			processFileResponse((CHFileResponse) obj);
		} else if (obj instanceof CHFilelistRequest) {
			processFilelistRequest((CHFilelistRequest) obj);
		} else if (obj instanceof CHFilelistResponse) {
			processFilelistResponse((CHFilelistResponse) obj);
		}
	}

	/**********************
	 * 受信したコマンド別の処理
	 **********************/

	private void processLoginResult(CHLoginResult result) {
		if (result.isResult() == false) {
			conn.close();
		}
	}

	private void processLoginMemberChanged(CHLoginMemberChanged result) {

		for (CHUserState aUserState : result.getUserStates()) {
			if (!members.contains(aUserState.getUser())) {
				members.add(aUserState.getUser());
			}
		}

		msFrame.setMembers(members);
		setMemberSelectorListner();

	}

	private void processSourcesendResponse(CHSourcesendResponse response) {
		final String sender = response.getUser();
		final String source = response.getSource();
		final String senderCurrentFile = response.getCurrentFileName();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (shouldPrintSource(sender, senderCurrentFile)) {
					chFrameMap.get(sender).getFrame().getEditor()
							.setText(source);
				}
			}
		});
	}

	private void processLogoutResult(CHLogoutResponse result) {
		if (!user.equals(result.getUser())) {
			msFrame.addLoginedMember(result.getUser());
			msFrame.setMembers(members);
			setMemberSelectorListner();
		} else {
			for (String aMember : members) {
				REApplication chApplication = chFrameMap.get(aMember);
				if (chApplication != null) {
					chApplication.getFrame().setVisible(false);
				}
			}
			conn.close();
		}
	}

	private void processFileRequest(CHFileRequest request) {
		CDirectory finalProjectDir = getFinalProjectDir();

		List<CHFile> files = new ArrayList<CHFile>();
		for (String path : request.getRequestFilePaths()) {
			CFile file = finalProjectDir.findFile(path);
			byte[] byteArray = file.loadAsByte();
			files.add(new CHFile(path, byteArray));
		}

		conn.write(new CHFileResponse(user, files));
	}

	private void processFileResponse(CHFileResponse response) {
		String user = response.getUser();
		CDirectory cDir = getUserDir(user);

		for (CHFile aFile : response.getFiles()) {
			CFile file = cDir.findOrCreateFile(aFile.getPath());
			file.saveAsByte(aFile.getBytes());
		}
	}

	private void processFilelistResponse(CHFilelistResponse response) {
		String user = response.getUser();

		CFileList fileListServer = response.getFileList();
		CDirectory cDir = getUserDir(user);
		CFileList fileListClient = new CFileList(cDir);

		List<CFileListDifference> differences = CFileListUtils.compare(
				fileListServer, fileListClient);

		List<String> requestFilePaths = new ArrayList<String>();
		for (CFileListDifference aDifference : differences) {
			switch (aDifference.getKind()) {
			case CREATED:
			case UPDATED:
				requestFilePaths.add(aDifference.getPath());
				break;
			case REMOVED:
				cDir.findChild(new CPath(aDifference.getPath())).delete();
				break;
			default:
				throw new RuntimeException();
			}
		}

		conn.write(new CHFileRequest(user, requestFilePaths));
	}

	private void processFilelistRequest(CHFilelistRequest request) {

		CDirectory finalProjectDir = getFinalProjectDir();
		CFileList fileList = new CFileList(finalProjectDir);

		conn.write(new CHFilelistResponse(user, fileList));

	}

	/****************
	 * ファイル操作関係
	 ****************/

	private CDirectory getFinalProjectDir() {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				"MyProjects/final");
	}

	private CDirectory getUserDir(String user) {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				"MyProjects/.CH/" + user + "/final");
	}

	/**********
	 * 判定関係
	 **********/

	public boolean shouldPrintSource(String sender, String senderCurrentFile) {
		if (!connButton.isSelected()) {
			return false;
		}
		if (!chFrameMap.containsKey(sender)) {
			return false;
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

	/****************
	 * preference関係
	 ****************/

	private static final String LOGINID_LABEL = "CheCoPro.loginid";
	private static final String PORTNUMBER_LABEL = "CheCoPro.portnumber";

	class CheCoProPreferenceCategory extends CAbstractPreferenceCategory {

		private static final long serialVersionUID = 1L;

		private JTextField nameField = new JTextField(15);
		private JComboBox<Integer> portBox = new JComboBox<Integer>();
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
			if (getRepository().exists(PORTNUMBER_LABEL)) {
				portBox.setSelectedIndex(Integer.parseInt(getRepository().get(
						PORTNUMBER_LABEL)));
				port = Integer.parseInt(getRepository().get(PORTNUMBER_LABEL));
			}
			port += 20000;
		}

		@Override
		public void save() {
			user = nameField.getText();
			getRepository().put(LOGINID_LABEL, user);
			getRepository().put(PORTNUMBER_LABEL,
					Integer.toString(portBox.getSelectedIndex()));
		}

		class CheCoProPreferencePanel extends JPanel {

			private static final long serialVersionUID = 1L;

			public CheCoProPreferencePanel() {
				FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);
				JPanel namePanel = new JPanel(flowLayout);
				namePanel.add(new JLabel("Name : "));
				namePanel.add(nameField);
				JPanel portPanel = new JPanel(flowLayout);
				portPanel.add(new JLabel("Group : "));
				for (int i = 0; i < 51; i++) {
					portBox.addItem(i);
				}
				portPanel.add(portBox);
				this.add(namePanel, BorderLayout.CENTER);
				this.add(portPanel, BorderLayout.CENTER);
			}
		}

	}

}