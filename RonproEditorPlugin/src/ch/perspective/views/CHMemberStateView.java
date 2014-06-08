package ch.perspective.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class CHMemberStateView extends ViewPart{

	@Override
	public void createPartControl(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("hoge");
	}

	@Override
	public void setFocus() {

	}

}
