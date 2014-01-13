package ch.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ch.conn.framework.CHUserState;

public class CHMemberSelectorFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String user;
	private List<JButton> buttons = new ArrayList<JButton>();
	private List<String> pushed = new ArrayList<String>();
	private List<String> loginedMembers = new ArrayList<String>();

	public CHMemberSelectorFrame(String myName) {
		this.user = myName;
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

			for (String aPushed : pushed) {
				if (aUserState.getUser().equals(aPushed)) {
					button.setEnabled(false);
				}
			}

			for (String aLoginedMember : loginedMembers) {
				if (aUserState.getUser().equals(aLoginedMember)) {
					button.setForeground(Color.RED);
				}
			}

			if (aUserState.getUser().equals(user)) {
				button.setEnabled(false);
			}
			buttons.add(button);
		}
		this.getContentPane().validate();

	}

	public List<JButton> getButtons() {
		return buttons;
	}

	public void setDisable(String user) {
		this.pushed.add(user);
	}

	public void setEnable(String user) {
		pushed.remove(user);
	}

	public void addLoginedMember(String name) {
		loginedMembers.add(name);
	}

	public void removeLoginedMember(String name) {
		loginedMembers.remove(name);
	}

	public void setUser(String user) {
		this.user = user;
	}

	public static void main(String[] args) {
		CHMemberSelectorFrame frame = new CHMemberSelectorFrame("name");
		List<CHUserState> userStates = new ArrayList<CHUserState>();
		userStates.add(new CHUserState("user1", true, Color.CYAN));
		userStates.add(new CHUserState("name", true, Color.LIGHT_GRAY));
		userStates.add(new CHUserState("user2", true, Color.MAGENTA));
		frame.open();
		frame.setMembers(userStates);
		userStates.add(new CHUserState("user3", true, Color.YELLOW));
		frame.setMembers(userStates);

	}
}
