package bc.apps;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ClassBlockFileModel.OutputSelDefClassPageModel;
import bc.b2j.analyzer.BlockToJavaAnalyzer;
import bc.b2j.model.JavaSourceWriter;
import bc.b2j.model.ProgramModel;
import bc.b2j.model.ResolveSyntaxError;
import bc.utils.DomParserWrapper;
import bc.utils.ExtensionChanger;

public class BlockToJavaMain {

	public static void convert(File openBlockXmlFile, String enc,
			String[] classpaths) throws Exception {

		Document document = DomParserWrapper.parse(openBlockXmlFile.getPath());
		System.out.println("document:" + openBlockXmlFile.getPath());

		BlockToJavaAnalyzer visitor = new BlockToJavaAnalyzer(
				openBlockXmlFile.getName());
		visitor.visit(document);

		ProgramModel root = visitor.getProgramModel();

		JavaSourceWriter writer = new JavaSourceWriter();

		File javaFile = new File(
				ExtensionChanger.changeToJavaExtension(openBlockXmlFile
						.getPath()));

		OutputSourceModel sourceModel = new OutputSourceModel(javaFile, enc,
				classpaths);
		File classDefFile = new File("ext/block/lang_def_genuses_hoge.xml");
		// 自作クラスブロック定義ファイル
		OutputSelDefClassPageModel selfDefModel = new OutputSelDefClassPageModel(
				classDefFile, enc, classpaths);

		selfDefModel.setSelDefClassModel(visitor.getSelDefClassModel());
		selfDefModel.print();

		// 既存のxmlに追加で書き出しする>>lang_def_menu_に追加書き出し

		Document menuDocument = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(new File("hoge.xml"));

		Element test = menuDocument.createElement("KANI");
		menuDocument.appendChild(test);
		System.out.println(menuDocument.getLastChild().toString());

		ResolveSyntaxError resolveError = new ResolveSyntaxError();

		resolveError.resolveError(root);

		writer.print(root, sourceModel);

		sourceModel.save();
	}
}
