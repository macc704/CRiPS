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
		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classpaths);
		JavaToBlockAnalyzer visitor = new JavaToBlockAnalyzer(file, enc);
		// unit.accept(new SimplePrintVisitor(System.out));
		unit.accept(visitor);
		// 言語定義ファイルの上書き
		LangDefFileReWriter rewriter = new LangDefFileReWriter();
		rewriter.Rewrite(file, enc, classpaths);

		CompilationUnitModel root = visitor.getCompilationUnit();
		root.print(out, 0);
		out.close();
	}
}
