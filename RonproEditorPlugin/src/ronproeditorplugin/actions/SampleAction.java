package ronproeditorplugin.actions;

import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import a.slab.blockeditor.SBlockEditorListener;
import bc.BlockConverter;
import bc.apps.JavaToBlockMain;
import clib.common.thread.CTaskManager;
import clib.common.thread.ICTask;
import controller.WorkspaceController;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	private static final String LANG_DEF_PATH = "ext/block/lang_def.xml";
	private static final String LANG_DEF_TURTLE_PATH = "ext/block/lang_def_turtle.xml";
	private static final String IMAGES_PATH = "ext/block/images/";

	private IWorkbenchWindow window;
	private WorkspaceController blockEditor;

	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		MessageDialog.openInformation(
			window.getShell(),
			"RonproEditorPlugin",
			"Hello, Eclipse world");
		blockEditor = new WorkspaceController(IMAGES_PATH);
		blockEditor.setLangDefFilePath(LANG_DEF_PATH);
		blockEditor.loadFreshWorkspace();

		
		blockEditor.createAndShowGUI(blockEditor, new SBlockEditorListener() {

			public void blockConverted(File file) {
				// writeBlockEditingLog(BlockEditorLog.SubType.BLOCK_TO_JAVA);
				// app.doRefreshCurrentEditor();
				// app.doFormat();
				// app.doBlockToJavaSave();
				// app.doCompileBlocking(true);
				// successMessageDialog();// TODO
				// dirty = false;
			}

			public void blockDebugRun() {
				// writeBlockEditingLog(BlockEditorLog.SubType.DEBUGRUN);
				// app.doDebugRun();
			}

			public void blockRun() {
				// writeBlockEditingLog(BlockEditorLog.SubType.RUN);
				// app.doRun();
			}

			public void blockCompile() {
				// writeBlockEditingLog(BlockEditorLog.SubType.COMPILE);
				// app.doCompile();
			}

		}, "SJIS");
		blockEditor.getFrame().addWindowFocusListener(
				new WindowFocusListener() {


					@Override
					public void windowGainedFocus(java.awt.event.WindowEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void windowLostFocus(java.awt.event.WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
		// writeBlockEditingLog(BlockEditorLog.SubType.OPENED);
		blockEditor.getFrame().addWindowStateListener(
				new WindowStateListener() {


					@Override
					public void windowStateChanged(java.awt.event.WindowEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
	}

	
	private boolean isWorkspaceOpened() {
		return blockEditor != null && blockEditor.getFrame() != null
				&& blockEditor.getFrame().isVisible();
	}

	
	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	private CTaskManager man = new CTaskManager();

	public void doCompileBlock() {
		System.out.println(window.getActivePage().getActiveEditor().getTitle());
		IEditorPart editorPart = window.getActivePage().getActiveEditor();
		
		final IFileEditorInput fileEditorInput = (IFileEditorInput) editorPart.getEditorInput();
		IFile file = fileEditorInput.getFile();
		
		final File target = file.getFullPath().toFile();
	
		man.addTask(new ICTask() {

			public void doTask() {

				if (!isWorkspaceOpened()) {
					return;
				}
				if (!fileEditorInput.exists()) {
					doLockBlockEditor();
					return;
				}

			//	writeBlockEditingLog(BlockEditorLog.SubType.JAVA_TO_BLOCK);
				// app.doCompileBlocking(false);

				String message = "default";
				try {
//					message = app.doCompile2(false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (message.length() != 0) {// has compile error
//					writeBlockEditingLog(BlockEditorLog.SubType.JAVA_TO_BLOCK_ERROR);
					doCompileErrorBlockEditor(target);
					return;
				}


				doRefleshBlock(target);
				// TODO Auto-generated method stub
			}
			// });
			// TODO Auto-generated method stub
		});
	}
	
	protected void doRefleshBlock(final File javaFile) {
		blockEditor.setState(WorkspaceController.BLOCK_SHOWING);
		// Thread thread = new Thread() {
		//
		// @Override
		// public void run() {
		man.addTask(new ICTask() {

			public void doTask() {
				try {
					// xmlファイル生成
//					String[] libs = app.getLibraryManager().getLibsAsArray();
//					writeBlockEditingLog(BlockEditorLog.SubType.LOADING_START);
					// File javaFile = app.getSourceManager().getCurrentFile();
					String xmlFilePath = new JavaToBlockMain().run(javaFile,
							"SJIS", null);

					// BlockEditorに反映
					// lang def ファイル


						blockEditor.setLangDefFilePath(LANG_DEF_PATH);


					// blockEditor.resetLanguage();
					// blockEditor.setLangDefDirty(true);
					blockEditor.resetWorkspace();
					blockEditor.loadProjectFromPath(new File(xmlFilePath)
							.getPath());
	//				writeBlockEditingLog(BlockEditorLog.SubType.LOADING_END);
				} catch (Exception ex) {
					ex.printStackTrace();
		//			CErrorDialog.show(app.getFrame(), "Block変換時のエラー", ex);
				}
			}
		});
		// thread.setPriority(Thread.currentThread().getPriority() - 1);
		// thread.start();
	}

	
	private void doCompileErrorBlockEditor(final File target) {
		blockEditor.setState(WorkspaceController.COMPILE_ERROR);
		// Thread thread = new Thread() {
		//
		// @Override
		// public void run() {
		man.addTask(new ICTask() {

			public void doTask() {
				try {
					// xmlファイル生成
					String emptyWorkSpace = emptyBEWorkSpacePrint();
					String emptyFactory = emptyBEFactoryPrint();

					// BlockEditorに反映
					blockEditor.loadProject(emptyWorkSpace, emptyFactory);
					blockEditor.setCompileErrorTitle(target.getName());
				} catch (Exception ex) {
				}
			}
		});
		// thread.setPriority(Thread.currentThread().getPriority() - 1);
		// thread.start();
	}
	
	private void doLockBlockEditor() {
		if (!isWorkspaceOpened()) {
			return;
		}
		blockEditor.setState(WorkspaceController.PROJECT_SELECTED);
		// Thread thread = new Thread() {
		//
		// @Override
		// public void run() {
		man.addTask(new ICTask() {

			public void doTask() {
				try {
					// xmlファイル生成
					String emptyWorkSpace = emptyBEWorkSpacePrint();
					String emptyFactory = emptyBEFactoryPrint();

					// BlockEditorに反映
					blockEditor.loadProject(emptyWorkSpace, emptyFactory);
				} catch (Exception ex) {
				}
			}
		});
		// thread.setPriority(Thread.currentThread().getPriority() - 1);
		// thread.start();

	}

	private String emptyBEWorkSpacePrint() {
		StringBuffer blockEditorFile = new StringBuffer();
		blockEditorFile.append("<?xml version=\"1.0\" encoding=\""
				+ BlockConverter.ENCODING_BLOCK_XML + "\"?>");
		blockEditorFile.append("<CODEBLOCKS><Pages>");
		blockEditorFile.append("<Page page-name=\"BlockEditor\""
				+ " page-color=\" 40 40 40\" page-width=\"4000\""
				+ " page-infullview=\"yes\" page-drawer=\"NewClass\">");
		blockEditorFile
				.append("<PageBlocks></PageBlocks></Page></Pages></CODEBLOCKS>");
		return blockEditorFile.toString();
	}

	private String emptyBEFactoryPrint() {
		StringBuffer blockEditorFile = new StringBuffer();
		blockEditorFile.append("<?xml version=\"1.0\" encoding=\""
				+ BlockConverter.ENCODING_BLOCK_XML + "\"?>");
		blockEditorFile.append("<BlockLangDef>");
		blockEditorFile.append("<Pages drawer-with-page=\"yes\">");
		blockEditorFile
				.append("<Page page-name=\"BlockEditor\" page-width=\"400\"></Page>");
		blockEditorFile.append("</Pages>");
		blockEditorFile.append("</BlockLangDef>");
		return blockEditorFile.toString();
	}

}