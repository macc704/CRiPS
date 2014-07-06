package presplugin.adapter;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.commands.ICommandService;

import pres.core.model.PRCommandLog;
import pres.core.model.PREclipseTextEditLog;
import pres.core.model.PRProjectLog;
import pres.core.model.PRTextEditLog;
import presplugin.editors.IPresEditor;
import clib.common.filesystem.CPath;

public class EclipsePresAdapter {

	private PresProjectManager manager = new PresProjectManager();

	private volatile IPresEditor lastEditor;
	private volatile IPresEditor currentEditor;

	public EclipsePresAdapter() {
	}

	public PresProjectManager getManager() {
		return this.manager;
	}

	public void initialize() {
		this.initializeEventHandlers();
		manager.initialize();
	}

	public void terminate() {
		manager.terminate();
	}

	/**************************************
	 * Event Handling Management
	 **************************************/

	private void initializeEventHandlers() {
		// 2012/06/21 ファイル追加時のメカニズム
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				try {
					event.getDelta().accept(new IResourceDeltaVisitor() {
						public boolean visit(IResourceDelta delta) {
							if (delta.getKind() == IResourceDelta.ADDED) {
								if (delta.getFullPath().toString()
										.endsWith(".java")
										&& delta.getResource() instanceof IFile) {
									recordActionCommand(
											PRCommandLog.SubType.FILE_CREATED,
											(IFile) delta.getResource());
									handleSave((IFile) delta.getResource());
								}
							}
							if (delta.getKind() == IResourceDelta.REMOVED) {
								if (delta.getFullPath().toString()
										.endsWith(".java")
										&& delta.getResource() instanceof IFile) {
									recordActionCommand(
											PRCommandLog.SubType.FILE_DELETED,
											(IFile) delta.getResource());
								}
							}
							return true;
						}
					});
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}, IResourceChangeEvent.POST_CHANGE);

		// Run情報の受信設定
		DebugPlugin.getDefault().addDebugEventListener(
				new IDebugEventSetListener() {
					public void handleDebugEvents(DebugEvent[] events) {
						for (int i = 0; i < events.length; i++) {
							try {
								handleDebugEvent(events[i]);
							} catch (Exception ex) {
								// do nothing
								// ex.printStackTrace();
							}
						}
					}
				});

