package gs.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class CHMemberSelectorFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String myName;
	private List<JButton> buttons = new ArrayList<JButton>();
	private String pushedName;

	public CHMemberSelectorFrame(String myName) {
		this.myName = myName;
		pushedName = null;
	}

	public void open() {
		this.setTitle("CheCoProMemberSelector " + myName);
		this.setBounds(100, 100, 150, 500);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	public void setMembers(List<String> members) {

		this.getContentPane().removeAll();
		JPanel buttonPanel = new JPanel();
		this.getContentPane().add(buttonPanel);

		for (String aMember : members) {
			JButton button = new JButton(aMember);
			button.addActionListener(this);
			button.setActionCommand(aMember);
			buttonPanel.add(button);
			if (aMember.equals(myName)) {
				button.setEnabled(false);
			}
			buttons.add(button);
		}
		this.getContentPane().validate();

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

	@Override
	public void actionPerformed(ActionEvent e) {
		pushedName = e.getActionCommand();
		System.out.println(pushedName);
	}

}
