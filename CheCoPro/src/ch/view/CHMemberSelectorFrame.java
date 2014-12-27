package ch.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.conn.framework.CHUserState;

public class CHMemberSelectorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private String user;
	private List<JButton> buttons = new ArrayList<JButton>();

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
		List<JPanel> panels = new ArrayList<JPanel>();
		JPanel basePanel = new JPanel();
		
		this.getContentPane().add(basePanel);

		for (final CHUserState aUserState : userStates) {
			
			JPanel panel = new JPanel();
			
			JButton button = new JButton(aUserState.getUser());
			button.setBackground(aUserState.getColor());
			
			JLabel lastLoginTime = new JLabel("online");
			
			panel.add(button);
			panel.add(lastLoginTime);
			panels.add(panel);

			if (!aUserState.isLogin()) {
				button.setForeground(Color.RED);
				lastLoginTime.setText(aUserState.getLastLogin().toString());
			}

			buttons.add(button);
		}
		
		for (JPanel aPanle : panels) {
			basePanel.add(aPanle);
		}
		
		this.getContentPane().validate();

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
