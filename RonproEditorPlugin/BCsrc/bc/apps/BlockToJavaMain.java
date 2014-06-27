package bc.apps;

import java.io.File;

import org.w3c.dom.Document;

import bc.b2j.analyzer.BlockToJavaAnalyzer;
import bc.b2j.model.JavaSourceWriter;
import bc.b2j.model.ProgramModel;
import bc.b2j.model.ResolveSyntaxError;
import bc.classblockfilewriters.LangDefFilesReWriterMain;
import bc.utils.DomParserWrapper;
import bc.utils.ExtensionChanger;

public class BlockToJavaMain {

	public static void convert(File openBlockXmlFile, String enc,
			String[] classpaths) throws Exception {

		File javaFile = new File(
				ExtensionChanger.changeToJavaExtension(openBlockXmlFile
						.getPath()));

		// 言語定義ファイルの上書き
		LangDefFilesReWriterMain rewriter = new LangDefFilesReWriterMain(javaFile, enc,
				classpaths);
		rewriter.rewrite();

		Document document = DomParserWrapper.parse(openBlockXmlFile.getPath());

		BlockToJavaAnalyzer visitor = new BlockToJavaAnalyzer(
				openBlockXmlFile.getName());
		visitor.setProjectMethods(rewriter.getAddedMethods());
		visitor.visit(document);

		ProgramModel root = visitor.getProgramModel();

		JavaSourceWriter writer = new JavaSourceWriter();

		OutputSourceModel sourceModel = new OutputSourceModel(javaFile, enc,
				classpaths);

		ResolveSyntaxError resolveError = new ResolveSyntaxError();

		resolveError.resolveError(root);

		writer.print(root, sourceModel);

		sourceModel.save();
		//言語定義ファイルを再度書き換える
		rewriter.rewrite();
		
	}
}
