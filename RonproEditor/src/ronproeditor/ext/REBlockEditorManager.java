/*
 * REBlockEditorManager.java
 * Created on 2011/10/10
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ronproeditor.ext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import bc.BlockConverter;
import bc.apps.JavaToBlockMain;
import clib.common.filesystem.CPath;
import clib.common.thread.CTaskManager;
import clib.common.thread.ICTask;
import clib.view.dialogs.CErrorDialog;
import edu.inf.shizuoka.blocks.extent.SBlockEditorListener;
import edu.inf.shizuoka.debugger.DebuggerWorkspaceController;
import edu.mit.blocks.controller.WorkspaceController;
import net.unicoen.node.UniClassDec;
import net.unicoen.parser.blockeditor.BlockGenerator;
import pres.core.model.PRFileLog;
import pres.core.model.PRLog;
import ronproeditor.ICFwResourceRepository;
import ronproeditor.REApplication;
import ronproeditor.helpers.CFrameUtils;

/**
 * @author macchan
 *
 */
public class REBlockEditorManager {

	// private static final String LANG_DEF_PATH = "ext/block/lang_def.xml";
	// private static final String LANG_DEF_TURTLE_PATH =
	// "ext/block/lang_def_turtle.xml";
	// private static final String IMAGES_PATH = "ext/block/images/";
	private REApplication app;
	private WorkspaceController blockEditor;
	private DebuggerWorkspaceController debugger;

