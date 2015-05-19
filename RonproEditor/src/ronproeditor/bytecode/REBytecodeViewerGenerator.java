/*
 * REBytecodeViewerGenerator.java
 * Created on 2007/12/14 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.bytecode;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JDialog;

import ronproeditor.REApplication;
import ronproeditor.helpers.FileSystemUtil;
import ronproeditor.helpers.RECommandExecuter;
import ronproeditor.views.REFrame;

/**
 * REBytecodeViewerGenerator
 */
public class REBytecodeViewerGenerator {

	private static REBytecodeViewerGenerator INSTANCE = new REBytecodeViewerGenerator();
	private static final String JASMIN_TEMP = "TMP.J";

	public static void showViewer(File classFile, String className,
			REFrame frame) throws Exception {
		INSTANCE.showViewerInternal(classFile, className, frame);
	}

	private void showViewerInternal(File classFile, String className,
			REFrame frame) throws Exception {
		File jasmin = createJasmin(classFile, className, frame);
		File japa = createJapa(classFile, className, frame);
		File source = new File(classFile.getParent(), className + ".java");

		RELineRelationshipAnalyzer analyzer = new RELineRelationshipAnalyzer(
				source, jasmin);

		String jasminT = FileSystemUtil
				.load(jasmin, REApplication.SRC_ENCODING);
		String japaT = FileSystemUtil.load(japa, REApplication.SRC_ENCODING);
		String sourceT = analyzer.getSource();

		REBytecodeViewer viewer = new REBytecodeViewer();
		viewer.setTexts(sourceT, jasminT, japaT);
		viewer.setLineRelationship(analyzer.getLineRelationShip());
		showViewer(viewer, frame);
	}

	private void showViewer(REBytecodeViewer viewer, REFrame frame) {
		int margin = 20;
		JDialog dialog = new JDialog(frame, "Lesson Bytecode Viewer (Beta)",
				true);
		int w = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().width;
		int h = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().height;
		w -= margin * 2;
		h -= margin * 2;
		dialog.setBounds(margin, margin, w, h);
		dialog.getContentPane().add(viewer);
		dialog.setResizable(true);
		viewer.setDividerLocation(w);
		dialog.setVisible(true);
	}

	// String コマンド名 = "lib/d-java.exe -o jasmin " + クラス名 +
	// ".class > "+クラス名+".j";
	// 2007/12/12 現在，出力がDOSファイル名で出てしまうので，いったんTMPファイルへ出力して，
	// リネームするという方策を採る（とりあえずの処置）
	private File createJasmin(File classFile, String className, REFrame frame)
			throws Exception {
		File temp = new File(classFile.getParent(), JASMIN_TEMP);
		final File target = new File(classFile.getParent(), className + ".j");
		if (temp.exists()) {
			temp.delete();
		}
		if (target.exists()) {
			target.delete();
		}

		// {// Original D-java version
		// File file = new File("ext/bytecode/D-java.exe");
		// ArrayList<String> commands = new ArrayList<String>();
		// commands.add(file.getAbsolutePath());
		// commands.add("-o");
		// commands.add("jasmin");
		// commands.add(classFile.getName());
		// commands.add(">");
		// commands.add(JASMIN_TEMP);
		// CommandExecuter.executeCommandWait(commands,
		// classFile.getParentFile(), frame.getConsole());
		// temp.renameTo(target);
		// }

		// {// D-javaがx64でこけるので，javapで出来ないかテスト（途中） -> 失敗(japaがjasmin形式じゃないとダメ)
		// ArrayList<String> commands = new ArrayList<String>();
		// commands.add("javap");
		// commands.add("-l");
		// commands.add("-c");
		// commands.add(className);
		// CommandExecuter.executeCommandWait(commands,
		// classFile.getParentFile(), new DummyConsole() {
		// PrintStream p;
		//
		// public PrintStream getOut() {
		//
		// if (p == null) {
		// try {
		// p = new PrintStream(target);
		// } catch (Exception ex) {
		// }
		// }
		// return p;
		//
		// }
		// });
		// temp.renameTo(target);
		// }

		// {// Jasper version Jasperは出力形式に若干問題がある．
		// File jasper = new File("ext/bytecode/Jasper.jar");
		// ArrayList<String> commands = new ArrayList<String>();
		// commands.add("java");
		// commands.add("-jar");
		// commands.add("\"" + jasper.getAbsolutePath() + "\"");
		// commands.add(className);
		// CommandExecuter.executeCommandWait(commands,
		// classFile.getParentFile(), frame.getConsole());
		// }

		{// ClassFileAnalyzer version　GPLなので，その旨明示すること．論プロエディタはフリーなので問題なし．
			File jasper = new File("ext/bytecode/ClassFileAnalyzer.jar");
			ArrayList<String> commands = new ArrayList<String>();
			commands.add("java");
			commands.add("-jar");
			commands.add("\"" + jasper.getAbsolutePath() + "\"");
			commands.add("-file"); // Generate file in actual directory with
									// assembler source text.
			commands.add("-nopc"); // Omit pc label before mnemonic.
			commands.add(classFile.getName());
			RECommandExecuter.executeCommandWait(commands,
					classFile.getParentFile(), frame.getConsole(), frame.getConsole().getFontMetrics(frame.getConsole().getFont()));
		}

		return target;
	}

	// String コマンド = "java.exe -jar lib/japa.jar -j " + クラス名 + ".j";
	private File createJapa(File classFile, String className, REFrame frame)
			throws Exception {
		String jasminName = className + ".j";
		File target = new File(classFile.getParent(), className + ".japa");
		if (target.exists()) {
			target.delete();
		}

		File file = new File("ext/bytecode/japa.jar");
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("java");
		commands.add("-jar");
		commands.add(file.getAbsolutePath());
		commands.add("-j");
		commands.add("-o");
		commands.add(target.getName());
		commands.add(jasminName);
		RECommandExecuter.executeCommandWait(commands, classFile.getParentFile(),
				frame.getConsole(),frame.getConsole().getFontMetrics(frame.getConsole().getFont()));
		return target;
	}
}
