package ronproeditor.ext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ronproeditor.REApplication;
import ronproeditor.views.RESourceViewer;
import ch.connection.CHConnection;
import ch.connection.CHPacket;
import ch.view.CHMemberSelectorFrame;
import clib.preference.model.CAbstractPreferenceCategory;

public class RECheCoProManager {

	public static final String APP_NAME = "CheCoPro";
	public static final String DEFAULT_NAME = "guest";
	public static final int DEFAULT_PORT = 10000;
	public static final String IP = "localhost";

	private REApplication application;
	private CHConnection conn;
	private REApplication chApplication;
	private CHMemberSelectorFrame msFrame;
	private List<String> members = new ArrayList<String>();
	private String myName = DEFAULT_NAME;
	private int port = DEFAULT_PORT;
	private CHPacket chPacket = new CHPacket();
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
		final RESourceViewer viewer;
		viewer = application.getFrame().getEditor().getViewer();
		viewer.getTextPane().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {

				chPacket.setSource(viewer.getText());
				chPacket.setCurrentFileName(application.getSourceManager()
						.getCurrentFile().getName());
				chPacket.setCommand(CHPacket.SOURCE);
				conn.write(chPacket);

			}
		});

		application.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
					}
				});
	}

	private void initializeCHListeners(final REApplication chApplication,
			final String name) {

		List<WindowListener> listeners = new ArrayList<WindowListener>();
		listeners = Arrays
				.asList(chApplication.getFrame().getWindowListeners());
		for (WindowListener aListener : listeners) {
			chApplication.getFrame().removeWindowListener(aListener);
		}

		chApplication.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				chFrameMap.remove(name);
				msFrame.releasePushed(name);
				msFrame.setMembers(members);
				setMemberSelectorListner();
			}
		});

		chApplication.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {

						if (chApplication.getFrame().getEditor() != null) {

							final JTextPane textPane = chApplication.getFrame()
									.getEditor().getViewer().getTextPane();

							textPane.setBackground(Color.LIGHT_GRAY);

							chApplication.getFrame()
									.setTitle(
											APP_NAME
													+ " Editor-"
													+ chApplication
															.getSourceManager()
															.getCurrentFile()
															.getName());

							textPane.addCaretListener(new CaretListener() {

								@Override
								public void caretUpdate(CaretEvent e) {
									String selectedText = textPane
											.getSelectedText();
									System.out.println("selectedFromCH : "
											+ selectedText);
								}
							});
						} else {
							chApplication.getFrame().setTitle(
									APP_NAME + " Editor");
						}
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

	private void setMemberSelectorListner() {
		List<JButton> buttons = new ArrayList<JButton>(msFrame.getButtons());
		for (JButton aButton : buttons) {
			aButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String name = e.getActionCommand();
					msFrame.setPushed(name);
					msFrame.setMembers(members);

					chPacket.setMyName(myName);
					chPacket.setAdressee(name);
					chPacket.setCommand(CHPacket.FILE_SEND_REQUEST);
					conn.write(chPacket);

					if (application != null) {
						doOpenNewCHE(name);
					}
					setMemberSelectorListner();
				}
			});
		}

		msFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				chPacket.setCommand(CHPacket.LOGUOT);
				conn.write(chPacket);
			}
		});
	}

	public void doOpenNewCHE(String name) {
		chApplication = application.doOpenNewRE("MyProjects/.CHProjects/"
				+ name);
		chApplication.getFrame().setTitle(APP_NAME + " Editor");
		chApplication.getFrame().setDefaultCloseOperation(
				JFrame.DISPOSE_ON_CLOSE);

		initializeCHListeners(chApplication, name);

		JMenuBar menuBar = chApplication.getFrame().getJMenuBar();

		initializeCHMenu(menuBar);

		chFrameMap.put(name, chApplication);
	}

	private void initializeCHMenu(JMenuBar menuBar) {
		menuBar.getMenu(3).remove(4);

		JMenu chMenu = new JMenu("CheCoPro");
		menuBar.add(chMenu);
		Action fileSendRequest;
		fileSendRequest = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		};

		fileSendRequest.putValue(Action.NAME, "File Send Request");
		fileSendRequest.setEnabled(false);
		chMenu.add(fileSendRequest);

		menuBar.add(connButton);
		chApplication.getFrame().setJMenuBar(menuBar);
	}

	/********************
	 * クライアントメイン動作
	 ********************/

	public void startCheCoPro() {

		initializeREListener();

		File root = application.getSourceManager().getRootDirectory();

		if (!checkProject(root, "final")) {
			File finalProject = new File(root, "final");
			finalProject.mkdir();
		}

		if (!checkProject(root, ".CHProjects")) {
			File chProject = new File(root, ".CHProjects");
			chProject.mkdir();
		}

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
			msFrame = new CHMemberSelectorFrame(myName);
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

		chPacket.setMyName(myName);
		chPacket.setCommand(CHPacket.LOGIN);

		conn.write(chPacket);

		return conn.established();
	}

	private void readFromServer() {
		Object obj = conn.read();

		if (obj instanceof CHPacket) {
			CHPacket recivedCHPacket = (CHPacket) obj;
			int command = recivedCHPacket.getCommand();
			switch (command) {
			case CHPacket.LOGIN_RESULT:
			case CHPacket.LOGIN_MEMBER_STATUS:
				typeLoginResult(recivedCHPacket);
				break;
			case CHPacket.RECIVE_SOURCE:
				typeRecivedSource(recivedCHPacket);
				break;
			case CHPacket.LOGOUT_RESULT:
				typeLogoutResult(recivedCHPacket);
				break;
			case CHPacket.REQUEST_RESULT:
				typeRequestResult(recivedCHPacket.getMyName(),
						recivedCHPacket.getFile(), recivedCHPacket.getBytes());
				break;
			case CHPacket.FILELIST_REQ:
				typeDiff(recivedCHPacket);
				break;
			case CHPacket.FILELIST_RES:
				typeDiffResult(recivedCHPacket);
				break;
			}
		}
	}

	/**********************
	 * 受信したコマンド別の処理
	 **********************/

	private void typeLoginResult(CHPacket recivedCHPacket) {
		for (String aMember : recivedCHPacket.getMembers()) {
			if (!members.contains(aMember)) {
				members.add(aMember);
			}
		}

		// 名前が被った場合
		if (recivedCHPacket.isExist()) {
			myName = recivedCHPacket.getMyName();
			chPacket.setMyName(myName);
			msFrame.setMyName(myName);
			msFrame.setTitle("CheCoProMemberSelector " + myName);
		}

		msFrame.removeLoginedMember(recivedCHPacket.getMyName());
		msFrame.setMembers(members);
		setMemberSelectorListner();

		createMembersDir(members);

		if (recivedCHPacket.getCommand() == CHPacket.LOGIN_RESULT) {
			chPacket.setCommand(CHPacket.FILELIST_REQ);
			File finalProject = getFinalProject();
			List<File> files = Arrays.asList(finalProject.listFiles());
			List<String> fileNames = new ArrayList<String>();
			for (File aFile : files) {
				fileNames.add(aFile.getName());
			}
			chPacket.setFileNames(fileNames);
			conn.write(chPacket);
		}
	}

	private void typeRecivedSource(CHPacket recivedCHPacket) {
		final String sender = recivedCHPacket.getMyName();
		final String source = recivedCHPacket.getSource();
		final String senderCurrentFile = recivedCHPacket.getCurrentFileName();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (shouldPrintSource(sender, senderCurrentFile)) {
					chFrameMap.get(sender).getFrame().getEditor()
							.setText(source);
				}
			}
		});
	}

	private void typeLogoutResult(CHPacket recivedCHPacket) {
		if (!myName.equals(recivedCHPacket.getMyName())) {
			msFrame.addLoginedMember(recivedCHPacket.getMyName());
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

	private void typeRequestResult(String parent, File recivedFile, byte[] bytes) {

		String path = (recivedFile.getPath()).replace(Integer.toString(port)
				+ "/" + parent, "");
		File file = new File("MyProjects/.CHProjects/" + parent + "/final/"
				+ path);

		if (bytes == null) {
			file.mkdir();
		} else {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file, false);
				fos.write(bytes);
				file.createNewFile();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void typeDiffResult(CHPacket recivedCHPacket) {
		List<String> diff = recivedCHPacket.getFileNames();

		chPacket.setCommand(CHPacket.FILEGET_RES);
		List<File> files = new ArrayList<File>();
		files = Arrays.asList(getFinalProject().listFiles());
		sendFile(files, diff);
	}

	private void typeDiff(CHPacket recivedCHPacket) {
		File directory = getMembersFinalDir(recivedCHPacket.getAdressee());
		List<File> files = Arrays.asList(directory.listFiles());
		List<String> recivedFileNames = recivedCHPacket.getFileNames();

		List<String> fileNames = new ArrayList<>();
		for (File aFile : files) {
			if (!recivedFileNames.contains(aFile.getName())) {
				aFile.delete();
			} else {
				fileNames.add(aFile.getName());
			}
		}

		List<String> diff = new ArrayList<String>();
		for (String aRecivedFileName : recivedFileNames) {
			if (!fileNames.contains(aRecivedFileName)) {
				if (!aRecivedFileName.startsWith(".")) {
					diff.add(aRecivedFileName);
				}
			}
		}

		chPacket.setFileNames(diff);
		chPacket.setAdressee(recivedCHPacket.getAdressee());
		chPacket.setCommand(CHPacket.FILELIST_RES);
		conn.write(chPacket);
	}

	/****************
	 * ファイル操作関係
	 ****************/

	public File getMembersFinalDir(String name) {
		File root = application.getSourceManager().getRootDirectory();

		List<File> projects = Arrays.asList(root.listFiles());
		File chDir = null;
		for (File aProject : projects) {
			if (aProject.getName().equals(".CHProjects")) {
				chDir = aProject;
			}
		}

		List<File> memberDirs = Arrays.asList(chDir.listFiles());
		File memberDir = null;
		for (File aMemberDir : memberDirs) {
			if (aMemberDir.getName().equals(name)) {
				memberDir = aMemberDir;
			}
		}

		List<File> files = Arrays.asList(memberDir.listFiles());
		for (File aFile : files) {
			if (aFile.getName().equals("final")) {
				return aFile;
			}
		}
		return null;
	}

	private void createMembersDir(List<String> members) {
		for (String aMember : members) {
			File root = new File("MyProjects/.CHProjects", aMember);
			if ((!aMember.equals(myName)) && (!root.exists())) {
				root.mkdir();
				File finalProject = new File(root, "final");
				if (!finalProject.exists()) {
					finalProject.mkdir();
				}
			}
		}
	}

	public byte[] convertFileToByte(File file) {

		if (file.isDirectory()) {
			return null;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int i = 0;
		try {
			while ((i = fis.read()) != -1) {
				baos.write(i);
			}
			baos.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	public List<String> getFileNames(File projectName) {
		List<File> files = new ArrayList<File>();
		files = Arrays.asList(projectName.listFiles());
		List<String> fileNames = new ArrayList<String>();
		for (File aFile : files) {
			if (aFile.isFile()) {
				fileNames.add(aFile.getName());
			}
		}
		return fileNames;
	}

	public File getFinalProject() {
		File root = application.getSourceManager().getRootDirectory();
		List<File> projects = new ArrayList<File>();
		projects = Arrays.asList(root.listFiles());
		for (File aProject : projects) {
			if (aProject.getName().equals("final")) {
				return aProject;
			}
		}
		return null;
	}

	private void sendFile(List<File> files, List<String> diff) {
		for (File aFile : files) {
			if (diff.contains(aFile.getName())) {
				chPacket.setFile(aFile);
				byte[] bytes = convertFileToByte(aFile);
				chPacket.setBytes(bytes);
				conn.write(chPacket);
			}
			if (aFile.isDirectory() && !aFile.getName().startsWith(".")) {
				sendFile(Arrays.asList(aFile.listFiles()), diff);
			}
		}
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
		if (!chApplication.getSourceManager().getCurrentFile().getName()
				.equals(senderCurrentFile)) {
			return false;
		}
		return true;
	}

	public boolean checkProject(File root, String name) {
		List<File> projects = new ArrayList<File>();
		projects = Arrays.asList(root.listFiles());
		for (File aProject : projects) {
			if (aProject.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/****************
	 * preference関係
	 ****************/

	private static final String LOGINID_LABEL = "CheCoPro.loginid";
	private static final String PORTNUMBER_LABEL = "CheCoPro.portnumber";

	class CheCoProPreferenceCategory extends CAbstractPreferenceCategory {

		/**
		 * 
		 */
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
				myName = getRepository().get(LOGINID_LABEL);
				nameField.setText(myName);
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
			myName = nameField.getText();
			getRepository().put(LOGINID_LABEL, myName);
			getRepository().put(PORTNUMBER_LABEL,
					Integer.toString(portBox.getSelectedIndex()));
		}

		class CheCoProPreferencePanel extends JPanel {

			/**
			 * 
			 */
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