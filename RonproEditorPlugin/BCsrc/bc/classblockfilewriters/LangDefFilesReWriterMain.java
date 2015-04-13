package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;

import bc.utils.ASTParserWrapper;

public class LangDefFilesReWriterMain {

	private File file;
	private String enc;
	private String[] classpaths;
	private BufferedReader br;
	private Map<String, String> addedMethods = new HashMap<String, String>();
	private Map<String, String> addedMethodsJavaType = new HashMap<String, String>();
	private List<String> addedClasses = new LinkedList<String>();//追加済みクラス

	public LangDefFilesReWriterMain(File file, String enc, String[] classpaths) {
		this.file = file;
		this.enc = enc;
		this.classpaths = classpaths;
	}

	public void rewrite() throws Exception {
		// オブジェクト変数ブロックの定義されたxmlファイルを作成する
		File classDefFile = new File(file.getParentFile().getPath()
				+ "/lang_def_genuses_project.xml");
		// メニューの定義されたxmlを作成、（or追加)
		File projectMenuFile = new File(file.getParentFile().getPath()
				+ "/lang_def_menu_project.xml");
		//言語定義ファイルを書き換えるインスタンスを作成
		LangDefFilesRewriter selfDefModel = new LangDefFilesRewriter(
				classDefFile, this.file.getName());

		// 同じディレクトリ内のすべてのjavaファイルをパースし、モデルに追加する
		parseDirectry(selfDefModel);

//		// 継承関係にあるブロック達をファミリーに出力
		printLangDefFamilies();

		// langDefファイルを作成する
		Copier langDefXml = new LangDefFileCopier();
		Copier langDefDtd = new LangDefFileDtdCopier();
		langDefXml.print(file);
		langDefDtd.print(file);

		// genuseファイルを作成する　その際にprojectファイルの場所を追記する
		Copier genusCopier = new LangDefGenusesCopier();
		genusCopier.print(file);

		FileReader reader = new FileReader(file);

		br = new BufferedReader(reader);
		String str;

		while ((str = br.readLine()) != null) {
			if (str.contains(" extends Turtle")) {
				File turtleMenu = new File(System.getProperty("user.dir"),"ext/block/lang_def_menu_turtle.xml");
				selfDefModel.printMenu(projectMenuFile, turtleMenu);
				selfDefModel.printGenus();
				this.addedMethods = selfDefModel.getAddedMethods();
				this.addedMethodsJavaType = selfDefModel
						.getAddedMethodsJavaType();
				return;
			}
		}
		// メニューの出力
		printMenu(selfDefModel, projectMenuFile);

		// プロジェクトのオブジェクトブロック情報を出力する
		selfDefModel.printGenus();
		//追加済みのメソッド，返り値の型を追加する
		this.addedMethods = selfDefModel.getAddedMethods();
		this.addedMethodsJavaType = selfDefModel.getAddedMethodsJavaType();
	}

	private void parseDirectry(LangDefFilesRewriter selfDefModel) throws IOException{
		for (String name : file.getParentFile().list()) {
			if (name.endsWith(".java")) {
				// javaファイル生成
				File javaFile = new File(file.getParentFile().getPath() + "/"
						+ name);
				name = name.substring(0, name.indexOf(".java"));
				//javaファイルを解析
				Map<String, List<PublicMethodInfo>> methods = analyzeJavaFile(
						name, javaFile, name);
				//親クラス名を取得し，各モデルに追加する
				String superClassName = getSuperClassName(javaFile);
				// ローカル変数ブロックのモデルを追加
				selfDefModel.setLocalVariableBlockModel(name, methods, superClassName);// メソッドリストを引数に追加
//				// インスタンス変数ブロックのモデルを追加
				selfDefModel.setInstanceVariableBlockMode(name, methods, superClassName);

				// 型変換ブロックモデルの追加
				selfDefModel.setConvertBlockModel(name);
				// 引数ブロックモデルの追加
				selfDefModel.setParameterBlockModel(name, methods);
//				//配列ブロックモデルの追加
				selfDefModel.setArrayParameterBlockModel(name, methods);

				//キャッシュに登録済みクラスを追加する
				addedClasses.add(name);
			}
		}
	}

	private void printMenu(LangDefFilesRewriter selfDefModel, File projectMenuFile){
		File cuiMenu = new File(System.getProperty("user.dir"),"ext/block/lang_def_menu_cui.xml");
		selfDefModel.printMenu(projectMenuFile, cuiMenu);
	}

	public List<String> getAddedClasses() {
		return this.addedClasses;
	}

	private void printLangDefFamilies() {
		LangDefFamiliesCopier langDefFamilies = new LangDefFamiliesCopier();

		langDefFamilies.print(file);
	}

	private Map<String, List<PublicMethodInfo>> analyzeJavaFile(String name,
			File file, String childName) throws IOException {
		// javaファイル解析して、クラス名とメソッドのセットを取得する
		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classpaths);
		MethodAnalyzer visitor = new MethodAnalyzer();
		unit.accept(visitor);

		Map<String, List<PublicMethodInfo>> methods = new HashMap<String, List<PublicMethodInfo>>();
		String superClassName = visitor.getSuperClassName();

		//親クラスのクラス名と，メソッド情報を取得し，先に登録する
		if (superClassName != null
				&& existCurrentDirectry(superClassName + ".java")) {
			methods = analyzeJavaFile(superClassName,
					new File(file.getParentFile().getPath() + "/"
							+ superClassName + ".java"), childName);
		}
		//最後に，自クラス名とメソッド情報を登録して返す
		methods.put(name, visitor.getMethods());

		return methods;
	}

	public String getSuperClassName(File file){
		// javaファイル解析
		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classpaths);
		MethodAnalyzer visitor = new MethodAnalyzer();

		// 継承チェック
		unit.accept(visitor);

		return visitor.getSuperClassName();

	}

	public Map<String, String> getAddedMethods() {
		return this.addedMethods;
	}

	public Map<String, String> getAddedMethodsJavaType() {
		return this.addedMethodsJavaType;
	}

	private Boolean existCurrentDirectry(String fileName) {
		for (String name : file.getParentFile().list()) {
			if (name.equals(fileName)) {
				return true;
			}
		}
		return false;
	}

	class Family {
		List<String> familyList = new ArrayList<String>();

		protected void addFamilyMember(String member) {
			familyList.add(member);
		}

		protected List<String> getFamilyMember() {
			return this.familyList;
		}

	}
}
