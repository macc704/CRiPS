/*
 * PrintTest.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.apps;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ClassBlockFileModel.OutputSelDefClassPageModel;
import bc.BlockConverter;
import bc.j2b.analyzer.JavaToBlockAnalyzer;
import bc.j2b.model.CompilationUnitModel;
import bc.utils.ASTParserWrapper;
import bc.utils.DomParserWrapper;
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

		// ohata ブジェクトブロックの書き出し
		// オブジェクト変数ブロックのxmlファイルを作成する
		File classDefFile = new File(file.getParentFile().getPath()
				+ "/lang_def_geneses_"
				+ file.getName().substring(0, file.getName().indexOf('.'))
				+ ".xml");
		// menu情報のxmlを作成、（or追加)
		File projectMenuFile = new File(file.getParentFile().getPath()
				+ "/lang_def_menu_project.xml");

		OutputSelDefClassPageModel selfDefModel = new OutputSelDefClassPageModel(
				classDefFile, projectMenuFile, file.getName());

		// クラスのブロック情報を出力する
		selfDefModel.print();

		Document document = DomParserWrapper.parse(projectMenuFile.getPath());
		Element menuRoot = document.getDocumentElement();

		NodeList lists = menuRoot.getChildNodes();

		for (int i = 0; i < lists.getLength(); i++) {
			Node list = lists.item(i);
			System.out.println("listname" + list.getNodeName());
			if (list.getNodeName().equals("BlockDrawerSet")) {
				NodeList factry = list.getChildNodes();
				for (int j = 0; j < factry.getLength(); j++) {
					Node drawer = factry.item(j);
					if (drawer.getNodeName().equals("BlockDrawer")) {
						drawer.setTextContent("hoge");
					}
				}
			}

		}

		CompilationUnitModel root = visitor.getCompilationUnit();
		root.print(out, 0);
		out.close();
	}
}
