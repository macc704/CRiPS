package ronproeditor.ext;

import ppv.app.datamanager.PPDataManager;
import ronproeditor.REApplication;
import src.coco.controller.CCCompileErrorConverter;
import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.model.CCCompileErrorManager;

// TODO CompileError.csvを出力した後にPPVが起動できなくなる不具合がある
public class RECreateCocoDataManager {
	REApplication application;

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppvフォルダに展開する
	// private static String ORIGINAL_KINDS_FILE =
	// "ext/cocoviewer/ErrorKinds.csv"; // ext内のErrorKinds
	private static String ORIGINAL_DATA_FILE = "CompileError.csv"; // ppvから出力されるcsvファイル
	// private static String KINDS_FILE = "MyErrorKinds.csv"; //
	// ErrorKinds.csvにないコンパイルエラー情報を追加したファイル
	private static String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv"; // ext内のErrorKinds
	private static String DATA_FILE = "CompileErrorLog.csv"; // Coco用のコンパイルエラーデータ

	public RECreateCocoDataManager(REApplication application) {
		this.application = application;
	}

	public void createCocoData() {
		// CompileError.csvを自動的にエクスポートする
		autoExportCompileErrorCSV();

		// 自動的にエクスポートしたファイルをCoco用データに変換する
		convertCompileErrorData();
	}

	private void autoExportCompileErrorCSV() {
		REPresVisualizerManager ppvManager = new REPresVisualizerManager(
				application);
		ppvManager.exportAndImportAll();
		PPDataManager ppDataManager = ppvManager.getPPDataManager();
		ppDataManager.setLibDir(application.getLibraryManager().getDir());
		// TODO Hardcoding
		ppDataManager.openProjectSet("hoge", true, true, true);
	}

	private void convertCompileErrorData() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		String ppvRootPath = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).getAbsolutePath()
				.toString()
				+ "/";

		checkAllFileExist();

		// エラーの種類データをロード
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load(KINDS_FILE);

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
		// try {
		// CCAddCompileErrorKinds addCompileErrorKinds = new
		// CCAddCompileErrorKinds(
		// manager, kindloader.getLines());
		// addCompileErrorKinds.addKinds(ORIGINAL_KINDS_FILE, ppvRootPath
		// + KINDS_FILE);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private void checkAllFileExist() {
		checkOneFileExist(DATA_FILE);
		checkOneFileExist(KINDS_FILE);
		checkOneFileExist(ORIGINAL_DATA_FILE);
		// checkOneFileExist(ORIGINAL_KINDS_FILE);
	}

	private void checkOneFileExist(String filename) {
		application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).findOrCreateFile(filename);
	}
}
