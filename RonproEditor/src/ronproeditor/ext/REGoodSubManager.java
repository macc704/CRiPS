package ronproeditor.ext;

import gs.connection.Connection;
import gs.connection.DivideRoom;
import gs.connection.SendObject;
import gs.frame.CHMemberSelectorFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import ronproeditor.REApplication;
import ronproeditor.views.RESourceViewer;

public class REGoodSubManager {

	public static final String APP_NAME = "GoodSub";

	private REApplication application;
	private Connection conn;
	private REApplication chApplication;
	private RESourceViewer viewer;
	private JButton connButton;
	private boolean started;

	public static void main(String[] args) {
	}

	public REGoodSubManager(REApplication application) {
		this.application = application;
		initialize();
	}

	private void initialize() {
	}

	public void startGoodSub() {

		// initializeFrame();
		initializeListener();

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
		// frame = new JFrame("CheCoPro");
		// frame.setBounds(100, 100, 600, 500);
		// frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// sv = new RESourceViewer();
		// btn = new JButton("stop");
		// JSplitPane verticalSplitter = new
		// JSplitPane(JSplitPane.VERTICAL_SPLIT);
		//
		// JSplitPane horizontalSplitter = new JSplitPane(
		// JSplitPane.HORIZONTAL_SPLIT);
		//
		// List<JButton> btns = new ArrayList<JButton>();
		// btns.add(new JButton("user1"));
		// btns.add(new JButton("user2"));
		// btnPanel = new JPanel();
		// btnPanel.setLayout(new GridLayout(0, 1));
		// JScrollPane scrollBtn = new JScrollPane();
		//
		// scrollBtn.setViewportView(btnPanel);
		// horizontalSplitter.add(scrollBtn, JSplitPane.LEFT);
		//
		// verticalSplitter.setTopComponent(btn);
		// verticalSplitter.setBottomComponent(sv);
		//
		// horizontalSplitter.add(verticalSplitter, JSplitPane.RIGHT);
		//
		// frame.getContentPane().add(horizontalSplitter, BorderLayout.CENTER);
		// frame.setVisible(true);
	}

	public void initializeListener() {
		viewer = application.getFrame().getEditor().getViewer();
		viewer.getTextPane().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String text = viewer.getText();
				conn.write(text);
			}
		});

		// btn.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// // TODO Auto-generated method stub
		// String btnText = btn.getText();
		// if (btnText == "start") {
		// btn.setText("stop");
		// } else if (btnText == "stop") {
		// btn.setText("start");
		// }
		// }
		// });
	}

	@SuppressWarnings("unchecked")
	private void newConnectionOpened(Connection conn) {

		int groupNum = 0;
		List<String> members = new ArrayList<String>();
		SendObject sendObject = new SendObject();

		conn.shakehandForClient();

		// ïîâÆî‘çÜê›íË
		DivideRoom room = new DivideRoom();
		try {
			groupNum = room.selectRoomNum();
		} catch (IOException e) {
			e.printStackTrace();
		}

		sendObject.setMyName(room.getUserName());
		sendObject.setRoomNum(groupNum);

		conn.write(sendObject);

		CHMemberSelectorFrame frame = new CHMemberSelectorFrame(
				sendObject.getMyName());
		frame.open();

		if (conn.established()) {
			System.out.println("client established");
			doOpenNewCHE();
		}

		try {
			while (conn.established()) {
				Object obj = conn.read();
				if (obj instanceof String) {
					final String text = (String) obj;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (isStarted()) {
								chApplication.getFrame().getEditor()
										.setText(text);
							}
						}
					});
				} else if (obj instanceof List) {
					for (String aMember : (List<String>) obj) {
						if (!members.contains(aMember)) {
							members.add(aMember);
						}
					}
					frame.setMembers(members);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		conn.close();
		System.out.println("client closed");

	}

	public void doOpenNewCHE() {
		chApplication = application.doOpenNewRE("CHTestProject");
		chApplication.getFrame().setTitle("CheCoPro Editor");

		started = false;

		connButton = new JButton("Start");
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

	}

	public boolean isStarted() {
		return started;
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

}