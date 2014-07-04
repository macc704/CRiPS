package ch.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
			
			if(pushed.equals(user)){
				// eclipse active 
			} else {
				conn.write(new CHFilelistRequest(pushed));
				REApplication application = new REApplication();
				// メンバのディレクトリに変更
				application.doOpenNewRE(CH_DIR_PATH + "/" + user);
			}
		}
	};

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
