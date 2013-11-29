package ClassBlockFileModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.CompilationUnit;

import bc.j2b.analyzer.MethodAnalyzer;
import bc.utils.ASTParserWrapper;
import clib.common.filesystem.CFile;

public class LangDefFileReWriter {

	private File file;
	private String enc;
	private String[] classpaths;
	private BufferedReader br;

	public LangDefFileReWriter(File file, String enc, String[] classpaths) {
		this.file = file;
		this.enc = enc;
		this.classpaths = classpaths;
	}

	public void rewrite() throws Exception {
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
				// javaファイル解析
				CompilationUnit unit = ASTParserWrapper.parse(file, enc,
						classpaths);
				MethodAnalyzer visitor = new MethodAnalyzer();

				// 継承してたら、親クラスのメソッドを読む
				// 継承してるか？
				// おおまかな流れ
				// javaファイル解析-----
				// | |
				// 継承チェック |
				// | ---------継承先の解析
				// javaファイル解析

				// 継承チェック
				File classFile = new File(file.getParentFile().getPath() + "/"
						+ name);
				FileReader reader = new FileReader(classFile);
				br = new BufferedReader(reader);
				String str;

				Pattern p = Pattern.compile("[^ ]+");
				while ((str = br.readLine()) != null) {

					if (str.contains("extends")) {
						str = str.substring(
								str.indexOf("extends") + "extends".length(),
								str.length());
						Matcher m = p.matcher(str);
						if (m.find()) {

						}
						break;
					}
				}

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

	public void analyzeJavaFile(String name) throws IOException {
		// javaファイル解析
		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classpaths);
		MethodAnalyzer visitor = new MethodAnalyzer();

		// 継承チェック
		File classFile = new File(file.getParentFile().getPath() + "/" + name);
		FileReader reader = new FileReader(classFile);
		br = new BufferedReader(reader);
		String str;

		Pattern p = Pattern.compile("[^ ]+");
		while ((str = br.readLine()) != null) {
			if (str.contains("extends")) {
				str = str.substring(
						str.indexOf("extends") + "extends".length(),
						str.length());
				Matcher m = p.matcher(str);
				if (m.find()) {

				}
				break;
			}
		}

		unit.accept(visitor);

	}

}
