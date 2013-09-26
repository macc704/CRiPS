package ronproeditor.views;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pres.core.model.PRCommandLog;
import pres.core.model.PRCommandLog.SubType;
import pres.core.model.PRTextEditLog;
import ronproeditor.REApplication;
import ronproeditor.helpers.FileSystemUtil;
import ronproeditor.source.IndentEngine;
import ronproeditor.source.NonWrappingTextPane;

public class RESourceEditor {

	enum State {
		NONE, CHANGING, INSERTING, DELETING;
	};

	private REApplication application;

	private RESourceViewer viewer;
	private boolean dirty = false;

	private LinkedList<UndoableEdit> undoStack = new LinkedList<UndoableEdit>();
	private LinkedList<UndoableEdit> redoStack = new LinkedList<UndoableEdit>();
	private CompoundEdit compoundEdit;

	private MyDocumentListener documentListener = new MyDocumentListener();

	public RESourceEditor(REApplication application) {
		this.application = application;

		initializeViewer();
		load();
		initializeListeners();
	}

	private void initializeViewer() {
		this.viewer = new RESourceViewer() {
			private static final long serialVersionUID = 1L;

			@Override
			protected JTextPane createTextPane() {
				return new NonWrappingTextPane() {
					private static final long serialVersionUID = 1L;

					public void cut() {
						RESourceEditor.this.application
								.writePresLog(PRCommandLog.SubType.CUT);
						super.cut();
					}

					public void paste() {
						RESourceEditor.this.application
								.writePresLog(PRCommandLog.SubType.PASTE);
						super.paste();
					}

					public void copy() {
						RESourceEditor.this.application
								.writePresLog(PRCommandLog.SubType.COPY);
						super.copy();
					}
				};
			}
		};
	}

	private void load() {
		setText(loadText());
	}

	private void initializeListeners() {

		JTextPane textPane = viewer.getTextPane();
		textPane.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				RESourceEditor.this.application
						.writePresLog(PRCommandLog.SubType.FOCUS_LOST);// TODO
			}

