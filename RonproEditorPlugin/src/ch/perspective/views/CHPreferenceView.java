package ch.perspective.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import ch.actions.CheCoProManager;
import ch.conn.framework.packets.CHLogoutRequest;
import ch.library.CHFileSystem;
import clib.common.table.CCSVFileIO;

public class CHPreferenceView extends ViewPart {

	private Text userNameArea;
	private Text passArea;
	private Combo groupNumArea;
	private CheCoProManager manager;

	private Button connectButton;
	private Button applyBbutton;

	private String user;

	private String password;

	@Override
	public void createPartControl(Composite parent) {

		setComponents(parent);
	}

	@Override
	public void setFocus() {
	}

	private String[][] loadPrefFile() {
		String[][] table = new String[1][3];
		table = CCSVFileIO.load(CHFileSystem.getPrefFile());
		if (table.length == 0) {
			table = new String[1][3];
			table[0][0] = "";
			table[0][1] = "";
			table[0][2] = "0";
		}
		return table;
	}

	private void createUserNameArea(Composite parent, String user) {
		Label userLabel = new Label(parent, SWT.NONE);
		userLabel.setText("UserName");
		userNameArea = new Text(parent, SWT.SINGLE | SWT.BORDER);
		userNameArea.setTextLimit(16);
		userNameArea.setText(user);

		userNameArea.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				applyBbutton.setEnabled(true);
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		userNameArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createPassArea(Composite parent, String pass) {
		Label passLabel = new Label(parent, SWT.NONE);
		passLabel.setText("Password");
		passArea = new Text(parent, SWT.PASSWORD | SWT.BORDER);
		passArea.setTextLimit(16);
		passArea.setText(pass);

		passArea.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				applyBbutton.setEnabled(true);
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		passArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createGroupNumArea(Composite parent, int groupNum) {
		Label groupNumLabel = new Label(parent, SWT.NONE);
		groupNumLabel.setText("GroupNumber");
		groupNumArea = new Combo(parent, SWT.READ_ONLY);
		for (int i = 0; i < 51; i++) {
			groupNumArea.add(Integer.toString(i));
		}
		groupNumArea.select(groupNum);

		groupNumArea.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				applyBbutton.setEnabled(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createApplyButton(Composite parent) {
		applyBbutton = new Button(parent, SWT.PUSH);
		applyBbutton.setText("Apply");
		applyBbutton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String[][] table = new String[1][3];
				table[0][0] = userNameArea.getText();
				table[0][1] = passArea.getText();
				table[0][2] = groupNumArea.getText();
				CCSVFileIO.save(table, CHFileSystem.getPrefFile());
				if (!table[0][0].equals("") && !table[0][1].equals("")) {
					connectButton.setEnabled(true);
				} else {
					connectButton.setEnabled(false);
				}
				user = table[0][0];
				password = table[0][1];
				applyBbutton.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createConnectButton(Composite parent) {
		connectButton = new Button(parent, SWT.PUSH);
		connectButton.setText("Login");
		connectButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (connectButton.getText().equals("Login")) {
					manager = new CheCoProManager(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow());
					userNameArea.setEditable(false);
					passArea.setEditable(false);
					groupNumArea.setEnabled(false);
					connectButton.setText("Logout");
				} else if (connectButton.getText().equals("Logout")) {
					if (manager != null) {
						manager.getConn().write(
								new CHLogoutRequest(manager.getUser()));
					}
					userNameArea.setEditable(true);
					passArea.setEditable(true);
					groupNumArea.setEnabled(true);
					connectButton.setText("Login");
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void setComponents(Composite parent) {

		String[][] table = loadPrefFile();

		parent.setLayout(new GridLayout(2, true));

		// ユーザ名
		createUserNameArea(parent, table[0][0]);
		user = table[0][0];

		// パスワード
		createPassArea(parent, table[0][1]);
		password = table[0][1];

		// グループ番号
		createGroupNumArea(parent, Integer.parseInt(table[0][2]));

		// 適用ボタン
		createApplyButton(parent);

		// 接続ボタン
		createConnectButton(parent);
		if (!table[0][0].equals("") && !table[0][1].equals("")) {
			connectButton.setEnabled(true);
		} else {
			connectButton.setEnabled(false);
		}
	}

	public void isLogined(boolean login) {
		if (login) {
			connectButton.setText("Logout");
		} else {
			connectButton.setText("Login");
		}
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
