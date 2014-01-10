package ch.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

	public void setMembers(List<String> members) {

		this.getContentPane().removeAll();
		JPanel buttonPanel = new JPanel();
		this.getContentPane().add(buttonPanel);

		for (final String aMember : members) {
			JButton button = new JButton(aMember);

			buttonPanel.add(button);

			for (String aPushed : pushed) {
				if (aMember.equals(aPushed)) {
					button.setEnabled(false);
				}
			}

			for (String aLoginedMember : loginedMembers) {
				if (aMember.equals(aLoginedMember)) {
					button.setForeground(Color.RED);
				}
			}

			if (aMember.equals(user)) {
				button.setEnabled(false);
			}
			buttons.add(button);
		}
		this.getContentPane().validate();

	}

	public List<JButton> getButtons() {
		return buttons;
	}

	public void setPushed(String pushed) {
		this.pushed.add(pushed);
	}

	public void releasePushed(String name) {
		pushed.remove(name);
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
		List<String> members = new ArrayList<String>();
		members.add("user1");
		members.add("name");
		members.add("user2");
		frame.open();
		frame.setMembers(members);
		members.add("user3");
		frame.setMembers(members);

	}
}
