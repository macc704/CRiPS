package ch.perspective.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class CHPreferenceView extends ViewPart{

	@Override
	public void createPartControl(Composite parent) {
		
		setComponents(parent);
	}

	@Override
	public void setFocus() {
	}
	
	public void setComponents(Composite parent){
		
		parent.setLayout(new GridLayout(2,true));
		
		// ユーザ名
		Label userLabel = new Label(parent, SWT.NONE);
		userLabel.setText("UserName");
		Text userNameArea = new Text(parent, SWT.SINGLE | SWT.BORDER);
		userNameArea.setTextLimit(16);
		userNameArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// パスワード
		Label passLabel = new Label(parent, SWT.NONE);
		passLabel.setText("Password");
		Text passArea = new Text(parent, SWT.PASSWORD | SWT.BORDER);
		passArea.setTextLimit(16);
		passArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// グループ番号
		Label groupNumLabel = new Label(parent, SWT.NONE);
		groupNumLabel.setText("GroupNumber");
		Combo groupNumArea = new Combo(parent, SWT.READ_ONLY);
		for(int i=0; i<51; i++){
			groupNumArea.add(Integer.toString(i));
		}
	}

}
