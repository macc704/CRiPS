package ClassBlockFileModel;

import java.io.File;

import org.eclipse.jdt.core.dom.CompilationUnit;

import bc.j2b.analyzer.MethodAnalyzer;
import bc.utils.ASTParserWrapper;
import clib.common.filesystem.CFile;

public class LangDefFileReWriter {

	public void Rewrite(File file, String enc, String[] classpaths)
			throws Exception {
		// ohata ブジェクトブロックの書き出し
		// オブジェクト変数ブロックのxmlファイルを作成する
		File classDefFile = new File(file.getParentFile().getPath()
				+ "/lang_def_genuses_project.xml");
		// menu情報のxmlを作成、（or追加)

		File projectMenuFile = new File(file.getParentFile().getPath()
				+ "/lang_def_menu_project.xml");

		// 同じディレクトリ内のすべてのjavaファイルをパースし、モデルに追加する
		OutputSelDefClassPageModel selfDefModel = new OutputSelDefClassPageModel(
				classDefFile, projectMenuFile);
		for (String name : file.getParentFile().list()) {
			if (name.endsWith(".java")) {
				CompilationUnit unit = ASTParserWrapper.parse(file, enc,
						classpaths);
				MethodAnalyzer visitor = new MethodAnalyzer();

				unit.accept(visitor);

				selfDefModel.setLocalSelDefClass(
						name.substring(0, name.indexOf(".java")),
						visitor.getMethods());// メソッドリストを引数に追加
				selfDefModel.setGlobalSelDefClass(
						name.substring(0, name.indexOf(".java")),
						visitor.getMethods());
			}
		}

		// langDefファイルが存在しない場合は、作成する
		if (!new File(file.getParentFile().getPath() + "/lang_def_project.xml")
				.exists()) {
			Copier langDefXml = new LangDefFileCopier();
			Copier langDefDtd = new LangDefFileDtdCopier();
			langDefXml.print(file);
			langDefDtd.print(file);
		}
		// genusesファイルがない場合は作成する　その際にprojectファイルの場所を追記する
		if (!new File(file.getParentFile().getPath() + "/lang_def_genuses.xml")
				.exists()) {
			Copier genusCopier = new LangDefGenusesCopier();
			genusCopier.print(file);
		}

		CFile jFile = new CFile(file);
		if (jFile.loadText().indexOf(" extends Turtle") != -1) {
			File turtleMenu = new File("ext/block/lang_def_menu_turtle.xml");
			selfDefModel.printMenu(projectMenuFile, turtleMenu);
		} else {
			File cuiMenu = new File("ext/block/lang_def_menu_cui.xml");
			selfDefModel.printMenu(projectMenuFile, cuiMenu);
		}

		// クラスのブロック情報を出力する
		selfDefModel.printGenus();

	}

}
