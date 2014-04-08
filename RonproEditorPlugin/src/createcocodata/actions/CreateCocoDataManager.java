package createcocodata.actions;

import javax.swing.JOptionPane;

import org.eclipse.ui.IWorkbenchWindow;

import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import src.coco.controller.CCCompileErrorConverter;
import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.model.CCCompileErrorManager;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;

public class CreateCocoDataManager {

	private String PPV_ROOT_DIR = CFileSystem.getHomeDirectory()
			.findOrCreateDirectory(".ppvdata").getAbsolutePath().toString();
	private static String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv"; // ext内のErrorKinds
	private static String ORIGINAL_DATA_FILE = "/CompileError.csv"; // ppvから出力されるcsvファイル
	private static String DATA_FILE = "/CompileErrorLog.csv"; // Coco用のコンパイルエラーデータ

	private PPProjectSet ppProjectSet;

	private PresVisualizerManager ppvManager;

	public CreateCocoDataManager(IWorkbenchWindow window) {
		// TODO Auto-generated constructor stub
		// WorkSpaceのパスを取得する
		// IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// IWorkspaceRoot root = workspace.getRoot();
		// System.out.println(root.getLocation().toFile().getAbsolutePath()
		// .toString());
		ppvManager = new PresVisualizerManager(window);
		createCocoData();
	}

	public void createCocoData() {
		int res = JOptionPane.showConfirmDialog(null,
				"データの作成には時間がかかりますが，よろしいですか？", "データの作成",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		// CompileError.csvを自動的にエクスポートする
		autoExportCompileErrorCSV();

		// 自動的にエクスポートしたファイルをCoco用データに変換する
		convertCompileErrorData();
	}

	private void autoExportCompileErrorCSV() {
		ppvManager.exportAndImportAll();
		PPDataManager ppDataManager = ppvManager.getPPDataManager();

		// TODO: ライブラリの場所
		ppDataManager.setLibDir(new CDirectory(new CPath(PPV_ROOT_DIR))
				.findOrCreateDirectory("ppv.lib"));
		// TODO Hardcoding
		ppProjectSet = ppDataManager.openProjectSet("hoge", true, true, true);
	}

	private void convertCompileErrorData() {
		CCCompileErrorManager manager = new CCCompileErrorManager();

		checkAllFileExist();

		// エラーの種類データをロード
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load(KINDS_FILE);

		// CompileErrorデータをCoco用にコンバート
		try {
			CCCompileErrorConverter errorConverter = new CCCompileErrorConverter(
					manager);
			errorConverter.convertData(PPV_ROOT_DIR + ORIGINAL_DATA_FILE,
					PPV_ROOT_DIR + DATA_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkAllFileExist() {
		checkOneFileExist(DATA_FILE);
		checkOneFileExist(ORIGINAL_DATA_FILE);
	}

	private void checkOneFileExist(String filename) {
		CFileSystem.findDirectory(PPV_ROOT_DIR).findOrCreateFile(filename);
	}

	public PPProjectSet getPPProjectSet() {
		return ppProjectSet;
	}
}
