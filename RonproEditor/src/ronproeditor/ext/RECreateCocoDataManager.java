package ronproeditor.ext;

import javax.swing.JOptionPane;

import clib.common.compiler.CJavaCompilerFactory;
import coco.controller.CCCompileErrorConverter;
import coco.controller.CCCompileErrorKindLoader;
import coco.model.CCCompileErrorManager;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import ronproeditor.REApplication;

public class RECreateCocoDataManager {
	private REApplication application;

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppvフォルダに展開する
	private static String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv"; // ext内のErrorKinds
	private static String ORIGINAL_DATA_FILE = "CompileError.csv"; // ppvから出力されるcsvファイル
	private static String DATA_FILE = "CompileErrorLog.csv"; // Coco用のコンパイルエラーデータ

	private PPProjectSet ppProjectSet;

	public RECreateCocoDataManager(REApplication application) {
		this.application = application;
	}

	public void createCocoData() {
		if (!CJavaCompilerFactory.hasEmbededJavaCompiler()) {
			int res = JOptionPane.showConfirmDialog(null,
					"<html><font color=\"red\">JDKで動作させない場合，予期しない動作や<br>"
					+ "処理時間が長くなる場合があります．それでもよろしいですか？</font></html>",
					"コンパイラのチェック",
					JOptionPane.OK_CANCEL_OPTION);
			if (res != JOptionPane.OK_OPTION) {
				return;
			}
		}

		int res = JOptionPane.showConfirmDialog(null,
				"<html>データの作成には時間がかかります．<br>"
				+ "処理中にはRonproが操作できなくなりますが，よろしいですか？</html>", "データの作成",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		// CompileError.csvを自動的にエクスポートする
		autoExportCompileErrorCSV();

		// 自動的にエクスポートしたファイルをCoco用データに変換する
		convertCompileErrorData();
		
		JOptionPane.showMessageDialog(application.getFrame(), "CocoViewer用データ作成完了しました！", "CocoViewer用データ作成完了しました！",
		        JOptionPane.INFORMATION_MESSAGE);
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
			e.printStackTrace();
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

	public PPProjectSet getPPProjectSet() {
		return ppProjectSet;
	}
}