			public void focusGained(FocusEvent e) {
				RESourceEditor.this.application
						.writePresLog(PRCommandLog.SubType.FOCUS_GAINED);// TODO
			}
		});

		textPane.getDocument().addDocumentListener(documentListener);
		textPane.getDocument().addUndoableEditListener(documentListener);

		// Debuggerí‚é~
		// application.getDebugManager().addPropertyChangeListener(
		// new PropertyChangeListener() {
		// public void propertyChange(PropertyChangeEvent evt) {
		// JTextPane textArea = getTextPane();
		// String name = evt.getPropertyName();
		// if (name.equals(REDebugManager.BREAKPOINT_UPDATE)) {
		// viewer.revalidate();
		// viewer.repaint();
		// } else if (name.equals(REDebugManager.DEBUG_START)) {
		// JavaCodeDocument doc = (JavaCodeDocument) textArea
		// .getDocument();
		// doc.clearDebugPosition();
		// } else if (name.equals(REDebugManager.DEBUG_SUSPEND)) {
		// JavaCodeDocument doc = (JavaCodeDocument) textArea
		// .getDocument();
		// doc.applyDebugPosition(application
		// .getDebugManager().getSuspendedPosition());
		// } else if (name.equals(REDebugManager.DEBUG_RESUME)) {
		// JavaCodeDocument doc = (JavaCodeDocument) textArea
		// .getDocument();
		// doc.clearDebugPosition();
		// } else if (name.equals(REDebugManager.DEBUG_STOP)) {
		// JavaCodeDocument doc = (JavaCodeDocument) textArea
		// .getDocument();
		// doc.clearDebugPosition();
		// }
		// }
		// });
	}

	public RESourceViewer getViewer() {
		return viewer;
	}

	public String getText() {
		return viewer.getText();
	}

	public void setText(String text) {
		if (compoundEdit != null) {
			throw new RuntimeException("compoundEdit is not null for setText()");
		}
		if (text == null) {
			return;
		}
		if (text.equals(getText())) {
			return;
		}
		this.compoundEdit = new CompoundEdit();
		viewer.setText(text);
		this.compoundEdit.end();
		undoStack.push(this.compoundEdit);
		this.compoundEdit = null;
	}

	public List<Action> getActions() {
		JTextPane textPane = getViewer().getTextPane();
		return Arrays.asList(textPane.getActions());
	}

	public UndoableEdit undoableEdit() {
		return undoStack.peek();
	}

	public UndoableEdit redoableEdit() {
		return redoStack.peek();
	}

	protected void doUndo() {
		application.writePresLog(PRCommandLog.SubType.UNDO);
		UndoableEdit edit = undoStack.removeFirst();
		edit.undo();
		redoStack.addFirst(edit);
		application.getFrame().editorStateChanged();
	}

	protected void doRedo() {
		application.writePresLog(PRCommandLog.SubType.REDO);
		UndoableEdit edit = redoStack.removeFirst();
		edit.redo();
		undoStack.addFirst(edit);
		application.getFrame().editorStateChanged();
	}

	public boolean isDirty() {
		return dirty;
	}

	private void setDirty(boolean dirty) {
		if (this.dirty != dirty) {
			this.dirty = dirty;
			application.getFrame().editorStateChanged();
		}
	}

	// TODO âºÇÃé¿ëï
	public void refresh() {
		if (isDirty()) {
			JOptionPane.showMessageDialog(application.getFrame(), "ïsê≥Ç»çXêV",
					"ï“èWÇ≥ÇÍÇƒÇ¢Ç‹Ç∑", JOptionPane.ERROR_MESSAGE);
			return;
		}
		setText(loadText());
	}

	private String loadText() {
		File file = application.getSourceManager().getCurrentFile();
		return FileSystemUtil.load(file, REApplication.SRC_ENCODING);
	}

	public void doSave() {
		File file = application.getSourceManager().getCurrentFile();
		FileSystemUtil.save(file, getViewer().getText());
		setDirty(false);
	}

	class MyDocumentListener implements DocumentListener, UndoableEditListener {

		private State state = State.NONE;

		public void changedUpdate(DocumentEvent e) {
			state = State.CHANGING;
			// setDirty(true); // fontÇïœÇ¶ÇΩÇ∆Ç´Ç…dirtyÇ…Ç»Ç¡ÇƒÇµÇ‹Ç§ÅD
		}

		public void insertUpdate(DocumentEvent e) {
			try {
				int offset = e.getOffset();
				int length = e.getLength();
				Document doc = e.getDocument();
				String text = doc.getText(offset, length);
				application.writePresTextEditLog(PRTextEditLog.SubType.INSERT,
						offset, length, text);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			state = State.INSERTING;
			setDirty(true);
		}

		public void removeUpdate(DocumentEvent e) {
			try {
				int offset = e.getOffset();
				int length = e.getLength();
				application.writePresTextEditLog(PRTextEditLog.SubType.DELETE,
						offset, length, null);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			state = State.DELETING;
			setDirty(true);
		}

		public void undoableEditHappened(UndoableEditEvent evt) {
			setDirty(true);
			switch (state) {
			case INSERTING:
			case DELETING:
				if (compoundEdit != null) {
					compoundEdit.addEdit(evt.getEdit());
				} else {
					undoStack.addFirst(evt.getEdit());
				}
				application.getFrame().editorStateChanged();
				break;
			default:
				break;
			}
			state = State.NONE;
		}

	}

	public void format() {
		String text = getText();
		String newText = IndentEngine.execIndent(text);
		if (!text.equals(newText)) {
			application.writePresLog(SubType.START_FORMAT);
			// getViewer().getTextPane().getDocument().addDocumentListener(documentListener);//2012.10.10äÎåØÇ»ÇÃÇ≈àÍíUÉÑÉÅ
			setText(newText);
			// getViewer().getTextPane().getDocument().addDocumentListener(documentListener);//2012.10.10äÎåØÇ»ÇÃÇ≈àÍíUÉÑÉÅ
			application.writePresLog(SubType.END_FORMAT);
		}
	}

}
