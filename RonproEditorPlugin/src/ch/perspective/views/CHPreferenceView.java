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
import org.eclipse.ui.part.ViewPart;

import ch.library.CHFileSystem;
import clib.common.table.CCSVFileIO;

public class CHPreferenceView extends ViewPart{
	
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
	
	public void setComponents(Composite parent){
		
		String[][] table = new String[1][3];
		table = CCSVFileIO.load(CHFileSystem.getPrefFile());
		if(table.length == 0){
			table = new String[1][3];
			table[0][0] = "";
			table[0][1] = "";
			table[0][2] = "0";
		}
		
		parent.setLayout(new GridLayout(2,true));
		
		// ユーザ名
		Label userLabel = new Label(parent, SWT.NONE);
		userLabel.setText("UserName");
		userNameArea = new Text(parent, SWT.SINGLE | SWT.BORDER);
		userNameArea.setTextLimit(16);
		userNameArea.setText(table[0][0]);
		userNameArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// パスワード
		Label passLabel = new Label(parent, SWT.NONE);
		passLabel.setText("Password");
		passArea = new Text(parent, SWT.PASSWORD | SWT.BORDER);
		passArea.setTextLimit(16);
		passArea.setText(table[0][1]);
		passArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// グループ番号
		Label groupNumLabel = new Label(parent, SWT.NONE);
		groupNumLabel.setText("GroupNumber");
		groupNumArea = new Combo(parent, SWT.READ_ONLY);
		for(int i=0; i<51; i++){
			groupNumArea.add(Integer.toString(i));
		}
		groupNumArea.select(Integer.parseInt(table[0][2]));
		
		// 適用ボタン
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

}
