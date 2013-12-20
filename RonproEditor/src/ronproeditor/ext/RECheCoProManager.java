package ronproeditor.ext;

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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import ronproeditor.REApplication;
import ronproeditor.views.RESourceViewer;
import ch.connection.CHConnection;
import ch.connection.CHPacket;
import ch.view.CHMemberSelectorFrame;
import clib.preference.model.CAbstractPreferenceCategory;

public class RECheCoProManager {

	public static final String APP_NAME = "CheCoPro";

	private REApplication application;
	private CHConnection conn;
	private REApplication chApplication;
	private boolean started;
	private CHMemberSelectorFrame msFrame;
	private List<String> members = new ArrayList<String>();
	private String myName = "guest";
	private List<List<Object>> chDatas = new ArrayList<List<Object>>();
	private CHPacket chPacket = new CHPacket();

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

		File root = application.getSourceManager().getRootDirectory();

		if (!checkFinalProject(root)) {
			File finalProject = new File(root, "final");
			finalProject.mkdir();
		}

		if (!checkFinalClass(root)) {
			File finalClass = new File(root + "/final", "Final.java");
			try {
				finalClass.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void startCheCoPro() {

		initializeListener();

		new Thread() {
			public void run() {
				connectServer();
			}
		}.start();
	}

	public void initializeListener() {
		final RESourceViewer viewer;
		viewer = application.getFrame().getEditor().getViewer();
		viewer.getTextPane().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {

				chPacket.setSource(viewer.getText());
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

	private void connectServer() {

		try (Socket sock = new Socket("localhost", 10000)) {
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

	public void doOpenNewCHE(String name) {
		chApplication = application.doOpenNewRE(name + "Project");
		chApplication.getFrame().setTitle("CheCoPro Editor");
		chApplication.getFrame().setDefaultCloseOperation(
				JFrame.DISPOSE_ON_CLOSE);

		List<WindowListener> listeners = new ArrayList<WindowListener>();
		listeners = Arrays
				.asList(chApplication.getFrame().getWindowListeners());
		for (WindowListener aListener : listeners) {
			chApplication.getFrame().removeWindowListener(aListener);
		}

		chApplication.getFrame().addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

			}

		});

		chApplication.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (chApplication.getFrame().getEditor() != null) {
							final JTextPane textPane = chApplication.getFrame()
									.getEditor().getViewer().getTextPane();
							textPane.addCaretListener(new CaretListener() {

								@Override
								public void caretUpdate(CaretEvent e) {
									String selectedText = textPane
											.getSelectedText();
									System.out.println("selectedFromCH : "
											+ selectedText);
								}
							});
						}
					}
				});

		started = false;

		final JButton connButton = new JButton("Start");
		connButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (connButton.getText().equals("Start")) {
					started = true;
					connButton.setText("Stop");
				} else if (connButton.getText().equals("Stop")) {
					started = false;
					connButton.setText("Start");
				}
			}
		});

		JMenuBar menuBar = chApplication.getFrame().getJMenuBar();
		menuBar.add(connButton);
		chApplication.getFrame().setJMenuBar(menuBar);

		List<Object> chFrames = new ArrayList<Object>();
		chFrames.add(chApplication);
		chFrames.add(name);
		chDatas.add(chFrames);
	}

	private void readFromServer() {
		Object obj = conn.read();

		if (obj instanceof CHPacket) {
			CHPacket recivedCHPacket = (CHPacket) obj;
			int command = recivedCHPacket.getCommand();
			switch (command) {
			case CHPacket.LOGIN_RESULT:
				typeLoginResult(recivedCHPacket);
				break;
			case CHPacket.RECIVE_SOURCE:
				typeRecivedSource(recivedCHPacket);
				break;
			case CHPacket.LOGOUT_RESULT:
				typeLogoutResult(recivedCHPacket);
				break;
			case CHPacket.RECIVE_FILE:
				typeRecivedFile(recivedCHPacket);
				break;
			}
		}
	}

	private void typeLoginResult(CHPacket recivedCHPacket) {
		for (String aMember : recivedCHPacket.getMembers()) {
			if (!members.contains(aMember)) {
				members.add(aMember);
			}
		}

		// ñºëOÇ™îÌÇ¡ÇΩèÍçá
		if (recivedCHPacket.isExist()) {
			myName = recivedCHPacket.getMyName();
			chPacket.setMyName(myName);
			msFrame.setMyName(myName);
			msFrame.setTitle("CheCoProMemberSelector " + myName);
		}

		msFrame.setMembers(members);
		setMemberSelectorListner();

		createMembersDir(members);

		sendFiles(getFinalProject());
	}

	private void createMembersDir(List<String> members) {
		for (String aMember : members) {
			File root = new File(aMember + "Project");
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

	private void setFileToPacket(List<String> fileNames, List<byte[]> bytes) {
		chPacket.setCommand(CHPacket.FILE);
		chPacket.setFileNames(fileNames);
		chPacket.setBytes(bytes);
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

	private void sendFiles(File finalProject) {
		List<File> files = new ArrayList<File>();
		files = Arrays.asList(finalProject.listFiles());

		List<byte[]> bytes = new ArrayList<byte[]>();
		for (File aFile : files) {
			if (aFile.isFile()) {
				bytes.add(convertFileToByte(aFile));
			}
		}

		setFileToPacket(getFileNames(finalProject), bytes);
		conn.write(chPacket);
	}

	private void typeRecivedSource(CHPacket recivedCHPacket) {
		final String sender = recivedCHPacket.getMyName();
		final String source = recivedCHPacket.getSource();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (isStarted()) {
					for (List<Object> aData : chDatas) {
						if (sender.equals(aData.get(1))
								&& ((REApplication) aData.get(0)).getFrame()
										.getEditor() != null) {
							((REApplication) aData.get(0)).getFrame()
									.getEditor().setText(source);
						}
					}
				}
			}
		});
	}

	private void typeLogoutResult(CHPacket recivedCHPacket) {
		members.remove(recivedCHPacket.getMyName());
		msFrame.setMembers(members);
		setMemberSelectorListner();
	}

	private void typeRecivedFile(CHPacket recivedCHPacket) {
		String senderName = recivedCHPacket.getMyName();
		List<byte[]> bytes = new ArrayList<byte[]>();
		List<String> fileNames = new ArrayList<String>();
		bytes = recivedCHPacket.getBytes();
		fileNames = recivedCHPacket.getFileNames();
		int i = 0;
		for (String aFileName : fileNames) {
			File file = new File(senderName + "Project/final", aFileName);
			try {
				FileOutputStream fos = new FileOutputStream(file, false);
				fos.write(bytes.get(i));
				file.createNewFile();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
	}

	public boolean isStarted() {
		return started;
	}

	public void setMemberSelectorListner() {
		List<JButton> buttons = new ArrayList<JButton>(msFrame.getButtons());
		for (JButton aButton : buttons) {
			aButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String name = e.getActionCommand();
					msFrame.setPushed(name);
					msFrame.setMembers(members);
					if (application != null) {
						doOpenNewCHE(name);
						// fileSendRequest(name);
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
				conn.close();
			}
		});
	}

	private boolean checkFinalProject(File root) {
		List<File> projects = new ArrayList<File>();
		projects = Arrays.asList(root.listFiles());
		for (File aProject : projects) {
			if (aProject.getName().equals("final")) {
				return true;
			}
		}
		return false;
	}

	private boolean checkFinalClass(File root) {
		List<File> projects = new ArrayList<File>();
		File finalProject = null;
		projects = Arrays.asList(root.listFiles());
		for (File aProject : projects) {
			if (aProject.getName().equals("final")) {
				finalProject = aProject;
			}
		}

		List<File> classes = new ArrayList<File>();
		classes = Arrays.asList(finalProject);

		for (File aClass : classes) {
			if (aClass == null) {
				return false;
			} else if (aClass.getName().equals("Final.java")) {
				return true;
			}
		}
		return false;
	}

	private static final String LOGINID_LABEL = "CheCoPro.loginid";

	class CheCoProPreferenceCategory extends CAbstractPreferenceCategory {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private JTextField nameField = new JTextField(15);
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
		}

		@Override
		public void save() {
			myName = nameField.getText();
			getRepository().put(LOGINID_LABEL, myName);
		}

		class CheCoProPreferencePanel extends JPanel {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public CheCoProPreferencePanel() {
				this.add(new JLabel("name : "));
				this.add(nameField);
			}
		}

	}

}