package blockEditorplugin.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

public class OrganizedImportAction implements Runnable{

	private IWorkbenchWindow window;

	public OrganizedImportAction(IWorkbenchWindow window){
		this.window = window;
	}

	public void run(){
		OrganizeImportsAction org = new OrganizeImportsAction(window.getActivePage().getActiveEditor().getEditorSite());
		try {
			IFileEditorInput fileEditorInput = (IFileEditorInput) (window
					.getActivePage().getActiveEditor().getEditorInput());
			IFile tmpFile = fileEditorInput.getFile();
			IResource resource = tmpFile;

			IJavaProject prj = null;
			if (resource.getProject().hasNature(
					"org.eclipse.jdt.core.javanature")) {
				prj = JavaCore.create(resource.getProject());
			}

			IStructuredSelection selection = new StructuredSelection(
					prj);
			org.run(selection);
			//save
			IEditorPart editorPart = window.getActivePage().getActiveEditor();
			ITextEditor textEditor = (ITextEditor) editorPart;
			textEditor.doSave(null);

		} catch (CoreException ce) {
			ce.printStackTrace();
		}

	}
}
