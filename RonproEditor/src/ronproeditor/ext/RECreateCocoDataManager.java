package ronproeditor.ext;

import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import ronproeditor.REApplication;
import src.coco.controller.CCCompileErrorConverter;
import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.model.CCCompileErrorManager;

// TODO CompileError.csvを出力した後にPPVが起動できなくなる不具合がある
public class RECreateCocoDataManager {
	REApplication application;

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppvフォルダに展開する
	private static String ORIGINAL_DATA_FILE = "CompileError.csv"; // ppvから出力されるcsvファイル
	private static String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv"; // ext内のErrorKinds
	private static String DATA_FILE = "CompileErrorLog.csv"; // Coco用のコンパイルエラーデータ
	private PPProjectSet ppProjectSet;

	public RECreateCocoDataManager(REApplication application) {
		this.application = application;
	}

	public void createCocoData() {
		// 確認ダイアログ
		// int res = JOptionPane.showConfirmDialog(null,
		// "データの作成には時間がかかりますが，よろしいですか？", "データの作成",
		// JOptionPane.OK_CANCEL_OPTION);
		// if (res != JOptionPane.OK_OPTION) {
		// return;
		// }

		// CompileError.csvを自動的にエクスポートする
		autoExportCompileErrorCSV();

		// 自動的にエクスポートしたファイルをCoco用データに変換する
		convertCompileErrorData();

		// ここよりそもそもppvの方をスレッド化しないといけない
		// Thread thread = new Thread() {
		// public void run() {
		// try {
		// // CompileError.csvを自動的にエクスポートする
		// autoExportCompileErrorCSV();
		//
		// // 自動的にエクスポートしたファイルをCoco用データに変換する
		// convertCompileErrorData();
		// } catch (Exception ex) {
		// throw new RuntimeException("ppvデータ作成に失敗しました");
		// }
		// }
		// };
		//
		// thread.run();
	}

	private void autoExportCompileErrorCSV() {
		REPresVisualizerManager ppvManager = new REPresVisualizerManager(
				application);
		ppvManager.exportAndImportAll();
		PPDataManager ppDataManager = ppvManager.getPPDataManager();
		ppDataManager.setLibDir(application.getLibraryManager().getDir());
		// TODO Hardcoding
		ppProjectSet = ppDataManager.openProjectSet("hoge", true, true, true);
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
			throw new RuntimeException("CocoViewerのデータ変換ができませんでした");
		}
	}

	private void checkAllFileExist() {
		checkOneFileExist(DATA_FILE);
		checkOneFileExist(ORIGINAL_DATA_FILE);
	}

	private void checkOneFileExist(String filename) {
		application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).findOrCreateFile(filename);
	}

	public PPProjectSet getppProjectSet() {
		return ppProjectSet;
	}
}
