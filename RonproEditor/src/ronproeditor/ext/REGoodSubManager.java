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
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import ronproeditor.REApplication;
import ronproeditor.views.RESourceViewer;
import ch.connection.Connection;
import ch.datas.LoginData;
import ch.datas.SourceData;
import ch.frame.CHMemberSelectorFrame;
import ch.frame.LoginDialog;

public class REGoodSubManager {

	public static final String APP_NAME = "GoodSub";

	private REApplication application;
	private Connection conn;
	private REApplication chApplication;
	private boolean started;
	private CHMemberSelectorFrame msFrame;
	private List<String> members = new ArrayList<String>();
	private String myName;
	private List<List<Object>> chDatas = new ArrayList<List<Object>>();

	public static void main(String[] args) {
		new REGoodSubManager();
	}

	public REGoodSubManager(REApplication application) {
		this.application = application;
		initialize();
	}

	public REGoodSubManager() {
		connectServer();
	}

	private void initialize() {
	}

	public void startGoodSub() {

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

		sendRootDirectory();

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
		int groupNumber = loginDialog.getGroupNumber();
		myName = loginDialog.getName();

		LoginData loginData = new LoginData();
		loginData.setMyName(myName);
		loginData.setGroupNumber(groupNumber);

		conn.write(loginData);

		return conn.established();
	}

	public void doOpenNewCHE(String name) {
		chApplication = application.doOpenNewRE("MyProjects");
		chApplication.getFrame().setTitle("CheCoPro Editor");

		chApplication.getFrame().addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {

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
							if (name.equals(aData.get(1))) {
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
			msFrame.setMembers(members);
			setMemberSelectorListner();
		} else if (obj instanceof File) {
			File root = (File) obj;
			List<File> projects = new ArrayList<File>();
			projects = Arrays.asList(root.listFiles());
			for (File aProject : projects) {
				System.out.println("project : " + aProject.getName());
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
					System.out.println("pushed " + name);
					msFrame.setPushed(name);
					msFrame.setMembers(members);
					if (application != null) {
						doOpenNewCHE(name);
					}
					setMemberSelectorListner();
				}
			});
		}
	}

	public void sendRootDirectory() {
		File root = application.getSourceManager().getRootDirectory();
		conn.write(root);
	}

}