package blockEditorplugin.actions;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.io.File;

import javax.swing.SwingUtilities;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;

import pres.core.model.PRFileLog;
import pres.core.model.PRLog;
import presplugin.PresPlugin;
import presplugin.editors.PresExtendedJavaEditor;
import ronproeditor.helpers.CFrameUtils;
import ronproeditorplugin.Activator;
import a.slab.blockeditor.SBlockEditorListener;
import bc.BlockConverter;
import bc.apps.JavaToBlockMain;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.thread.CTaskManager;
import clib.common.thread.ICTask;
import controller.WorkspaceController;

public class BlockEditorManager {
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
		openBlockEditor(window);
	}

	public void openBlockEditor(IWorkbenchWindow window){
		if(isWorkspaceOpened()){
			CFrameUtils.toFront(blockEditor.getFrame());
			return;
		}
		man.start();
		man.setPriority(Thread.currentThread().getPriority() - 1);

		blockEditor = new WorkspaceController(IMAGES_PATH);
		blockEditor.setLangDefFilePath(LANG_DEF_PATH);
		blockEditor.loadFreshWorkspace();
		BlockEditorManager.window = window;
		window.getActivePage().getActiveEditor().getEditorSite()
				.getWorkbenchWindow().getPartService()
				.addPartListener(partListener);
		// エディタのテキストが保存されたら再読み込み
		ICommandService service = (ICommandService) Activator.getDefault()
				.getWorkbench().getService(ICommandService.class);
		service.addExecutionListener(saveListener);
		// タブの切り替えのリスナー登録
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private IExecutionListener saveListener = new IExecutionListener() {
		// eclipse上のsaveイベントリスナー

		public void preExecute(String commandId, ExecutionEvent event) {
			// TODO Auto-generated method stub

		}

		public void postExecuteSuccess(String commandId, Object returnValue) {
			// TODO Auto-generated method stub
			if (commandId.endsWith("org.eclipse.ui.file.save")) {
				if (isWorkspaceOpened()) {
					try {
						doCompileBlock();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		public void notHandled(String commandId, NotHandledException exception) {
			// TODO Auto-generated method stub

		}

		public void postExecuteFailure(String commandId,
				org.eclipse.core.commands.ExecutionException exception) {
			// TODO Auto-generated method stub

		}
	};

	private IPartListener partListener = new IPartListener() {

		public void partOpened(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
			// TODO Auto-generated method stub
		}

		public void partClosed(IWorkbenchPart part) {
			// TODO Auto-generated method stub
		}

		public void partBroughtToTop(IWorkbenchPart part) {
			// TODO Auto-generated method stub
		}

		public void partActivated(IWorkbenchPart part) {
			// TODO Auto-generated method stub
			if (isWorkspaceOpened() && part instanceof PresExtendedJavaEditor) {
				try {
					doCompileBlock();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	public void createAndShowGUI() throws CoreException {
		blockEditor.createAndShowGUI(blockEditor, new SBlockEditorListener() {

			public void blockConverted(File file) {
				writeBlockEditingLog(BlockEditorLog.SubType.BLOCK_TO_JAVA);
				Display.getDefault().asyncExec(new TextFormatAction(window));
				Display.getDefault().asyncExec(
						new OrganizedImportAction(window));

				// app.doRefreshCurrentEditor();
				// app.doFormat();
				// app.doBlockToJavaSave();
				// app.doCompileBlocking(true);
				// successMessageDialog();// TODO
				// dirty = false;
				//
				// openedTextEditor.setFocus();

			}

			public void blockDebugRun() {
				writeBlockEditingLog(BlockEditorLog.SubType.DEBUGRUN);
				// app.doDebugRun();
			}

			public void chengeInheritance(){
				writeBlockEditingLog(BlockEditorLog.SubType.INHERITANCE_CHANGED);
			}

			public void blockRun() {

				// エディタで開いているファイルを獲得する
				IFileEditorInput fileEditorInput = (IFileEditorInput) window
						.getActivePage().getActiveEditor().getEditorInput();
				IFile file = fileEditorInput.getFile();
				// Ruする

				writeBlockEditingLog(BlockEditorLog.SubType.RUN);

				IEditorInput editorInput = window.getActivePage()
						.getActiveEditor().getEditorInput();
				ITypeRoot root = (ITypeRoot) JavaUI
						.getEditorInputJavaElement(editorInput);

				IJavaElement elt;

				try {
					ILaunchConfigurationWorkingCopy wc;
					elt = root.getElementAt(ITypeRoot.JAVA_PROJECT);
					IJavaProject proj = elt.getJavaProject();

					DebugPlugin plugin = DebugPlugin.getDefault();
					ILaunchManager lm = plugin.getLaunchManager();

					ILaunchConfigurationType configType = lm
							.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

					wc = configType.newInstance(null, Activator.PLUGIN_ID);

					// プログラム実行時の設定を記述する
					wc.setAttribute(
							IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
							proj.getElementName());

					// パッケージ名取得

					wc.setAttribute(
							IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
							getPackageName(file)
									+ file.getName().substring(0,
											file.getName().indexOf(".")));

					wc.setAttribute(
							IJavaLaunchConfigurationConstants.ATTR_ALLOW_TERMINATE,
							true);

					//wc.setAttribute(IJavaLaunchConfigurationConstants.);

					//
					// IResource bin = null;
					// // binフォルダを探す
					// for (IResource resource :
					// file.getProject().members()) {
					// if (resource.getName().equals("bin")) {
					// bin = resource;
					// }
					// }
					//
					// IContainer binFile = (IContainer) bin;
					//
					// for (IResource packageFolder : binFile.members()) {OS
					// if (packageFolder.getName().equals(packageName)) {
					// bin = packageFolder;
					// binFile = (IContainer) bin;
					// }
					// }
					//
					//wc.setMappedResources(null);
					// // binフォルダ一覧表示
					// for (IResource resource : binFile.members()) {
					// System.out.println("file:" + resource.toString());
					// }
					ILaunchConfiguration config;
					try {
						config = wc.doSave();
						config.launch(ILaunchManager.RUN_MODE, null);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (JavaModelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			public void blockCompile() {
				writeBlockEditingLog(BlockEditorLog.SubType.COMPILE);
				// app.doCompile();
			}

			@Override
			public void toggleTraceLines(String state) {
				// TODO Auto-generated method stub
				writeBlockEditingLog(BlockEditorLog.SubType.TOGGLE_TRACELINES, state);
			}

			@Override
			public void saveAsJavaAndJS(File file) {
				// TODO Auto-generated method stub
				
			}

		}, ENCODING);
		blockEditor.getFrame().addWindowFocusListener(
				new WindowFocusListener() {
					public void windowLostFocus(WindowEvent e) {
						writeBlockEditingLog(BlockEditorLog.SubType.FOCUS_LOST);
					}

					public void windowGainedFocus(WindowEvent e) {
						writeBlockEditingLog(BlockEditorLog.SubType.FOCUS_GAINED);
					}
				});
		writeBlockEditingLog(BlockEditorLog.SubType.OPENED);
		blockEditor.getFrame().addWindowStateListener(
				new WindowStateListener() {
					public void windowStateChanged(WindowEvent e) {
						if (e.getNewState() == WindowEvent.WINDOW_CLOSED) {
							writeBlockEditingLog(BlockEditorLog.SubType.CLOSEED);
							ICommandService service = (ICommandService) Activator
									.getDefault().getWorkbench()
									.getService(ICommandService.class);
							service.removeExecutionListener(saveListener);
						} else if (e.getNewState() == WindowEvent.WINDOW_OPENED) {
							// do nothing
						}
					}
				});
		doCompileBlock();
	}

	private String getPackageName(IFile file) {
		String parentFolderName = file.getParent().getName();
		IEditorInput editorInput = window.getActivePage().getActiveEditor()
				.getEditorInput();
		ITypeRoot root = (ITypeRoot) JavaUI
				.getEditorInputJavaElement(editorInput);
		try {
			IJavaElement element = root.getElementAt(ITypeRoot.JAVA_PROJECT);
			IJavaProject project = element.getJavaProject();

			for (IPackageFragment pac : project.getPackageFragments()) {
				System.out.println("pacName:" + pac.getElementName());
				if (parentFolderName.equals(pac.getElementName())) {
					return parentFolderName + ".";
				}
			}

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	private boolean isWorkspaceOpened() {
		return blockEditor != null && blockEditor.getFrame() != null
				&& blockEditor.getFrame().isVisible();
	}

	public void setWindow(IWorkbenchWindow window) {
		BlockEditorManager.window = window;
	}

	public void doCompileBlock() throws CoreException {
		IEditorPart editorPart = window.getActivePage().getActiveEditor();
		final IFileEditorInput fileEditorInput = (IFileEditorInput) editorPart
				.getEditorInput();
		IFile file = fileEditorInput.getFile();
		final File target = file.getLocation().toFile();

		IResource resource = file;

		int max = resource.findMaxProblemSeverity(IMarker.PROBLEM, true,
				IResource.DEPTH_INFINITE);
		if (max != 2) {
			man.addTask(new ICTask() {

				public void doTask() {

					if (!isWorkspaceOpened()) {
						return;
					}
					if (!fileEditorInput.exists()) {
						doLockBlockEditor();
						return;
					}

					doRefleshBlock(target);
					// TODO Auto-generated method stub
				}
				// });
				// TODO Auto-generated method stub
			});
		} else {
			// if (message.length() != 0) {// has compile error
			// //
			writeBlockEditingLog(BlockEditorLog.SubType.JAVA_TO_BLOCK_ERROR);
			doCompileErrorBlockEditor(target);
			// return;
			// }

		}
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
					writeBlockEditingLog(BlockEditorLog.SubType.LOADING_START);
					// File javaFile = app.getSourceManager().getCurrentFile();
					String xmlFilePath = new JavaToBlockMain().run(javaFile,
							"SJIS", libs);

					blockEditor.setLangDefFilePath(javaFile.getParentFile()
							.getPath() + "/lang_def_project.xml");

					blockEditor.resetWorkspace();
					blockEditor.loadProjectFromPath(new File(xmlFilePath)
							.getPath());
					writeBlockEditingLog(BlockEditorLog.SubType.LOADING_END);
				} catch (Exception ex) {
					ex.printStackTrace();
					// CErrorDialog.show(app.getFrame(), "Block変換時のエラー", ex);
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

	private void writeBlockEditingLog(BlockEditorLog.SubType subType,
			String... texts) {
		try {
			// if (!app.getSourceManager().hasCurrentFile()) {
			// return;
			// }

			IEditorPart editorPart = window.getActivePage().getActiveEditor();
			final IFileEditorInput fileEditorInput = (IFileEditorInput) editorPart
					.getEditorInput();
			IFile file = fileEditorInput.getFile();
			CFile target = (CFile) CFileSystem.convertToCFile(file
					.getLocation().toFile());
			CDirectory project = new CDirectory(new CPath(file.getProject()
					.getProject().getLocation().toFile()));

			CPath path = target.getRelativePath(project);

			PRLog log = new BlockEditorLog(subType, path, texts);
			writePresLog(log, file);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void writePresLog(PRLog log, IFile file) {
		PresPlugin.getDefault().getPres().getManager()
				.getRecordingProject(file).record(log);
	}
}

class BlockEditorLog extends PRFileLog {
	public static enum Type implements PRLogType {
		BLOCK_COMMAND_RECORD
	};

	public static enum SubType implements PRLogSubType {
		ANY, BLOCK_TO_JAVA, BLOCK_TO_JAVA_ERROR, JAVA_TO_BLOCK, JAVA_TO_BLOCK_ERROR, COMPILE, RUN, DEBUGRUN, OPENED, CLOSEED, FOCUS_GAINED, FOCUS_LOST, LOADING_START, LOADING_END,INHERITANCE_CHANGED, TOGGLE_TRACELINES
	};

	/**
	 * Constructor
	 */
	public BlockEditorLog(SubType subType, CPath path, Object[] args) {
		super(Type.BLOCK_COMMAND_RECORD, subType, path, args);
	}
}
