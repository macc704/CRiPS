package ronproeditor.ext;

import gs.connection.Connection;
import gs.connection.DivideRoom;
import gs.connection.LoginData;
import gs.connection.SourceData;
import gs.frame.CHMemberSelectorFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	private JButton connButton;
	private boolean started;
	private CHMemberSelectorFrame msFrame;
	private List<String> members = new ArrayList<String>();
	private String myName;
	private List<List<Object>> chDatas = new ArrayList<List<Object>>();

	public static void main(String[] args) {
		REGoodSubManager man = new REGoodSubManager();
		LoginData data = new LoginData();
		data.setMyName("hoge"+((int)(Math.random()*10000))%1000);
		data.setRoomNum(1);
		man.connectServer(data);
	}
	
	private REGoodSubManager() {
	}

	public REGoodSubManager(REApplication application) {
		this.application = application;	
	}

	public void startGoodSub() {
		initializeListener();
		new Thread() {
			public void run() {
				LoginData data = loginDialog();
				connectServer(data);
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
				conn.write(sourceData);
			}
		});
	}

	private void connectServer(LoginData login) {
		try (Socket sock = new Socket("localhost", 10000)) {
			conn = new Connection(sock);
			newConnectionOpened(login);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void newConnectionOpened(LoginData loginData) {

		conn.shakehandForClient();

		boolean login = login(loginData);
		if (login) {
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

	private LoginData loginDialog() {
		DivideRoom room = new DivideRoom();
		int groupNum = -1;
		try {
			groupNum = room.selectRoomNum();
		} catch (IOException e) {
			e.printStackTrace();
		}

		LoginData loginData = new LoginData();
		myName = room.getUserName();
		loginData.setMyName(myName);
		loginData.setRoomNum(groupNum);
		
		return loginData;
	}
	
	private boolean login(LoginData data) {
		conn.write(data);
		return conn.established();
	}

	public void doOpenNewCHE(String name) {
		chApplication = application.doOpenNewRE("CHTestProject");
		chApplication.getFrame().setTitle("CheCoPro Editor");

		chApplication.getFrame().addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {

			}

		});

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
							if (aData.get(1) == name) {
								((REApplication) aData.get(0)).getFrame()
										.getEditor().setText(source);
							}
						}
						// chApplication.getFrame().getEditor().setText(source);
					}
				}
			});
		} else if (obj instanceof List) {
			for (String aMember : (List<String>) obj) {
				if (!members.contains(aMember)) {
					members.add(aMember);
				}
			}
			//msFrame.setMembers(members);
			System.out.println(obj);
			msFrame.setMembers((List<String>)obj);
			setMemberSelectorListner();
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
					// sendObject.setSelectedMember(pushed);
					// conn.write(loginData);
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

}