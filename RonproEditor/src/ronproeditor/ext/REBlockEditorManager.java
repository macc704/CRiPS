/*
 * REBlockEditorManager.java
 * Created on 2011/10/10
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ronproeditor.ext;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import a.slab.blockeditor.SBlockEditorListener;
import bc.BlockConverter;
import clib.common.filesystem.CPath;
import clib.common.thread.CTaskManager;
import clib.common.thread.ICTask;
import clib.view.dialogs.CErrorDialog;
import controller.WorkspaceController;
import net.unicoen.generator.JavaGenerator;
import net.unicoen.generator.JavaScriptGenerator;
import net.unicoen.mapper.JavaMapper;
import net.unicoen.mapper.JavaScriptMapper;
import net.unicoen.node.UniClassDec;
import net.unicoen.parser.blockeditor.BlockGenerator;
import net.unicoen.parser.blockeditor.BlockMapper;
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

	 private static final String LANG_DEF_PATH = "ext/blocks/lang_def_turtle.xml";
	// private static final String LANG_DEF_TURTLE_PATH =
	// "ext/block/lang_def_turtle.xml";
	 private static final String IMAGES_PATH = "ext/block/images/";
	private REApplication app;
	private WorkspaceController blockEditor;

	public REBlockEditorManager(REApplication app) {
		this.app = app;

		man.start();
		man.setPriority(Thread.currentThread().getPriority() - 1);

		app.getSourceManager().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (ICFwResourceRepository.DOCUMENT_OPENED.equals(evt.getPropertyName())) {
					doCompileBlock();
				} else {
					doLockBlockEditor();
				}
			}
		});
	}

	public void doOpenBlockEditorFromUni() {
		doOpenBlockEditor();
	}
	

	public void doOpenBlockEditor() {
		if (isWorkspaceOpened()) { // already opened
			CFrameUtils.toFront(blockEditor.getFrame());
			return;
		}

		blockEditor = new WorkspaceController(IMAGES_PATH);
		blockEditor.setLangDefFilePath(LANG_DEF_PATH);
		blockEditor.loadFreshWorkspace();
		blockEditor.createAndShowGUI(blockEditor, new SBlockEditorListener() {

			public void blockConverted(File file) {
				writeBlockEditingLog(BlockEditorLog.SubType.BLOCK_TO_JAVA);
				app.doRefreshCurrentEditor();
				app.doFormat();
				app.doBlockToJavaSave();
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

			@Override
			public void saveAsJavaAndJS(File file) {
				BlockMapper mapper = new BlockMapper();
				UniClassDec classDec = (UniClassDec) mapper.parse(file);

				try {
					outputFileFromUni(classDec, file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} 
				
			}
			
			public void outputFileFromUni(UniClassDec dec, File selectedFile) throws FileNotFoundException {
				File javaFile = new File(selectedFile.getParentFile().getPath() + File.separator + dec.className + ".java");
				PrintStream out = new PrintStream(javaFile);
				JavaGenerator.generate(dec, out);
				out.close();
				this.blockConverted(javaFile);

				File jsFile = new File(selectedFile.getParentFile().getPath() + File.separator + dec.className + ".js");
				out = new PrintStream(jsFile);
				JavaScriptGenerator.generate(dec, out);
				out.close();
				this.blockConverted(jsFile);
			}

		}, REApplication.SRC_ENCODING);
		
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
						} else if (e.getNewState() == WindowEvent.WINDOW_OPENED) {
							// do nothing
						}
					}
				});

		doCompileBlock();
	}

	private boolean isWorkspaceOpened() {
		return blockEditor != null && blockEditor.getFrame() != null && blockEditor.getFrame().isVisible();
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

				@SuppressWarnings("unused")
				String message = "default";
				try {
					message = app.doCompile2(false);
				} catch (Exception e) {
					e.printStackTrace();
				}

//				if (message.length() != 0) {// has compile error
//					writeBlockEditingLog(BlockEditorLog.SubType.JAVA_TO_BLOCK_ERROR);
//					doCompileErrorBlockEditor(target);
//					return;
//				}

				doRefleshBlock(target);
			}
		});
	}

	protected void doRefleshBlock(final File javaFile) {
		man.addTask(new ICTask() {

			public void doTask() {
				try {
					// xmlファイル生成
					writeBlockEditingLog(BlockEditorLog.SubType.LOADING_START);
					String filePath = app.getSourceManager().getCurrentFile().getPath();
					String xmlFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".xml";

					// file change
					File file = app.getSourceManager().getCurrentFile();
					File xmlFile = new File(xmlFilePath);

					blockEditor.resetWorkspace();

					PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(xmlFile)), false, "UTF-8");
					BlockGenerator generator = new BlockGenerator(out, "ext/blocks/");
					
					// 拡張子に応じて変換する
					if (file.getPath().endsWith(".java")) {
						JavaMapper mapper = new JavaMapper();
						generator.parse((UniClassDec) mapper.parseFile(javaFile.getPath()));
					} else if (file.getPath().endsWith(".js")) {
						JavaScriptMapper mapper = new JavaScriptMapper();
						generator.parse((UniClassDec) mapper.parseFile(javaFile.getPath()));
					}

					out.close();
					blockEditor.loadProjectFromPath(new File(xmlFilePath).getPath());

					writeBlockEditingLog(BlockEditorLog.SubType.LOADING_END);
				} catch (Exception ex) {
					ex.printStackTrace();
					CErrorDialog.show(app.getFrame(), "Block変換時のエラー", ex);
				}
			}
		});
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
