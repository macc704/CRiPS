/*
 * PrintTest.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.apps;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.jdt.core.dom.CompilationUnit;

import ClassBlockFileModel.LangDefFileReWriter;
import bc.BlockConverter;
import bc.j2b.analyzer.JavaToBlockAnalyzer;
import bc.j2b.model.CompilationUnitModel;
import bc.utils.ASTParserWrapper;
import bc.utils.ExtensionChanger;

/**
 * @author macchan
 */
public class JavaToBlockMain {

	public JavaToBlockMain() {

	}

	public static void main(String[] args) throws Exception {
		JavaToBlockMain jtb = new JavaToBlockMain();
		jtb.process(new File("testcase/Test32.java"), "JISAutoDetect",
				new PrintStream(new File("testcase/Test32.xml")),
				new String[] {});
	}

	// public String run(File file) throws Exception {
	// return run(file, "JISAutoDetect");
	// }

	public String run(File file, String enc, String[] classpaths)
			throws Exception {
		String filePath = ExtensionChanger.changeToXmlExtension(file.getPath());

		process(file, enc, new PrintStream(new File(filePath),
				BlockConverter.ENCODING_BLOCK_XML), classpaths);
		return filePath;
	}

	public void process(File file, String enc, PrintStream out,
			String[] classpaths) throws Exception {
		// 言語定義ファイルの上書き
		LangDefFileReWriter rewriter = new LangDefFileReWriter(file, enc,
				classpaths);
		rewriter.rewrite();

		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classpaths);
		JavaToBlockAnalyzer visitor = new JavaToBlockAnalyzer(file, enc,
				rewriter.getAddedMethods());

		unit.accept(visitor);

		CompilationUnitModel root = visitor.getCompilationUnit();
		root.print(out, 0);

		// JarFile obpro = getObpro(file);
		//
		// if (obpro != null) {
		// System.out.println(obpro.get);
		// }
		out.close();
	}

	// private JarFile getObpro(File file) {
	// File dir = file.getParentFile();
	// while (!(dir.isDirectory() && dir.getName().equals("testbase"))) {
	// dir = dir.getParentFile();
	// }
	// for (int i = 0; i < dir.listFiles().length; i++) {
	// if (dir.listFiles()[i].getName().equals("lib")) {
	// dir = dir.listFiles()[i];
	// JarFile obpro;
	// for (int j = 0; j < dir.listFiles().length; j++) {
	// if (dir.listFiles()[j].getName().equals("obpro.jar")) {
	// try {
	// obpro = new JarFile(dir.listFiles()[j]);
	// return obpro;
	// } catch (IOException e) {
	// System.out.println("obpro読み込みに失敗");
	// }
	// }
	// }
	// }
	// }
	// return null;
	// }
}
