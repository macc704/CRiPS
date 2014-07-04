package ch.perspective.views;

import org.eclipse.swt.SWT;
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
import ch.library.CHFileSystem;
import clib.common.table.CCSVFileIO;

public class CHPreferenceView extends ViewPart {

	private Text userNameArea;
	private Text passArea;
	private Combo groupNumArea;

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
		userNameArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createPassArea(Composite parent, String pass) {
		Label passLabel = new Label(parent, SWT.NONE);
		passLabel.setText("Password");
		passArea = new Text(parent, SWT.PASSWORD | SWT.BORDER);
		passArea.setTextLimit(16);
		passArea.setText(pass);
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
	}

	private void createApplyButton(Composite parent) {
		Button applyBbutton = new Button(parent, SWT.PUSH);
		applyBbutton.setText("Apply");
		applyBbutton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String[][] table = new String[1][3];
				table[0][0] = userNameArea.getText();
				table[0][1] = passArea.getText();
				table[0][2] = groupNumArea.getText();
				CCSVFileIO.save(table, CHFileSystem.getPrefFile());
				System.out.println("Apply");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private Button connectButton;

	private void createConnectButton(Composite parent) {
		connectButton = new Button(parent, SWT.PUSH);
		connectButton.setText("Connect");
		connectButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (connectButton.getText().equals("Connect")) {
					new CheCoProManager(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow());
					connectButton.setText("Disconnect");
				} else if (connectButton.getText().equals("Disconnect")) {
					// TODO 切断処理
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	// 一時的に同期ボタンこちらに
	private void createSyncButton(Composite parent) {
		Button syncButton = new Button(parent, SWT.PUSH);
		syncButton.setText("FileRequest(temp)");
		syncButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

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

		// パスワード
		createPassArea(parent, table[0][1]);

		// グループ番号
		createGroupNumArea(parent, Integer.parseInt(table[0][2]));

		// 適用ボタン
		createApplyButton(parent);

		// 接続ボタン
		createConnectButton(parent);

		createSyncButton(parent);
	}

}
