package ronproeditor.ext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ronproeditor.REApplication;
import ronproeditor.views.RESourceViewer;
import ch.connection.Connection;
import ch.datas.FileData;
import ch.datas.LoginData;
import ch.datas.SourceData;
import ch.frame.CHMemberSelectorFrame;
import ch.frame.LoginDialog;
import clib.preference.model.CAbstractPreferenceCategory;

public class RECheCoProManager {

	public static final String APP_NAME = "CheCoPro";

	private REApplication application;
	private Connection conn;
	private REApplication chApplication;
	private boolean started;
	private CHMemberSelectorFrame msFrame;
	private List<String> members = new ArrayList<String>();
	private String myName;
	private List<List<Object>> chDatas = new ArrayList<List<Object>>();

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
				SourceData sourceData = new SourceData();
				sourceData.setMyName(myName);
				sourceData.setSource(viewer.getText());
				sourceData.setCurrentFileName(application.getSourceManager()
						.getCCurrentFile().getNameByString());
				conn.write(sourceData);
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
			conn = new Connection(sock);
			newConnectionOpened(conn);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void newConnectionOpened(Connection conn) {

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
		LoginDialog loginDialog = new LoginDialog();
		loginDialog.openLoginDialog();
		myName = loginDialog.getName();

		LoginData loginData = new LoginData();
		loginData.setMyName(myName);

		conn.write(loginData);

		return conn.established();
	}

	public void doOpenNewCHE(String name) {
		chApplication = application.doOpenNewRE(name + "Projects");
		chApplication.getFrame().setTitle("CheCoPro Editor");

		chApplication.getFrame().addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
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

	@SuppressWarnings("unchecked")
	private void readFromServer() {
		Object obj = conn.read();
		if (obj instanceof SourceData) {
			final String source = ((SourceData) obj).getSource();
			final String name = ((SourceData) obj).getMyName();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (isStarted()) {
						for (List<Object> aData : chDatas) {
							if (name.equals(aData.get(1))
									&& ((REApplication) aData.get(0))
											.getFrame().getEditor() != null) {
								((REApplication) aData.get(0)).getFrame()
										.getEditor().setText(source);
							}
						}
					}
				}
			});
		} else if (obj instanceof List) {
			for (String aMember : (List<String>) obj) {
				if (!members.contains(aMember)) {
					members.add(aMember);
				}
			}
			for (String aMember : members) {
				if (!((List<String>) obj).contains(aMember)) {
					members.remove(aMember);
					break;
				}
			}
			msFrame.setMembers(members);
			setMemberSelectorListner();
		} else if (obj instanceof FileData) {
			FileData fileData = (FileData) obj;
			if (fileData.getRequestName().equals(myName)) {
				// ファイル送信要求を受信したときの処理
				sendMyProjects(fileData);
			} else if (fileData.getMyName().equals(myName)) {
				// ファイル送信要求を出しファイルを受信したときの処理
				File root = fileData.getFile();
				List<File> projects = new ArrayList<File>();
				projects = Arrays.asList(root.listFiles());
				createMemberProjects(projects);
			}
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
						fileSendRequest(name);
					}
					setMemberSelectorListner();
				}
			});
		}

		msFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				conn.write(myName);
				conn.close();
			}
		});
	}

	public void sendMyProjects(FileData fileData) {
		File root = application.getSourceManager().getRootDirectory();
		fileData.setFile(root);
		conn.write(fileData);
	}

	public void fileSendRequest(String name) {
		FileData fileData = new FileData();
		fileData.setRequestName(name);
		fileData.setMyName(myName);
		conn.write(fileData);
	}

	public void createMemberProjects(List<File> projects) {
		File root = chApplication.getSourceManager().getRootDirectory();

		for (File aProject : projects) {
			File project = new File(root, aProject.getName());
			project.mkdir();
			List<File> files = new ArrayList<File>();
			files = Arrays.asList(aProject.listFiles());
			for (File aFile : files) {
				File file = new File(project, aFile.getName());
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

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
			// TODO Auto-generated method stub

		}

		@Override
		public void save() {
			// TODO Auto-generated method stub

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