package ronproeditor.ext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ronproeditor.REApplication;
import ronproeditor.views.REFrame;
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
import ch.conn.framework.packets.CHLogoutResponse;
import ch.conn.framework.packets.CHSourceChanged;
import ch.library.CHFileSystem;
import ch.view.CHEntryDialog;
import ch.view.CHMemberSelectorFrame;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.sync.CFileList;
import clib.preference.model.CAbstractPreferenceCategory;

public class RECheCoProManager {

	public static final String APP_NAME = "CheCoPro";
	public static final String DEFAULT_NAME = "guest";
	public static final String DEFAULT_PASSWAOD = "xxx";
	public static final Color DEFAULT_COLOR = Color.WHITE;
	public static final int DEFAULT_PORT = 10000;
	public static final String IP = "localhost";

	private REApplication application;
	private CHConnection conn;
	private CHMemberSelectorFrame msFrame;
	private List<CHUserState> userStates = new ArrayList<CHUserState>();
	private List<String> members = new ArrayList<String>();
	private String user = DEFAULT_NAME;
	private String password = DEFAULT_PASSWAOD;
	private int port = DEFAULT_PORT;
	private Color color = DEFAULT_COLOR;
	private HashMap<String, REApplication> chFrameMap = new HashMap<String, REApplication>();
	private JToggleButton connButton = new JToggleButton("ìØä˙íÜ", true);
	private CHUserLogWriter logWriter;

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
	 * ÉtÉåÅ[ÉÄÅEÉäÉXÉiä÷åW
	 *******************/

	private void initializeREListener() {

		application.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (application.getFrame().getEditor() != null) {
							List<KeyListener> keyListeners = Arrays
									.asList(application.getFrame().getEditor()
											.getViewer().getTextPane()
											.getKeyListeners());

							for (KeyListener aKeyListener : keyListeners) {
								application.getFrame().getEditor().getViewer()
										.getTextPane()
										.removeKeyListener(aKeyListener);
							}

							application.getFrame().getEditor().getViewer()
									.getTextPane()
									.addKeyListener(new KeyAdapter() {

										@Override
										public void keyReleased(KeyEvent e) {
											conn.write(new CHSourceChanged(
													user, application
															.getFrame()
															.getEditor()
															.getViewer()
															.getText(),
													application
															.getSourceManager()
															.getCurrentFile()
															.getName()));
										}
									});
						}
					}
				});

		JMenuBar menubar = application.getFrame().getJMenuBar();
		JButton fileSendButton = new JButton("Save to server");
		menubar.add(fileSendButton);
		application.getFrame().setJMenuBar(menubar);
		fileSendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				processFilelistRequest(new CHFilelistRequest(user));
			}
		});
	}

	private void setMemberSelectorListner() {
		List<JButton> buttons = new ArrayList<JButton>(msFrame.getButtons());
		for (JButton aButton : buttons) {
			aButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String name = e.getActionCommand();
					msFrame.setDisable(name);
					msFrame.setMembers(userStates);
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
				conn.close();
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
		initializeCHMenu(chApplication, user);
	}

	private void initializeCHMenu(final REApplication chApplication,
			final String user) {
		JMenuBar menuBar = chApplication.getFrame().getJMenuBar();
		menuBar.getMenu(3).remove(4);

		for (int i = 0; i < menuBar.getMenuCount(); i++) {
			menuBar.getMenu(i).setBackground(getUserColor(user));
		}

		menuBar.add(connButton);

		JButton fileRequestButton = new JButton("File request");
		fileRequestButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				conn.write(new CHFilelistRequest(user));
				chApplication.doRefresh();
			}
		});

		JButton copyFileButton = new JButton("Save to MyProjects");
		copyFileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				logWriter.writeCommand(CHUserLogWriter.COPY_FILE);
				logWriter.writeFrom(user);
				logWriter.addRowToTable();
				copyUserDirToMyProjects(user);
			}
		});

		menuBar.add(fileRequestButton);
		menuBar.add(copyFileButton);

		menuBar.setBackground(getUserColor(user));

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
				msFrame.setMembers(userStates);
				setMemberSelectorListner();
			}
		});

		chApplication.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						setCHTitleBar(chApplication);
						// if (chApplication.getFrame().getEditor() != null) {
						// chApplication.getFrame().getEditor().getViewer()
						// .getTextPane()
						// .setBackground(getUserColor(user));
						//
						// }
					}
				});

		connButton.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (connButton.isSelected()) {
					connButton.setText("ìØä˙íÜ");
				} else {
					connButton.setText("îÒìØä˙");
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

	private Color getUserColor(String user) {
		for (CHUserState aUserState : userStates) {
			if (user.equals(aUserState.getUser())) {
				return aUserState.getColor();
			}
		}
		return DEFAULT_COLOR;
	}

	private void copyUserDirToMyProjects(String user) {
		CDirectory masterDir = CHFileSystem.getUserDirForClient(user);
		CDirectory copyDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory("MyProjects/" + user);
		List<String> requestFilePaths = CHFileSystem.getRequestFilePaths(
				new CFileList(masterDir), copyDir);

		List<CHFile> files = CHFileSystem.getCHFiles(requestFilePaths,
				masterDir);
		CHFileSystem.saveFiles(files, copyDir);
		application.doRefresh();
	}

	/********************
	 * ÉNÉâÉCÉAÉìÉgÉÅÉCÉììÆçÏ
	 ********************/

	public void startCheCoPro() {

		logWriter = new CHUserLogWriter();
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
		connectionKilled();
		logWriter.saveTableToFile();
		System.out.println("client closed");

	}

	private boolean login() {
		conn.write(new CHLoginRequest(user, password, color));
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
		} else if (obj instanceof CHSourceChanged) {
			processSourceChanged((CHSourceChanged) obj);
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
		} else if (obj instanceof CHFilesizeNotice) {
			processFilesizeNotice((CHFilesizeNotice) obj);
		}
	}

	/**********************
	 * éÛêMÇµÇΩÉRÉ}ÉìÉhï ÇÃèàóù
	 **********************/

	private void processLoginResult(CHLoginResult result) {
		if (result.isResult() == CHLoginCheck.FAILURE) {
			conn.close();
		} else if (result.isResult() == CHLoginCheck.NEW_ENTRY) {
			CHEntryDialog entryDialog = new CHEntryDialog();
			entryDialog.open();
			user = entryDialog.getUser();
			password = entryDialog.getPassword();
			conn.write(new CHEntryRequest(user, password));
		} else if (result.isResult() == CHLoginCheck.SUCCESS) {
			logWriter.writeCommand(CHUserLogWriter.LOGIN);
			logWriter.addRowToTable();
			msFrame = new CHMemberSelectorFrame(user);
			msFrame.open();
		}
	}

	private void processEntryResult(CHEntryResult result) {
		if (result.isResult()) {
			// ìoò^ê¨å˜
			conn.write(new CHLoginRequest(user, password, color));
		} else {
			// ìoò^é∏îs
			System.out.println("Entry failed");
			conn.close();
		}
	}

	private void processLoginMemberChanged(CHLoginMemberChanged result) {

		// for (CHUserState aUserState : result.getUserStates()) {
		// if (members.contains(aUserState)) {
		// members.add(aUserState.getUser());
		// }
		// }

		userStates = result.getUserStates();
		msFrame.setMembers(result.getUserStates());
		setMemberSelectorListner();

	}

	private void processSourceChanged(CHSourceChanged response) {
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
			msFrame.setMembers(userStates);
			setMemberSelectorListner();
		} else {
			for (String aMember : members) {
				REApplication chApplication = chFrameMap.get(aMember);
				if (chApplication != null) {
					chApplication.getFrame().setVisible(false);
				}
			}
			logWriter.saveTableToFile();
			conn.close();
		}
	}

	private void processFileRequest(CHFileRequest request) {
		logWriter.writeCommand(CHUserLogWriter.SEND_FILE);
		logWriter.writeFrom(user);
		logWriter.addRowToTable();
		List<CHFile> files = CHFileSystem.getCHFiles(
				request.getRequestFilePaths(),
				CHFileSystem.getFinalProjectDir());
		conn.write(new CHFileResponse(user, files));
	}

	private void processFileResponse(CHFileResponse response) {
		logWriter.writeCommand(CHUserLogWriter.RECIVE_FILE);
		logWriter.writeFrom(response.getUser());
		logWriter.addRowToTable();
		CHFileSystem.saveFiles(response.getFiles(),
				CHFileSystem.getUserDirForClient(response.getUser()));
	}

	private void processFilelistResponse(CHFilelistResponse response) {
		String user = response.getUser();
		CDirectory copyDir = CHFileSystem.getUserDirForClient(user);

		List<String> requestFilePaths = CHFileSystem.getRequestFilePaths(
				response.getFileList(), copyDir);

		conn.write(new CHFileRequest(user, requestFilePaths));
	}

	private void processFilelistRequest(CHFilelistRequest request) {
		CFileList fileList = CHFileSystem.getFinalProjectFileList();
		conn.write(new CHFilelistResponse(user, fileList));
	}

	private void processFilesizeNotice(CHFilesizeNotice notice) {
		int fileSize = notice.getFileSize();
		System.out.println("size ; " + fileSize);
	}

	/*********
	 * êÿífèàóù
	 *********/

	private void connectionKilled() {
		resetMenubar();
	}

	private void resetMenubar() {
		JMenuBar menubar = application.getFrame().getJMenuBar();
		menubar.remove(5);
		application.getFrame().setJMenuBar(menubar);
	}

	/**********
	 * îªíËä÷åW
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
	 * preferenceä÷åW
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
			if (str.equals("cyan")) {
				color = Color.CYAN;
			} else if (str.equals("lightgray")) {
				color = Color.LIGHT_GRAY;
			} else if (str.equals("magenta")) {
				color = Color.MAGENTA;
			} else if (str.equals("yellow")) {
				color = Color.YELLOW;
			}
		}

		@Override
		public void save() {
			user = nameField.getText();
			password = String.valueOf(passField.getPassword());
			changeStringToColor((String) colorBox.getSelectedItem());
			getRepository().put(LOGINID_LABEL, user);
			getRepository().put(PASSWORD_LABEL,
					String.valueOf(passField.getPassword()));
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
				colorBox.addItem("cyan");
				colorBox.addItem("lightgray");
				colorBox.addItem("magenta");
				colorBox.addItem("yellow");
				colorPanel.add(colorBox);

				this.add(namePanel, BorderLayout.CENTER);
				this.add(passPanel, BorderLayout.CENTER);
				this.add(portPanel, BorderLayout.CENTER);
				this.add(colorPanel, BorderLayout.CENTER);
			}
		}

	}

}