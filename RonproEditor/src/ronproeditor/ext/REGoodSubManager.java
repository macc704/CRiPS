package ronproeditor.ext;

import gs.connection.Connection;
import gs.connection.DivideRoom;
import gs.connection.SendObject;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import ronproeditor.REApplication;
import ronproeditor.RESourceManager;
import ronproeditor.views.RESourceViewer;

public class REGoodSubManager {

	public static final String APP_NAME = "GoodSub";

	private REApplication application;
	private RESourceViewer viewer;
	private Connection conn;
	private RESourceViewer sv;
	private JButton btn;
	private JFrame frame;
	private SendObject sendObject = new SendObject();
	private JPanel btnPanel;
	private List<JButton> userBtns = new ArrayList<JButton>();

	public REGoodSubManager(REApplication application) {
		this.application = application;
		initialize();
	}

	private void initialize() {
	}

	public void startGoodSub() {

		initializeFrame();
		initializeListener();

		// frame.addWindowListener(new WindowAdapter() {
		// @Override
		// public void windowClosed(WindowEvent e) {
		// conn.close();
		// }
		// });

		new Thread() {
			public void run() {
				// ê⁄ë±
				try (Socket sock = new Socket("localhost", 10000)) {
					conn = new Connection(sock);
					newConnectionOpened(conn);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();

	}

	public void initializeFrame() {
		// éÛêMëãê∂ê¨
		frame = new JFrame("CheCoPro");
		frame.setBounds(100, 100, 600, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		sv = new RESourceViewer();
		btn = new JButton("stop");
		JSplitPane verticalSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		JSplitPane horizontalSplitter = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);

		List<JButton> btns = new ArrayList<JButton>();
		btns.add(new JButton("user1"));
		btns.add(new JButton("user2"));
		btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(0, 1));
		JScrollPane scrollBtn = new JScrollPane();

		scrollBtn.setViewportView(btnPanel);
		horizontalSplitter.add(scrollBtn, JSplitPane.LEFT);

		verticalSplitter.setTopComponent(btn);
		verticalSplitter.setBottomComponent(sv);

		horizontalSplitter.add(verticalSplitter, JSplitPane.RIGHT);

		frame.getContentPane().add(horizontalSplitter, BorderLayout.CENTER);
		frame.setVisible(true);
	}

	public void initializeListener() {
		viewer = application.getFrame().getEditor().getViewer();
		viewer.getTextPane().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// sendObject.setSource(viewer.getText());
				// sendList.set(0, sendObject.getUserName());
				// sendList.set(1, viewer.getText());
				String text = viewer.getText();
				conn.write(text);
			}
		});

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String btnText = btn.getText();
				if (btnText == "start") {
					btn.setText("stop");
				} else if (btnText == "stop") {
					btn.setText("start");
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void newConnectionOpened(Connection conn) {

		Object obj;
		int groupNum = 0;
		List<String> users = new ArrayList<String>();

		conn.shakehandForClient();

		// ïîâÆî‘çÜê›íË
		DivideRoom room = new DivideRoom();
		try {
			groupNum = room.selectRoomNum();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// SendObject data = new SendObject();
		sendObject.setUserName(room.getUserName());
		sendObject.setRoomNum(groupNum);

		users.add(sendObject.getUserName());

		// conn.write(groupNum);
		conn.write(sendObject);

		frame.setTitle("CheCoPro Group No." + groupNum);

		System.out.println("client established");

		CPSourceManager sourceManager = new CPSourceManager();
		sourceManager.createProject("CheCoTest");

		try {
			while (conn.established()) {
				obj = conn.read();
				if (obj instanceof String) {
					// getList.set(1, ((List<String>) obj).get(1));
					final String text = (String) obj;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (btn.getText() == "stop") {
								sv.setText(text);
							}
						}
					});
				} else if (obj instanceof File) {
					File file = (File) obj;
					System.out.println("get" + file.getName());
				} else if (obj instanceof List) {
					users = (List<String>) obj;
					for (String aUser : users) {
						for (JButton aBtn : userBtns) {
							if (aUser != aBtn.getText()) {
								userBtns.add(new JButton("aUser"));
							}
						}
					}
					for (JButton aBtn : userBtns) {
						if (aBtn.getText() != sendObject.getUserName()) {
							// É{É^Éìí«â¡
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		conn.close();
		System.out.println("client closed");

	}

	public void fileSender() {
		File file;
		file = application.getSourceManager().getCurrentFile();
		conn.write(file);
	}

	public void fileReceiver() {
		application.doCreateProject();
		application.doCreateFile();
	}

	class CPSourceManager extends RESourceManager {

		private File rootDirectory = application.getSourceManager()
				.getRootDirectory();
		private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
				this);

		public void createProject(String name) {
			if (!canCreateProject(name)) {
				throw new RuntimeException();
			}

			try {
				File newProject = new File(rootDirectory, name);
				newProject.mkdir();
				propertyChangeSupport.firePropertyChange(MODEL_REFRESHED, null,
						null);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

	}

}