package ronproeditor.ext;

import ronproeditor.REApplication;
import coco.controller.CCAddCompileErrorKinds;
import coco.controller.CCCompileErrorConverter;
import coco.controller.CCCompileErrorKindLoader;
import coco.model.CCCompileErrorManager;

public class RECreateCocoDataManager {
	REApplication application;

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppvフォルダに展開する
	private static String ORIGINAL_KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv"; // ext内のErrorKinds
	private static String ORIGINAL_DATA_FILE = "CompileError.csv"; // ppvから出力されるcsvファイル
	private static String KINDS_FILE = "MyErrorKinds.csv"; // ErrorKinds.csvにないコンパイルエラー情報を追加したファイル
	private static String DATA_FILE = "CompileErrorLog.csv"; // Coco用のコンパイルエラーデータ

	public RECreateCocoDataManager(REApplication application) {
		this.application = application;
	}

	public void createCocoData() {
		// CompileError.csvを自動的にエクスポートする

		// 自動的にエクスポートしたファイルをCoco用データに変換する
		convertCompileErrorData();
	}

	private void convertCompileErrorData() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		String ppvRootPath = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).getAbsolutePath()
				.toString()
				+ "/";

		// エラーの種類データをロード
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load(ORIGINAL_KINDS_FILE);

		// CompileErrorデータをCoco用にコンバート
		try {
			CCCompileErrorConverter errorConverter = new CCCompileErrorConverter(
					manager);
			errorConverter.convertData(ppvRootPath + ORIGINAL_DATA_FILE,
					ppvRootPath + DATA_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 追加のエラーデータを書き込んだMyKindsを作成
		try {
			CCAddCompileErrorKinds addCompileErrorKinds = new CCAddCompileErrorKinds(
					manager, kindloader.getLines());
			addCompileErrorKinds.addKinds(ORIGINAL_KINDS_FILE, ppvRootPath
					+ KINDS_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
