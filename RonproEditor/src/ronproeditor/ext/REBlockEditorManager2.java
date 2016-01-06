/*
 * REBlockEditorManager.java
 * Created on 2011/10/10
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ronproeditor.ext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

import bc.BlockConverter;
import bc.apps.JavaToBlockMain;
import bc.classblockfilewriters.LangDefFilesReWriterMain2;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.thread.CTaskManager;
import clib.common.thread.ICTask;
import clib.view.dialogs.CErrorDialog;
import edu.mit.blocks.controller.WorkspaceController;
import net.unicoen.mapper.JavaMapper;
import net.unicoen.mapper.JavaScriptMapper;
import net.unicoen.node.UniArg;
import net.unicoen.node.UniClassDec;
import net.unicoen.node.UniMemberDec;
import net.unicoen.node.UniMethodDec;
import net.unicoen.parser.blockeditor.BlockGenerator;
import pres.core.model.PRLog;
import ronproeditor.IREResourceRepository;
import ronproeditor.REApplication;
import ronproeditor.helpers.CFrameUtils;

/**
 * for New BlockEditor 2015.08.14
 *
 * @author macchan
 *
 */
public class REBlockEditorManager2 {

	public static final String LANG_DEF_BASE_DIR = "ext/block2/";
	public static String BLOCK_ENC = "UTF-8";

	private REApplication app;
	private WorkspaceController blockEditor;
	private BiFunction<File, REApplication, String> convertionAction;

	public void doOpenNewBlockEditor() {
		// Java->Block変換処理
		BiFunction<File, REApplication, String> convertAction = (sourceFile, app) -> {
			File srcfile = app.getSourceManager().getCurrentFile();
			File dir = srcfile.getParentFile();
			File tmpSrcFile = createUTFDummyFile(srcfile);

			UniClassDec classDec = convertJavaToUni(tmpSrcFile);
			//TODO mapperが完成次第消す
			if(isTurtle()){
				classDec.superClass = Lists.newArrayList("Turtle");
				for(UniMemberDec dec : classDec.members){
					if(dec instanceof UniMethodDec){
						UniMethodDec method = (UniMethodDec)dec;
						if(method.methodName.equals("main")){
							method.args = new ArrayList<>();
							method.args.add(new UniArg("String[]", "args"));
						}
					}
				}
			}
			
			File xmlfile = new File(dir.getPath() + "/" + classDec.className + ".xml");
			try {
				xmlfile.createNewFile();
				PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(xmlfile)), false, BLOCK_ENC);
				BlockGenerator blockParser = new BlockGenerator(out, sourceFile.getParent() + "/");
				blockParser.parse(classDec);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (RuntimeException e){
				e.printStackTrace();
			}finally{
				tmpSrcFile.delete();				
			}

			return xmlfile.getAbsolutePath();
		};

		// Managerの初期化処理
		Function<File, WorkspaceController> initBlockEditorAction = (sourceFile) -> {
			WorkspaceController blockEditor = new WorkspaceController();
			if(sourceFile != null){
				// プロジェクトを解析して、言語定義ファイルを書き換える
				LangDefFilesReWriterMain2 rewriter = new LangDefFilesReWriterMain2(sourceFile, REApplication.SRC_ENCODING, new String[] {}, REBlockEditorManager2.LANG_DEF_BASE_DIR);
				try {
					rewriter.rewrite();
				} catch (Exception e) {
					e.printStackTrace();
				}

				blockEditor.setLangDefFilePath(sourceFile.getParent() + "/" + "lang_def_project.xml");
			}else{
					blockEditor.setLangDefFilePath(LANG_DEF_BASE_DIR + "lang_def.xml");
			}

			blockEditor.loadFreshWorkspace();
			blockEditor.createAndShowGUI();

			return blockEditor;
		};

