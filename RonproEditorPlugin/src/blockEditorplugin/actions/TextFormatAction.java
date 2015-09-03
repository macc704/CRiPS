package blockEditorplugin.actions;

import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

public class TextFormatAction implements Runnable {
	
	private IWorkbenchWindow window;
	
	public TextFormatAction(IWorkbenchWindow window){
		this.window = window;
	}
	
	public void run() {
		
		// TODO Auto-generated method stub
		IEditorPart editorPart = window.getActivePage()
				.getActiveEditor();
		ITextEditor textEditor = (ITextEditor) editorPart;
		ITextOperationTarget target = (ITextOperationTarget) textEditor
				.getAdapter(ITextOperationTarget.class);
		textEditor.doRevertToSaved();
		target.doOperation(ISourceViewer.FORMAT);
		textEditor.doSave(null);
	}

}
