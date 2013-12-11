package ronproeditor.ext;

import gs.connection.Connection;
import gs.connection.DivideRoom;
import gs.connection.SendObject;
import gs.frame.CHMemberSelectorFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
	private SendObject sendObject = new SendObject();
	private CHMemberSelectorFrame frame;
	private List<String> members = new ArrayList<String>();

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
		viewer = application.getFrame().getEditor().getViewer();
		viewer.getTextPane().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String text = viewer.getText();
				conn.write(text);
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
			frame = new CHMemberSelectorFrame(sendObject.getMyName());
			frame.open();
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
		DivideRoom room = new DivideRoom();
		int groupNum = -1;
		try {
			groupNum = room.selectRoomNum();
		} catch (IOException e) {
			e.printStackTrace();
		}

		sendObject.setMyName(room.getUserName());
		sendObject.setRoomNum(groupNum);

		conn.write(sendObject);

		return conn.established();
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

	@SuppressWarnings("unchecked")
	private void readFromServer() {
		Object obj = conn.read();
		if (obj instanceof String) {
			final String text = (String) obj;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (isStarted()) {
						chApplication.getFrame().getEditor().setText(text);
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
			setMemberSelectorListner();
		}
	}

	public boolean isStarted() {
		return started;
	}

	public void setMemberSelectorListner() {
		List<JButton> buttons = new ArrayList<JButton>(frame.getButtons());
		for (final JButton aButton : buttons) {
			aButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("pushed " + aButton.getText());
					sendObject.setSelectedMember(aButton.getText());
					conn.write(sendObject);
					if (application != null) {
						doOpenNewCHE();
					}
				}
			});
		}
	}

}