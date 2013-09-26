/*
 * PrintTest.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package test.j2b;

import java.io.File;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.jdt.core.dom.CompilationUnit;

import bc.utils.ASTParserWrapper;

/**
 * @author macchan
 */
public class PrintTest {

	public static void main(String[] args) throws Exception {
		new PrintTest().run();
	}

	void run() throws Exception {
		File dir = new File("testcase");
		JFileChooser loadFilechooser = new JFileChooser(dir);
		final FileFilter filterJavaExtention = new FileNameExtensionFilter(
				"JAVAファイル(*.java)", "java");
		loadFilechooser.addChoosableFileFilter(filterJavaExtention);

		int loadSelected = loadFilechooser.showOpenDialog(loadFilechooser);
		if (loadSelected == JFileChooser.APPROVE_OPTION) {
			File loadFile = loadFilechooser.getSelectedFile();
			File javaFile = new File(loadFile.getPath());
			CompilationUnit unit = ASTParserWrapper.parse(javaFile,
					"JISAutoDetect", new String[] {});

			// ASTPrinter.printNode(unit, 0);
			SimplePrintVisitor visitor01 = new SimplePrintVisitor();
			unit.accept(visitor01);

			JFileChooser saveFilechooser = new JFileChooser(dir);
			final FileFilter filterXmlExtention = new FileNameExtensionFilter(
					"XMLファイル(*.xml)", "xml");
			saveFilechooser.addChoosableFileFilter(filterXmlExtention);

			int saveSelected = saveFilechooser.showSaveDialog(saveFilechooser);
			if (saveSelected == JFileChooser.APPROVE_OPTION) {
				File xmlFile = new File(saveFilechooser.getSelectedFile()
						.getPath());
				PrintStream ps = new PrintStream(xmlFile);
				SimplePrintVisitor visitor02 = new SimplePrintVisitor(ps);
				unit.accept(visitor02);
			}
		}
	}
}
