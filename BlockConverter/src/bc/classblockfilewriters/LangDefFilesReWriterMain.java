package bc.classblockfilewriters;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LangDefFilesReWriterMain {

	private File file;
	private String enc;
	private String[] classpaths;

	private List<String> addedClasses = new LinkedList<String>();// 追加済みクラス
	LangDefFilesRewriter langDefFilesRewriter;

	public LangDefFilesReWriterMain(File file, String enc, String[] classpaths) {
		this.file = file;
		this.enc = enc;
		this.classpaths = classpaths;
	}

	/*
	 * 自作クラスを利用可能にするために，メニュー，ブロック定義ファイルを書き換えて生成する
	 */
	public void rewrite() throws Exception {
		File classDefFile = new File(file.getParentFile().getPath() + "/lang_def_genuses_project.xml");
		File projectMenuFile = new File(file.getParentFile().getPath() + "/lang_def_menu_project.xml");
		// 言語定義ファイルを書き換えるインスタンスを作成
		langDefFilesRewriter = new LangDefFilesRewriter(classDefFile, this.file.getName(),enc, classpaths);
		langDefFilesRewriter.parseDirectry(enc, classpaths);

		copyLangDefFiles();

		// メニューの出力
		langDefFilesRewriter.printMenu(projectMenuFile);

		// プロジェクトのオブジェクトブロック情報を出力する
		langDefFilesRewriter.printGenus();

		langDefFilesRewriter.setProjectClassInfo();
	}

	private void copyLangDefFiles() {
		// // 継承関係にあるブロック達をファミリーに出力
		LangDefFamiliesCopier langDefFamilies = new LangDefFamiliesCopier();
		langDefFamilies.print(file);

		// langDefファイルを作成する
		Copier langDefXml = new LangDefFileCopier();
		langDefXml.print(file);

		Copier langDefDtd = new LangDefFileDtdCopier();
		langDefDtd.print(file);

		// genuseファイルを作成する　その際にprojectファイルの場所を追記する
		Copier genusCopier = new LangDefGenusesCopier();
		genusCopier.print(file);
	}

	public List<String> getAddedClasses() {
		return this.addedClasses;
	}

	public Map<String, String> getAddedMethods() {
		return langDefFilesRewriter.getAddedMethodsType();
	}

	public Map<String, String> getAddedMethodsJavaType() {
		return langDefFilesRewriter.getAddedJavaMethodsJavaType();
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
