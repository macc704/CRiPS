package ch.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import ronproeditor.REApplication;
import ch.conn.framework.CHConnection;
import ch.conn.framework.CHUserState;
import ch.conn.framework.packets.CHFilelistRequest;

public class CHMemberSelectorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String CH_DIR_PATH = "runtime-EclipseApplication/.ch";

	private String user;
	private List<JButton> buttons = new ArrayList<JButton>();
	private CHConnection conn;
	private HashMap<String, REApplication> openedCHEditors = new HashMap<>();

	public CHMemberSelectorFrame(String user) {
		this.user = user;
	}

	public CHMemberSelectorFrame(String user, CHConnection conn) {
		this.user = user;
		this.conn = conn;
	}

	public void open() {
		this.setTitle("CheCoProMemberSelector " + user);
		this.setBounds(100, 100, 150, 500);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	public void setMembers(List<CHUserState> userStates) {

		this.getContentPane().removeAll();
		JPanel buttonPanel = new JPanel();
		this.getContentPane().add(buttonPanel);

		for (final CHUserState aUserState : userStates) {
			JButton button = new JButton(aUserState.getUser());
			button.setBackground(aUserState.getColor());

			buttonPanel.add(button);

			if (!aUserState.isLogin()) {
				button.setForeground(Color.RED);
			}

			button.addActionListener(buttonAction);

			buttons.add(button);
		}
		this.getContentPane().validate();

	}

	private ActionListener buttonAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String pushed = e.getActionCommand();

			if (pushed.equals(user)) {
				// eclipse active
			} else if (openedCHEditors.get(pushed) == null) {
				conn.write(new CHFilelistRequest(pushed));

				REApplication application = new REApplication();
				REApplication chApplication = application
						.doOpenNewRE(CH_DIR_PATH + "/" + pushed);
				openedCHEditors.put(pushed, chApplication);
				initCHEditor(chApplication);
			}
		}
	};

	public void initCHEditor(REApplication application) {
		initCHEMenubar(application);
		initCHWindow(application);
	}

	private void initCHEMenubar(final REApplication application) {

		JMenuBar menuBar = application.getFrame().getJMenuBar();
		menuBar.add(initSyncButton(application));
		menuBar.add(initPullButton());
		application.getFrame().setJMenuBar(menuBar);
	}

	private JToggleButton initSyncButton(final REApplication application) {

		final JToggleButton syncButton = new JToggleButton("同期中", true);

		syncButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (syncButton.isSelected()) {
					syncButton.setText("同期中");
				} else if (!syncButton.isSelected()) {
					syncButton.setText("非同期中");
				}

				if (application.getFrame().getEditor() != null) {
					application.getFrame().getEditor().getViewer()
							.getTextPane()
							.setEditable(!syncButton.isSelected());
				}
			}
		});

		return syncButton;
	}

	private JButton initPullButton() {

		JButton pullButton = new JButton("取り込み↓");
		pullButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO openPullDialog
			}
		});

		return pullButton;
	}

	private void initCHWindow(final REApplication application) {

		List<WindowListener> listeners = new ArrayList<WindowListener>();
		listeners = Arrays.asList(application.getFrame().getWindowListeners());
		for (WindowListener aListener : listeners) {
			application.getFrame().removeWindowListener(aListener);
		}

		application.getFrame().setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		application.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				openedCHEditors.remove(application);
			}
		});
	}

	public List<JButton> getButtons() {
		return buttons;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public static void main(String[] args) {
		CHMemberSelectorFrame frame = new CHMemberSelectorFrame("name");
		List<CHUserState> userStates = new ArrayList<CHUserState>();
		userStates.add(new CHUserState("user1", true, Color.CYAN));
		userStates.add(new CHUserState("name", true, Color.LIGHT_GRAY));
		userStates.add(new CHUserState("user2", false, Color.MAGENTA));
		frame.open();
		frame.setMembers(userStates);
		userStates.add(new CHUserState("user3", true, Color.YELLOW));
		frame.setMembers(userStates);

	}
}
