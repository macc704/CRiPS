package ch.perspective.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class CHMemberSourceView extends ViewPart{

	private Text text;
	
	@Override
	public void createPartControl(Composite parent) {
		text = new Text(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		text.append("hoge");
	}

	@Override
	public void setFocus() {

	}
	
	public void showMemberSource(String source) {
		text.setText(source);
	}

}
