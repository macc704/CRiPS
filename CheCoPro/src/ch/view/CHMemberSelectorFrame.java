package ch.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.conn.framework.CHUserState;
import ch.util.CHComponent;

public class CHMemberSelectorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private String user;
	private List<JButton> buttons = new ArrayList<JButton>();
	private List<String> editorOpens = new ArrayList<String>();
	private CHComponent component;

	public CHMemberSelectorFrame(String user) {
		this.user = user;
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
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		
		this.getContentPane().add(basePanel);

		for (final CHUserState aUserState : userStates) {
			
			JPanel panel = new JPanel();
			
			JButton button = new JButton(aUserState.getUser());
			button.setBackground(aUserState.getColor());
			
			JLabel lastLoginTime = new JLabel("online");
			lastLoginTime.setForeground(Color.GREEN);
			
			panel.setLayout(new BorderLayout());
			panel.add(button, BorderLayout.NORTH);
			panel.add(lastLoginTime, BorderLayout.CENTER);
			panels.add(panel);

			if (!aUserState.isLogin()) {
				button.setForeground(Color.RED);
				lastLoginTime.setText(formatter.format(aUserState.getLastLogin()));
				lastLoginTime.setForeground(Color.RED);
			}

			buttons.add(button);
		}
		
		for (JPanel aPanle : panels) {
			basePanel.add(aPanle);
		}
		
		this.getContentPane().validate();

	}
	
	public void initListener() {
		initButtonListener();
		initWindowListener();
	}
	
	private void initButtonListener() {
		List<JButton> buttons = new ArrayList<JButton>(getButtons());
		for (JButton aButton : buttons) {
			aButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String user = e.getActionCommand();

					if (user.equals(getUser())) { // 自分の名前
						// TODO RE前面に
					} else if (editorOpens.contains(user)) { // メンバーのエディタ開かれていたら
						// TODO そのメンバーのCHエディタを前面に
					} else if (!editorOpens.contains(user)) { // 開かれていなかったら
						// TODO そのメンバのCHエディタを開く
						addEditorOpens(user);
					}
				}
			});
		}
	}
	
	private void initWindowListener() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO 開いているCHエディタを閉じる
				editorOpens.clear();
				// TODO コネクションを切る
				component.fireWindowClosing();
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
	
	public void addEditorOpens(String user) {
		editorOpens.add(user);
	}
	
	public void removeEditorOpens(String user) {
		editorOpens.remove(user);
	}
	
	public void setComponent(CHComponent component) {
		this.component = component;
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
