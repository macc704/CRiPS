package blockEditorplugin.actions;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.io.File;

import javax.swing.SwingUtilities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import a.slab.blockeditor.SBlockEditorListener;
import bc.BlockConverter;
import bc.apps.JavaToBlockMain;
import clib.common.thread.CTaskManager;
import clib.common.thread.ICTask;
import controller.WorkspaceController;

public class BlockEditorManager implements IAdaptable {
	private static final String LANG_DEF_PATH = "ext/block/lang_def.xml";
	// private static final String LANG_DEF_TURTLE_PATH =
	// "ext/block/lang_def_turtle.xml";
	private static final String IMAGES_PATH = "ext/block/images/";
	public static final String LIB_FOLDER = "lib";

	private static final String ENCODING = "SJIS";
	private WorkspaceController blockEditor;
	private CTaskManager man = new CTaskManager();

	private static IWorkbenchWindow window;
	

	public BlockEditorManager(IWorkbenchWindow window) {
		man.start();
		man.setPriority(Thread.currentThread().getPriority() - 1);

		blockEditor = new WorkspaceController(IMAGES_PATH);
		blockEditor.setLangDefFilePath(LANG_DEF_PATH);
		blockEditor.loadFreshWorkspace();
		this.window = window;
		//タブの切り替えのリスナー登録
		window.getActivePage().getActiveEditor().getEditorSite().getWorkbenchWindow().getPartService().addPartListener(partListener);
		//エディタのテキストが保存されたら再読み込み
		IEditorPart editorPart = window.getActivePage().getActiveEditor();
		editorPart.addPropertyListener(ipListener);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
				
			}
		});
		
		
	}
	
	private IPartListener partListener = new IPartListener() {
		
		@Override
		public void partOpened(IWorkbenchPart part) {
			// TODO Auto-generated method stub
			System.out.println("hogeoopen");
		}
		
		@Override
		public void partDeactivated(IWorkbenchPart part) {
			// TODO Auto-generated method stub
			System.out.println("hoged");
		}
		
		@Override
		public void partClosed(IWorkbenchPart part) {
			// TODO Auto-generated method stub
			System.out.println("hogec");
		}
		
		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			// TODO Auto-generated method stub
			System.out.println("hoget");
		}
		
		@Override
		public void partActivated(IWorkbenchPart part) {
			// TODO Auto-generated method stub
			if(isWorkspaceOpened()){
				doCompileBlock();	
			}
			
		}
	};
	
	private IPropertyListener ipListener = new IPropertyListener() {
		
		@Override
		public void propertyChanged(Object source, int propId) {
			// TODO Auto-generated method stub
			System.out.println(propId);
		}
	};

	private ISelectionListener listener = new ISelectionListener() {
		
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			// TODO Auto-generated method stub
			IWorkbenchPart h = window.getActivePage().getActivePart();
			if(!window.getActivePage().getActivePart().equals(part)){
				System.out.println("hoge");	
				
			}
			
		}
	};
	
	public void createAndShowGUI() {
		blockEditor.createAndShowGUI(blockEditor, new SBlockEditorListener() {

			public void blockConverted(File file) {
				// writeBlockEditingLog(BlockEditorLog.SubType.BLOCK_TO_JAVA);
				// app.doRefreshCurrentEditor();
				// app.doFormat();
				// app.doBlockToJavaSave();
				// app.doCompileBlocking(true);
				// successMessageDialog();// TODO
				// dirty = false;
				//
				// openedTextEditor.setFocus();
				// IEditorPart editorPart = window.getActivePage()
				// .getActiveEditor();
				//
				// ITextEditor textEditor = (ITextEditor) editorPart;
				// openedTextEditor = textEditor;
				// ITextOperationTarget target = (ITextOperationTarget)
				// textEditor
				// .getAdapter(ITextOperationTarget.class);
				//
				// target.doOperation(ISourceViewer.FORMAT);

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

		}, ENCODING);
		blockEditor.getFrame().addWindowFocusListener(
				new WindowFocusListener() {

					@Override
					public void windowGainedFocus(WindowEvent e) {
						// TODO Auto-generated method stub
						// System.out.println("hoge");
					}

					@Override
					public void windowLostFocus(WindowEvent e) {
						// TODO Auto-generated method stub
						// System.out.println("hogelost");
					}
				});
		// writeBlockEditingLog(BlockEditorLog.SubType.OPENED);
		blockEditor.getFrame().addWindowStateListener(
				new WindowStateListener() {
					@Override
					public void windowStateChanged(WindowEvent e) {
						// TODO Auto-generated method stub

					}
				});

		doCompileBlock();
	}

	private boolean isWorkspaceOpened() {
		return blockEditor != null && blockEditor.getFrame() != null
				&& blockEditor.getFrame().isVisible();
	}

	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
	}

	public void doCompileBlock() {
		IEditorPart editorPart = window.getActivePage().getActiveEditor();
		final IFileEditorInput fileEditorInput = (IFileEditorInput) editorPart
				.getEditorInput();
		IFile file = fileEditorInput.getFile();
		final File target = file.getLocation().toFile();

		man.addTask(new ICTask() {

			public void doTask() {

				if (!isWorkspaceOpened()) {
					return;
				}
				if (!fileEditorInput.exists()) {
					doLockBlockEditor();
					return;
				}

				// writeBlockEditingLog(BlockEditorLog.SubType.JAVA_TO_BLOCK);
				// app.doCompileBlocking(false);

				// String message = "default";

				// if (message.length() != 0) {// has compile error
				// //
				// writeBlockEditingLog(BlockEditorLog.SubType.JAVA_TO_BLOCK_ERROR);
				// doCompileErrorBlockEditor(target);
				// return;
				// }

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
					String[] libs = { "lib/blib.jar" };
					// writeBlockEditingLog(BlockEditorLog.SubType.LOADING_START);
					// File javaFile = app.getSourceManager().getCurrentFile();
					String xmlFilePath = new JavaToBlockMain().run(javaFile,
							"SJIS", libs);

					blockEditor.setLangDefFilePath(LANG_DEF_PATH);

					blockEditor.resetWorkspace();
					blockEditor.loadProjectFromPath(new File(xmlFilePath)
							.getPath());
					// writeBlockEditingLog(BlockEditorLog.SubType.LOADING_END);
				} catch (Exception ex) {
					ex.printStackTrace();
					// CErrorDialog.show(app.getFrame(), "Block変換時のエラー", ex);
				}
			}
		});
		// thread.setPriority(Thread.currentThread().getPriority() - 1);
		// thread.start();
	}

	// private void doCompileErrorBlockEditor(final File target) {
	// blockEditor.setState(WorkspaceController.COMPILE_ERROR);
	// // Thread thread = new Thread() {
	// //
	// // @Override
	// // public void run() {
	// man.addTask(new ICTask() {
	//
	// public void doTask() {
	// try {
	// // xmlファイル生成
	// String emptyWorkSpace = emptyBEWorkSpacePrint();
	// String emptyFactory = emptyBEFactoryPrint();
	//
	// // BlockEditorに反映
	// blockEditor.loadProject(emptyWorkSpace, emptyFactory);
	// blockEditor.setCompileErrorTitle(target.getName());
	// } catch (Exception ex) {
	// }
	// }
	// });
	// // thread.setPriority(Thread.currentThread().getPriority() - 1);
	// // thread.start();
	// }

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

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		if(adapter.equals((WorkspaceController.class))){
			return blockEditor;
		}
		return null;
	}
}