	public REBlockEditorManager(REApplication app) {
		this.app = app;

		man.start();
		man.setPriority(Thread.currentThread().getPriority() - 1);
		// 凍結
		// Workspace.getInstance().addWorkspaceListener(new WorkspaceListener()
		// {
		// public void workspaceEventOccurred(WorkspaceEvent event) {
		// writeBlockEditingLog(BlockEditorLog.SubType.ANY,
		// event.toString());
		// }
		// });

		app.getSourceManager().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (/*
					 * ICFwResourceRepository.PREPARE_DOCUMENT_CLOSE
					 * .equals(evt.getPropertyName()) ||
					 */ICFwResourceRepository.DOCUMENT_OPENED.equals(evt.getPropertyName())
				/*
				 * || ICFwResourceRepository.MODEL_REFRESHED
				 * .equals(evt.getPropertyName())
				 */) {
					doCompileBlock();
				} else {
					doLockBlockEditor();
				}
			}
		});
	}

	public void doOpenBlockEditor() {
		initBlockEditor();

		doCompileBlock();
	}

	public void doOpenBlockEditorFromUni(UniClassDec classDec, String sourcePath) {
		initBlockEditor();

		doCompileBlockFromUni(classDec, sourcePath);
	}

	public void doOpenBlockEditorKeyaki() {
		initBlockEditor();

		blockEditor.setLangDefFilePath("ext/blocks/lang_def.xml");
		blockEditor.resetWorkspace();

		blockEditor.openBlockEditor();
	}

	public void doOpenDebuggerBlockEditor(UniClassDec classDec, String sourcePath) {
		initBlockEditor();

		initDebuggerBlockEditor(classDec, sourcePath);
	}

	public void doCompileBlockFromUni(UniClassDec classDec, String sourcePath) {
		try {
			File file = new File(sourcePath + classDec.className + ".xml");
			file.createNewFile();
			PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)), false, "UTF-8");

			BlockGenerator blockParser = new BlockGenerator(out, "ext/blocks/");
			blockParser.parse(classDec);

			// OpenBlock
			blockEditor.setLangDefFilePath("ext/blocks/lang_def.xml");
			blockEditor.resetWorkspace();

			blockEditor.openBlockEditor(file.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initDebuggerBlockEditor(UniClassDec classDec, String sourcePath) {
		if (isDebuggerBlockEditorOpened()) { // already opened
			CFrameUtils.toFront(debugger.getFrame());
			return;
		}

		try {
			debugger = new DebuggerWorkspaceController(classDec, "ext/blocks/lang_def.xml",
					new File(sourcePath + classDec.className + ".xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initBlockEditor() {
		if (isWorkspaceOpened()) { // already opened
			CFrameUtils.toFront(blockEditor.getFrame());
			return;
		}

		blockEditor = new WorkspaceController();

		// blockEditor = new WorkspaceController();
		// blockEditor.setLangDefFilePath(LANG_DEF_PATH);
		// blockEditor.loadFreshWorkspace();

		blockEditor.addBlockEditorListener(new SBlockEditorListener() {
			public void blockConverted(File file) {
				writeBlockEditingLog(BlockEditorLog.SubType.BLOCK_TO_JAVA);
				app.doRefreshCurrentEditor();
				app.doFormat();
				app.doBlockToJavaSave();
				// app.doCompileBlocking(true);
				// successMessageDialog();// TODO
				// dirty = false;
			}

			public void blockDebugRun() {
				writeBlockEditingLog(BlockEditorLog.SubType.DEBUGRUN);
				app.doDebugRun();
			}

			public void blockRun() {
				writeBlockEditingLog(BlockEditorLog.SubType.RUN);
				app.doRun();
			}

			public void blockCompile() {
				writeBlockEditingLog(BlockEditorLog.SubType.COMPILE);
				app.doCompile();
			}

			public void chengeInheritance() {
			}

			public void toggleTraceLines(String state) {
				writeBlockEditingLog(BlockEditorLog.SubType.TOGGLE_TRACELINES, state);
			}

		});
		// blockEditor.createAndShowGUI(new SBlockEditorListener() {
		// public void blockConverted(File file) {
		// writeBlockEditingLog(BlockEditorLog.SubType.BLOCK_TO_JAVA);
		// app.doRefreshCurrentEditor();
		// app.doFormat();
		// app.doBlockToJavaSave();
		// // app.doCompileBlocking(true);
		// // successMessageDialog();// TODO
		// // dirty = false;
		// }
		//
		// public void blockDebugRun() {
		// writeBlockEditingLog(BlockEditorLog.SubType.DEBUGRUN);
		// app.doDebugRun();
		// }
		//
		// public void blockRun() {
		// writeBlockEditingLog(BlockEditorLog.SubType.RUN);
		// app.doRun();
		// }
		//
		// public void blockCompile() {
		// writeBlockEditingLog(BlockEditorLog.SubType.COMPILE);
		// app.doCompile();
		// }
		//
		// public void chengeInheritance() {
		// }
		//
		// public void toggleTraceLines(String state) {
		// writeBlockEditingLog(BlockEditorLog.SubType.TOGGLE_TRACELINES,
		// state);
		// }
		//
		// });
		// blockEditor.getFrame().addWindowFocusListener(new
		// WindowFocusListener() {
		// public void windowLostFocus(WindowEvent e) {
		// writeBlockEditingLog(BlockEditorLog.SubType.FOCUS_LOST);
		// }
		//
		// public void windowGainedFocus(WindowEvent e) {
		// writeBlockEditingLog(BlockEditorLog.SubType.FOCUS_GAINED);
		// }
		// });
		writeBlockEditingLog(BlockEditorLog.SubType.OPENED);
		// blockEditor.getFrame().addWindowStateListener(new
		// WindowStateListener() {
		// public void windowStateChanged(WindowEvent e) {
		// if (e.getNewState() == WindowEvent.WINDOW_CLOSED) {
		// writeBlockEditingLog(BlockEditorLog.SubType.CLOSEED);
		// } else if (e.getNewState() == WindowEvent.WINDOW_OPENED) {
		// // do nothing
		// }
		// }
		// });
	}

	// private void successMessageDialog() {
	// // String selectButton[] = { "OK", "Java" };
	// //
	// // int select = JOptionPane.showOptionDialog(null,
	// // "BlockからJavaに変換しました。",
	// // "成功しました！！", JOptionPane.YES_NO_OPTION,
	// // JOptionPane.INFORMATION_MESSAGE, null, selectButton,
	// // selectButton[0]);
	// //
	// // if (select == 1) {
	// // toFront(app.getFrame());
	// // }
	// }

	private boolean isWorkspaceOpened() {
		return blockEditor != null && blockEditor.getFrame() != null && blockEditor.getFrame().isVisible();
	}

	private boolean isDebuggerBlockEditorOpened() {
		return debugger != null && debugger.getFrame() != null && debugger.getFrame().isVisible();
	}

	private CTaskManager man = new CTaskManager();

	public void doCompileBlock() {
		final File target = app.getSourceManager().getCurrentFile();
		man.addTask(new ICTask() {

			public void doTask() {

				if (!isWorkspaceOpened()) {
					return;
				}
				if (!app.getSourceManager().hasCurrentFile()) {
					doLockBlockEditor();
					return;
				}

				writeBlockEditingLog(BlockEditorLog.SubType.JAVA_TO_BLOCK);

				String message = "default";
				try {
					message = app.doCompile2(false);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (message.length() != 0) {// has compile error
					writeBlockEditingLog(BlockEditorLog.SubType.JAVA_TO_BLOCK_ERROR);
					doCompileErrorBlockEditor(target);
					return;
				}

				doRefleshBlock(target);
			}
		});
	}

	private void doCompileErrorBlockEditor(final File target) {
		// blockEditor.setState(WorkspaceController.COMPILE_ERROR);
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
					// blockEditor.setCompileErrorTitle(target.getName());
				} catch (Exception ex) {
				}
			}
		});
		// thread.setPriority(Thread.currentThread().getPriority() - 1);
		// thread.start();
	}

	protected void doRefleshBlock(final File javaFile) {
		// blockEditor.setState(WorkspaceController.BLOCK_SHOWING);
		// Thread thread = new Thread() {
		//
		// @Override
		// public void run() {
		man.addTask(new ICTask() {

			public void doTask() {
				try {
					// xmlファイル生成
					String[] libs = app.getLibraryManager().getLibsAsArray();
					writeBlockEditingLog(BlockEditorLog.SubType.LOADING_START);
					// File javaFile = app.getSourceManager().getCurrentFile();
					String xmlFilePath = new JavaToBlockMain().run(javaFile, REApplication.SRC_ENCODING, libs);

					blockEditor.setLangDefFilePath(javaFile.getParentFile().getPath() + "/lang_def_project.xml");

					// blockEditor.resetLanguage();
					// blockEditor.setLangDefDirty(true);
					blockEditor.resetWorkspace();
					blockEditor.loadProjectFromPath(new File(xmlFilePath).getPath());
					writeBlockEditingLog(BlockEditorLog.SubType.LOADING_END);
				} catch (Exception ex) {
					ex.printStackTrace();
					CErrorDialog.show(app.getFrame(), "Block変換時のエラー", ex);
				}
			}
		});
		// thread.setPriority(Thread.currentThread().getPriority() - 1);
		// thread.start();
	}

	protected boolean isTurtle() {
		return app.getSourceManager().getCCurrentFile().loadText().indexOf("extends Turtle") != -1;
	}

	private void doLockBlockEditor() {
		if (!isWorkspaceOpened()) {
			return;
		}

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
		blockEditorFile.append("<?xml version=\"1.0\" encoding=\"" + BlockConverter.ENCODING_BLOCK_XML + "\"?>");
		blockEditorFile.append("<CODEBLOCKS><Pages>");
		blockEditorFile.append("<Page page-name=\"BlockEditor\"" + " page-color=\" 40 40 40\" page-width=\"4000\""
				+ " page-infullview=\"yes\" page-drawer=\"NewClass\">");
		blockEditorFile.append("<PageBlocks></PageBlocks></Page></Pages></CODEBLOCKS>");
		return blockEditorFile.toString();
	}

	private String emptyBEFactoryPrint() {
		StringBuffer blockEditorFile = new StringBuffer();
		blockEditorFile.append("<?xml version=\"1.0\" encoding=\"" + BlockConverter.ENCODING_BLOCK_XML + "\"?>");
		blockEditorFile.append("<BlockLangDef>");
		blockEditorFile.append("<Pages drawer-with-page=\"yes\">");
		blockEditorFile.append("<Page page-name=\"BlockEditor\" page-width=\"400\"></Page>");
		blockEditorFile.append("</Pages>");
		blockEditorFile.append("</BlockLangDef>");
		return blockEditorFile.toString();
	}

	private void writeBlockEditingLog(BlockEditorLog.SubType subType, String... texts) {
		try {
			if (!app.getSourceManager().hasCurrentFile()) {
				return;
			}

			CPath path = app.getSourceManager().getCCurrentFile()
					.getRelativePath(app.getSourceManager().getCCurrentProject());
			PRLog log = new BlockEditorLog(subType, path, texts);
			app.writePresLog(log);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 20130926 DENOがBlockEditorを直接参照する設計は暫定
	public WorkspaceController getBlockEditor() {
		if (isWorkspaceOpened()) {
			return blockEditor;
		}
		return null;
	}

}

class BlockEditorLog extends PRFileLog {
	public static enum Type implements PRLogType {
		BLOCK_COMMAND_RECORD
	};

	public static enum SubType implements PRLogSubType {
		ANY, BLOCK_TO_JAVA, BLOCK_TO_JAVA_ERROR, JAVA_TO_BLOCK, JAVA_TO_BLOCK_ERROR, COMPILE, RUN, DEBUGRUN, OPENED, CLOSEED, FOCUS_GAINED, FOCUS_LOST, LOADING_START, LOADING_END, TOGGLE_TRACELINES
	};

	/**
	 * Constructor
	 */
	public BlockEditorLog(SubType subType, CPath path, Object[] args) {
		super(Type.BLOCK_COMMAND_RECORD, subType, path, args);
	}
}