		// Commandの発火情報の受信設定
		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getAdapter(ICommandService.class);
		commandService.addExecutionListener(new IExecutionListener() {
			public void preExecute(String commandId, ExecutionEvent event) {
				try {
					if (commandId.equals("org.eclipse.ui.file.export")) {
						refresh();
					}
				} catch (Exception ex) {
					// do nothing
				}
			}

			public void postExecuteSuccess(String commandId, Object returnValue) {
				try {
					handleCommandEvent(commandId, returnValue);
				} catch (Exception ex) {
					// do nothing
				}
			}

			public void postExecuteFailure(String commandId,
					ExecutionException exception) {
			}

			public void notHandled(String commandId,
					NotHandledException exception) {
			}
		});
	}

	/**************************************
	 * Recording Interface
	 **************************************/

	public void handleSave(IPresEditor editor) {
		handleSave(editor.getFile());
	}

	public void handleSave(IFile file) {
		recordActionCommand(PRCommandLog.SubType.SAVE, file);
		recordActionCommand(PRCommandLog.SubType.COMPILE, file);
		// TODO コピーしたファイルをマネージするのに必要だが，プロジェクト以下すべてコピーしてしまうので保留
		// manager.getRecordingProject(editor.getFile()).checkTargetFilesAndUpdate();
	}

	public void handleFocusGained(IPresEditor editor) {
		this.currentEditor = editor;
		this.lastEditor = editor;
		recordActionCommand(PRCommandLog.SubType.FOCUS_GAINED, editor.getFile());
	}

	public void handleFocusLost(IPresEditor editor) {
		recordActionCommand(PRCommandLog.SubType.FOCUS_LOST, editor.getFile());
		this.currentEditor = null;
	}

	private void handleDebugEvent(DebugEvent evt) {
		Object src = evt.getSource();
		if (src instanceof RuntimeProcess) {
			RuntimeProcess p = (RuntimeProcess) src;
			ILaunch launch = (ILaunch) p.getAdapter(ILaunch.class);
			ParsedRunConfiguration info = new ParsedRunConfiguration(
					launch.getLaunchConfiguration());
			if ((evt.getKind() & DebugEvent.CREATE) == DebugEvent.CREATE) {
				recordActionCommand(PRCommandLog.SubType.START_RUN,
						info.getResourceFile());
			} else if ((evt.getKind() & DebugEvent.TERMINATE) == DebugEvent.TERMINATE) {
				recordActionCommand(PRCommandLog.SubType.STOP_RUN,
						info.getResourceFile());
			} else {
				// do nothing
			}

		} else if (src instanceof IJavaDebugTarget/* JDIDebugTarget */) {
			IJavaDebugTarget target = (IJavaDebugTarget) src;
			ILaunch launch = target.getLaunch();
			ParsedRunConfiguration info = new ParsedRunConfiguration(
					launch.getLaunchConfiguration());
			if ((evt.getKind() & DebugEvent.CREATE) == DebugEvent.CREATE) {
				recordActionCommand(PRCommandLog.SubType.START_DEBUG,
						info.getResourceFile());
			} else if ((evt.getKind() & DebugEvent.TERMINATE) == DebugEvent.TERMINATE) {
				recordActionCommand(PRCommandLog.SubType.STOP_DEBUG,
						info.getResourceFile());
			} else {
				// do nothing
			}

		}
	}

	private void handleCommandEvent(String commandId, Object returnValue)
			throws Exception {
		IPresEditor currentEditorCopy = this.currentEditor;
		IPresEditor lastEditorCopy = this.lastEditor;
		PRCommandLog.SubType subType = convertToPresActionCommand(commandId);
		if (subType != null && currentEditorCopy != null) {
			recordActionCommand(subType, currentEditorCopy.getFile(), commandId);
		} else if (subType != null && lastEditorCopy != null) {
			// TODO 現状，ひとまず，lastEditorへの操作とみなす
			manager.getRecordingProject(lastEditorCopy.getFile()).record(
					new PRProjectLog(PRProjectLog.SubType.EXTENDED, commandId,
							returnValue));
		}
		// else if (subType == null && lastEditorCopy != null) {
		// // TODO 現状，ひとまず，lastEditorへの操作とみなす
		// manager.getRecordingProject(lastEditorCopy.getFile()).record(
		// new PRProjectLog(PRProjectLog.SubType.EXTENDED, commandId,
		// returnValue));
		// }
		else {
			// do nothing
		}
	}

	/**
	 * use handleDocumentEvent
	 */
	@Deprecated
	public void handleOperationEvent(IPresEditor editor,
			OperationHistoryEvent event, IUndoableOperation op) {
		boolean typing = ParsedTypingHistory.isTypingOperation(op);
		PREclipseTextEditLog.SubType subType = convertToPresTextEditCommand(event);
		if (typing && subType != null) {
			IFile file = editor.getFile();
			ParsedTypingHistory history = new ParsedTypingHistory(op.toString());
			manager.getRecordingProject(file).record(
					new PREclipseTextEditLog(subType, getCPath(file), history
							.getStart(), history.getEnd(), history.getText(),
							history.getPreservedText()));
		}
	}

	public void handleDocumentEvent(IPresEditor editor, DocumentEvent event) {
		IFile file = editor.getFile();
		manager.getRecordingProject(file).record(
				new PRTextEditLog(PRTextEditLog.SubType.ECLIPSE,
						getCPath(file), event.getOffset(), event.getLength(),
						event.getText()));
	}

	/**************************************
	 * Recording Sub
	 **************************************/

	private void recordActionCommand(PRCommandLog.SubType subType, IFile file,
			Object... args) {
		manager.getRecordingProject(file).record(
				new PRCommandLog(subType, getCPath(file), args));
	}

	private CPath getCPath(IFile file) {
		return new CPath(file.getProjectRelativePath().toString());
	}

	public void refresh() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench.getWorkbenchWindowCount() >= 1) {
			IWorkbenchWindow window = workbench.getWorkbenchWindows()[0];
			RefreshAction action = new RefreshAction(window);
			action.run();
		}
	}

	public PRCommandLog.SubType convertToPresActionCommand(String commandId) {
		if (commandId == null) {
			return null;
		} else if (commandId
				.equals("org.eclipse.jdt.debug.ui.localJavaShortcut.run")) {
			return null;
		} else if (commandId
				.equals("org.eclipse.jdt.debug.ui.localJavaShortcut.debug")) {
			return null;
		} else if (commandId.equals("org.eclipse.ui.file.save")) {
			return null;
		} else if (commandId.equals("org.eclipse.ui.edit.delete")) {
			return PRCommandLog.SubType.DELETE;
		} else if (commandId.equals("org.eclipse.ui.edit.cut")) {
			return PRCommandLog.SubType.CUT;
		} else if (commandId.equals("org.eclipse.ui.edit.copy")) {
			return PRCommandLog.SubType.COPY;
		} else if (commandId.equals("org.eclipse.ui.edit.paste")) {
			return PRCommandLog.SubType.PASTE;
		} else if (commandId.equals("org.eclipse.ui.edit.undo")) {
			return PRCommandLog.SubType.UNDO;
		} else if (commandId.equals("org.eclipse.ui.edit.redo")) {
			return PRCommandLog.SubType.REDO;
		}

		// default:
		return PRCommandLog.SubType.EXTENDED;
	}

	public PREclipseTextEditLog.SubType convertToPresTextEditCommand(
			OperationHistoryEvent event) {
		switch (event.getEventType()) {
		case OperationHistoryEvent.OPERATION_ADDED:
			return PREclipseTextEditLog.SubType.CREATE;
		case OperationHistoryEvent.OPERATION_CHANGED:
			return PREclipseTextEditLog.SubType.CHANGE;
		case OperationHistoryEvent.REDONE:
			return PREclipseTextEditLog.SubType.REDONE;
		case OperationHistoryEvent.UNDONE:
			return PREclipseTextEditLog.SubType.UNDONE;
		default:
			return null;
		}
	}

}
