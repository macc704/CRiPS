package cocoviewer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CocoviewerAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		MessageDialog.openInformation(window.getShell(), "RonproEditorPlugin",
				"Hello, Eclipse world");
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		this.window = window;
	}

}
