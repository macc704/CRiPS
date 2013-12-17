package presplugin.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

import presplugin.PresPlugin;

/**
 * PresExtendedJavaEditor
 */
@SuppressWarnings("restriction")
public class PresExtendedJavaEditor extends CompilationUnitEditor implements
		IPresEditor {

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		// attach historylistener (old code)
		// IOperationHistory history = PlatformUI.getWorkbench()
		// .getOperationSupport().getOperationHistory();
		// history.addOperationHistoryListener(historyListener);

		// attach DocumentListener (test code -> new code 2012/04/05)
		this.doc = getDocumentProvider().getDocument(input);
		this.doc.addDocumentListener(docListener);
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		// attach focuslistner
		SourceViewer viewer = (SourceViewer) getSourceViewer();
		StyledText textwidget = viewer.getTextWidget();
		textwidget.addFocusListener(focusListener);
	}

	@Override
	public void close(boolean save) {
		// detach historylistener
		// IOperationHistory history = PlatformUI.getWorkbench()
		// .getOperationSupport().getOperationHistory();
		// history.removeOperationHistoryListener(historyListener);

		if (this.doc != null) {
			this.doc.removeDocumentListener(docListener);
			this.doc = null;
		}

		// detach focuslistener
		SourceViewer viewer = (SourceViewer) getSourceViewer();
		StyledText textwidget = viewer.getTextWidget();
		textwidget.removeFocusListener(focusListener);

		super.close(save);
	}

	private IDocument doc;
	private IDocumentListener docListener = new IDocumentListener() {
		public void documentChanged(DocumentEvent event) {
			/*
			 * event.getLength(); Length of the replaced document text 削除した長さ
			 * event.getText()); text inserted into the document
			 */
			PresPlugin.getDefault().getPres()
					.handleDocumentEvent(PresExtendedJavaEditor.this, event);
		}

		public void documentAboutToBeChanged(DocumentEvent event) {
		}
	};

	// private IOperationHistoryListener historyListener = new
	// IOperationHistoryListener() {
	// public void historyNotification(OperationHistoryEvent event) {
	// IUndoableOperation op = event.getOperation();
	// // System.out.println(op);
	// IUndoContext context = getUndoContext();
	// if (context != null && op.hasContext(context)) {
	// PresPlugin
	// .getDefault()
	// .getPres()
	// .handleOperationEvent(PresExtendedJavaEditor.this,
	// event, op);
	// }
	// }
	// };

	private FocusListener focusListener = new FocusListener() {
		public void focusLost(FocusEvent e) {
			PresPlugin.getDefault().getPres()
					.handleFocusLost(PresExtendedJavaEditor.this);
		}

		public void focusGained(FocusEvent e) {
			PresPlugin.getDefault().getPres()
					.handleFocusGained(PresExtendedJavaEditor.this);
		}
	};

	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		PresPlugin.getDefault().getPres().handleSave(this);
	}

	/****************************************
	 * Helpers
	 ****************************************/

	public IFile getFile() {
		return ((FileEditorInput) getEditorInput()).getFile();
	}

	// private IUndoContext getUndoContext() {
	// if (getSourceViewer() instanceof ITextViewerExtension6) {
	// IUndoManager undoManager = ((ITextViewerExtension6) getSourceViewer())
	// .getUndoManager();
	// if (undoManager instanceof IUndoManagerExtension) {
	// IUndoContext context = ((IUndoManagerExtension) undoManager)
	// .getUndoContext();
	// return context;
	// }
	// }
	// return null;
	// }

}

// getViewer().getDocument().addDocumentListener(new IDocumentListener() {
// public void documentChanged(DocumentEvent event) {
// System.out.println("changed:"+event);
// }
//
// public void documentAboutToBeChanged(DocumentEvent event) {
// }
// });
