package bc.classblockfilewriters;

import java.io.File;
import java.util.List;
import java.util.Map;

public class LangDefFilesReWriterMain {

	private File file;
	private String enc;
	private String[] classpaths;

	private LangDefFilesRewriter langDefFilesRewriter;

	private String copyFilesBaseDir;

	private static String CREATED_GENUS_FILE = "lang_def_genuses_project.xml";
	private static String CREATED_MENU_FILE = "lang_def_menu_project.xml";

	public LangDefFilesReWriterMain(File file, String enc, String[] classpaths, String copyFilesBaseDir) {
		this.file = file;
		this.enc = enc;
		this.classpaths = classpaths;
		this.copyFilesBaseDir = copyFilesBaseDir;
	}

	/*
	 * 自作クラスを利用可能にするために，メニュー，ブロック定義ファイルを書き換えて生成する
	 */
	public void rewrite() throws Exception {
		File classDefFile = new File(file.getParentFile().getPath() + "/" + CREATED_GENUS_FILE);
		File projectMenuFile = new File(file.getParentFile().getPath() + "/" + CREATED_MENU_FILE);
		// 言語定義ファイルを書き換えるインスタンスを作成
		langDefFilesRewriter = new LangDefFilesRewriter(classDefFile, this.file.getName(),enc, classpaths);
		langDefFilesRewriter.parseDirectry(enc, classpaths);

		langDefFilesRewriter.copyLangDefFiles(copyFilesBaseDir);
		// メニューの出力
		langDefFilesRewriter.printMenu(projectMenuFile);
		langDefFilesRewriter.printGenuses();
		langDefFilesRewriter.setProjectClassInfo();
	}

	public List<String> getAddedClasses() {
		return langDefFilesRewriter.getAddedClasses();
	}

	public Map<String, String> getAddedMethods() {
		return langDefFilesRewriter.getAddedMethodsType();
	}

	public Map<String, String> getAddedMethodsJavaType() {
		return langDefFilesRewriter.getAddedJavaMethodsJavaType();
	}
}
