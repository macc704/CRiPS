package ch.perspective.views;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.part.ViewPart;

import ch.conn.framework.CHUserState;

public class CHMemberStateView extends ViewPart{

	Composite parent;
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		this.parent.setLayout(new RowLayout(SWT.VERTICAL));
	}

	@Override
	public void setFocus() {

	}
	
	public void setUserStates(List<CHUserState> userStates){
		removeObjects();
		Label onlineLabel = new Label(parent, SWT.NONE);
		onlineLabel.setForeground(new Color(parent.getDisplay(), 0, 255, 0));
		onlineLabel.setText("online");
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		for(CHUserState userState : userStates){
			if(userState.isLogin()){
				Label user = new Label(parent, SWT.NONE);
				user.setText(userState.getUser());
			}
		}
		Label offlineLabel = new Label(parent, SWT.NONE);
		offlineLabel.setForeground(new Color(parent.getDisplay(), 255, 0, 0));
		offlineLabel.setText("offline");
		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		for(CHUserState userState : userStates){
			if(!userState.isLogin()){
				Label user = new Label(parent, SWT.NONE);
				user.setText(userState.getUser());
			}
		}
		parent.layout();
	}
	
	public void removeObjects(){
		for(Widget widget : parent.getChildren()){
			widget.dispose();
		}
	}

}
