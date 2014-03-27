package bc.apps;

import java.io.File;

import org.w3c.dom.Document;

import bc.BCSystem;
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
		BCSystem.out.println("document:" + openBlockXmlFile.getPath());
		
		BlockToJavaAnalyzer visitor = new BlockToJavaAnalyzer(openBlockXmlFile.getName());
		visitor.visit(document);

		ProgramModel root = visitor.getProgramModel();
		
		JavaSourceWriter writer = new JavaSourceWriter();	
		
		File javaFile = new File(
				ExtensionChanger.changeToJavaExtension(openBlockXmlFile
						.getPath()));
		
		OutputSourceModel sourceModel = new OutputSourceModel(javaFile, enc,
				classpaths);
		
		ResolveSyntaxError resolveError = new ResolveSyntaxError();
		
		resolveError.resolveError(root);
		
		writer.print(root, sourceModel);
		
		sourceModel.save();
	}
}