		doOpenBlockEditor(initBlockEditorAction, convertAction);
	}

	public File createUTFDummyFile(File srcfile){
		File tmpSrcFile = new File(srcfile.getParent() + "/" + ".tmp" + srcfile.getName());
		try {
			FileInputStream fs = new FileInputStream(srcfile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fs, REApplication.SRC_ENCODING));

			FileOutputStream fo = new FileOutputStream(tmpSrcFile);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fo, BLOCK_ENC));
			String convertedText = "";
			String line = reader.readLine();
			while(line != null){
				convertedText += new String(line.getBytes(BLOCK_ENC)) + System.lineSeparator();
				line = reader.readLine();
			}
			bw.write(convertedText);
			reader.close();
			bw.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tmpSrcFile;
	}

	public void doOpenSemiNewBlockEditor() {
		// Java->Block変換処理
		BiFunction<File, REApplication, String> convertAction = (javaFile, app) -> {
			String[] libs = app.getLibraryManager().getLibsAsArray();
			String xmlFilePath = "noxml";
			try {
				xmlFilePath = new JavaToBlockMain().run(javaFile, REApplication.SRC_ENCODING, libs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return xmlFilePath;
		};

		// Managerの初期化処理
		Function<File, WorkspaceController> initBlockEditorAction = (javaFile) -> {
			WorkspaceController blockEditor = new WorkspaceController();
			blockEditor.setLangDefFilePath(REBlockEditorManager.LANG_DEF_PATH);
			blockEditor.loadFreshWorkspace();
			blockEditor.createAndShowGUI();
			return blockEditor;
		};

		doOpenBlockEditor(initBlockEditorAction, convertAction);
	}

	public REBlockEditorManager2(REApplication app) {
		this.app = app;
		man.start();
		man.setPriority(Thread.currentThread().getPriority() - 1);

		app.getSourceManager().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (IREResourceRepository.DOCUMENT_OPENED.equals(evt.getPropertyName())) {
					if(blockEditor != null && blockEditor.isOpened()){
						blockEditor.resetWorkspace();
						rewriteLangdefFile();
					}
					doCompileBlock();
				}
			}
		});
	}

	public void rewriteLangdefFile(){
		File currentFile = app.getSourceManager().getCurrentFile();
		if(isJavaFile(currentFile)){
			LangDefFilesReWriterMain2 rewriter = new LangDefFilesReWriterMain2(currentFile, REApplication.SRC_ENCODING, new String[] {}, REBlockEditorManager2.LANG_DEF_BASE_DIR);
			try {
				rewriter.rewrite();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isJavaFile(File file){
		return file.getName().endsWith(".java");
	}

	public void doOpenBlockEditor(Function<File, WorkspaceController> initAction, BiFunction<File, REApplication, String> convertionAction) {
		if (isWorkspaceOpened()) { // already opened
			CFrameUtils.toFront(blockEditor.getFrame());
			return;
		}
		blockEditor = initAction.apply(app.getSourceManager().getCurrentFile());
		this.convertionAction = convertionAction;
		blockEditor.addBlockEditorListener(new edu.inf.shizuoka.blocks.extent.SBlockEditorListener() {

			@Override
			public void blockConverted(File file) {
				writeBlockEditingLog(REBlockEditorLog.SubType.BLOCK_TO_JAVA);
				app.doRefreshCurrentEditor();
				app.doFormat();
				app.doBlockToJavaSave();
			}

			@Override
			public void blockDebugRun() {
				writeBlockEditingLog(REBlockEditorLog.SubType.DEBUGRUN);
				app.doDebugRun();
			}

			@Override
			public void blockRun() {
				writeBlockEditingLog(REBlockEditorLog.SubType.RUN);
				app.doRun();
			}

			@Override
			public void blockCompile() {
				writeBlockEditingLog(REBlockEditorLog.SubType.COMPILE);
				app.doCompile();
			}

			@Override
			public void chengeInheritance() {
			}

			@Override
			public void toggleTraceLines(String state) {
				writeBlockEditingLog(REBlockEditorLog.SubType.TOGGLE_TRACELINES, state);
			}

			@Override
			public void newFileCreated() {
				File file = app.getSourceManager().getCurrentFile();
				app.doRefresh();
				app.doOpen(file);
			}

		});

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
		writeBlockEditingLog(REBlockEditorLog.SubType.OPENED);
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

		doCompileBlock();
	}

	private boolean isWorkspaceOpened() {
		return blockEditor != null && blockEditor.getFrame() != null && blockEditor.getFrame().isVisible();
	}

	private boolean isOpenableTextFile(File file){
		if(file == null){
			return false;
		}
		String ext = CFileSystem.convertToCFile(file).getName().getExtension();
		if (!(ext.equals("java") || ext.equals("js"))) {
			return false;
		}

		return true;
	}

	private CTaskManager man = new CTaskManager();

	public void doCompileBlock() {
		final File target = app.getSourceManager().getCurrentFile();
		if(!isOpenableTextFile(target)){
			return;
		}
		man.addTask(new ICTask() {

			@Override
			public void doTask() {

				if (!isWorkspaceOpened()) {
					return;
				}
				if (!app.getSourceManager().hasCurrentFile()) {
					doLockBlockEditor();
					return;
				}

				writeBlockEditingLog(REBlockEditorLog.SubType.JAVA_TO_BLOCK);
				// app.doCompileBlocking(false);

				String message = "";
				if (app.getSourceManager().getCCurrentFile().getName().getExtension().equals("java")) {
					try {
						message = app.doCompileInternally(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (message.length() != 0) {// has compile error
					writeBlockEditingLog(REBlockEditorLog.SubType.JAVA_TO_BLOCK_ERROR);
					doCompileErrorBlockEditor(target);
					return;
				}

				doRefleshBlock(target);
			}
		});
	}

	private void doCompileErrorBlockEditor(final File target) {
		man.addTask(new ICTask() {

			@Override
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
	}

	protected void doRefleshBlock(final File javaFile) {
		man.addTask(new ICTask() {
			@Override
			public void doTask() {
				try {
					writeBlockEditingLog(REBlockEditorLog.SubType.LOADING_START);

					String xmlFilePath = convertionAction.apply(javaFile, app);

					if (xmlFilePath.equals("noxml")) {
						throw new RuntimeException();
					}

					blockEditor.setSelectedFile(new File(xmlFilePath));
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							blockEditor.loadProjectFromPath(xmlFilePath);
							writeBlockEditingLog(REBlockEditorLog.SubType.LOADING_END);
						}
					});

				} catch (Exception ex) {
					ex.printStackTrace();
					CErrorDialog.show(app.getFrame(), "Block変換時のエラー", ex);
				}
			}
		});
	}

	/*
	 * JavaをUnicoenモデルへ変換して返す
	 */
	public UniClassDec convertJavaToUni(File file) {
		if (file.getPath().endsWith(".java")) {
			JavaMapper mapper = new JavaMapper();
			Object node = mapper.parseFile(file.getPath());

			if (node instanceof UniClassDec) {
				return (UniClassDec) node;
			} else {
				CErrorDialog.show(null, "UniClassモデルが作成できませんでした");
				return null;
			}
		} else if (file.getPath().endsWith(".js")) {
			JavaScriptMapper mapper = new JavaScriptMapper();
			Object node = mapper.parseFile(file.getPath());
			if (node instanceof UniClassDec) {
				return (UniClassDec) node;
			} else {
				CErrorDialog.show(null, "UniClassモデルが作成できませんでした");
				return null;
			}
		}
		throw new RuntimeException("unknown file type");
	}

	protected boolean isTurtle() {
		return app.getSourceManager().getCCurrentFile().loadText().indexOf("extends Turtle") != -1;
	}

	private void doLockBlockEditor() {
		if (!isWorkspaceOpened()) {
			return;
		}
		man.addTask(new ICTask() {

			@Override
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
		blockEditorFile.append("<Page page-name=\"BlockEditor\"" + " page-color=\" 40 40 40\" page-width=\"4000\"" + " page-infullview=\"yes\" page-drawer=\"NewClass\">");
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

	private void writeBlockEditingLog(REBlockEditorLog.SubType subType, String... texts) {
		try {
			if (!app.getSourceManager().hasCurrentFile()) {
				return;
			}

			CPath path = app.getSourceManager().getCCurrentFile().getRelativePath(app.getSourceManager().getCCurrentProject());
			PRLog log = new REBlockEditorLog(subType, path, texts);
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
