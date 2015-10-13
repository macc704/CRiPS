/*
 * PrintTest.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.apps;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.jdt.core.dom.CompilationUnit;

import bc.BlockConverter;
import bc.classblockfilewriters.LangDefFilesReWriterMain;
import bc.j2b.analyzer.JavaToBlockAnalyzer;
import bc.j2b.model.CompilationUnitModel;
import bc.utils.ASTParserWrapper;
import bc.utils.ExtensionChanger;

/**
 * @author macchan
 */
public class JavaToBlockMain {

	private boolean isNewOpenBlocks = false;
	public static String LANG_DEF_PATH = "ext/block/lang_def.xml";
	public static String LANG_DEF_BASE_DIR = "ext/block/";

	public JavaToBlockMain() {

	}

	public JavaToBlockMain(boolean isNewOpenBlocks) {
		this.isNewOpenBlocks = isNewOpenBlocks;
	}

	public static void main(String[] args) throws Exception {
		JavaToBlockMain jtb = new JavaToBlockMain();
		jtb.process(new File("testcase/Test32.java"), "JISAutoDetect",
				new PrintStream(new File("testcase/Test32.xml")),
				new String[] {});
	}

	public String run(File file, String enc, String[] classpaths)
			throws Exception {
		String filePath = ExtensionChanger.changeToXmlExtension(file.getPath());

		File xml = new File(filePath);

		PrintStream hoge = new PrintStream(xml,BlockConverter.ENCODING_BLOCK_XML);
		process(file, enc, hoge, classpaths);
		return filePath;
	}

	public void process(File file, String enc, PrintStream out, String[] classpaths) throws Exception {
		// 言語定義ファイルの上書き
		LangDefFilesReWriterMain rewriter = new LangDefFilesReWriterMain(file,enc, classpaths, LANG_DEF_BASE_DIR);
		rewriter.rewrite();

		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classpaths);

		JavaToBlockAnalyzer visitor = new JavaToBlockAnalyzer(file, enc, rewriter.getAddedMethods(), rewriter.getAddedMethodsJavaType(), rewriter.getAddedClasses());
		unit.accept(visitor);

		CompilationUnitModel root = visitor.getCompilationUnit();
		root.print(out, 0, isNewOpenBlocks);

		out.close();
	}

}
