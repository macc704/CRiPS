package createcocodata.actions;

import javax.swing.JOptionPane;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchWindow;

import ppv.app.datamanager.PPProjectSet;
import ronproeditorplugin.Activator;
import src.coco.controller.CCCompileErrorConverter;
import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.model.CCCompileErrorManager;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;
import clib.common.time.CTime;
import clib.common.time.CTimeInterval;

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

		if (Activator.getDefault().getcompileErrorCashCreating()) {
			JOptionPane.showMessageDialog(null, "CompileError Cash作成・削除中です");
			return;
		} else {
			Activator.getDefault().setcompileErrorCashCreating(true);
		}

		ppvManager = new PresVisualizerManager(window);

		// スレッド化
		Thread thread = new Thread() {
			public void run() {
				createCocoData();
			}
		};

		thread.start();
	}

	public void createCocoData() {
		int res = JOptionPane.showConfirmDialog(null,
				"データの作成には時間がかかりますが，よろしいですか？", "データの作成",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		CTime startTime = new CTime();

		try {
			// CompileError.csvを自動的にエクスポートする
			autoExportCompileErrorCSV();

			// 自動的にエクスポートしたファイルをCoco用データに変換する
			convertCompileErrorData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Activator.getDefault().setcompileErrorCashCreating(false);
		}
		// アクティベーターでコンパイル情報を保持
		Activator.getDefault().setppProjectset(ppProjectSet);

		CTime endTime = new CTime();
		CTimeInterval interval = startTime.diffrence(endTime);
		String minute = interval.getMinuteString();
		String second = interval.getSecondString();

		JOptionPane.showMessageDialog(null, "処理時間： " + minute + "分 " + second
				+ "秒");
		// System.out.println("処理時間： " + minute + "分 " + second + "秒");
	}

	private void autoExportCompileErrorCSV() {
		ppvManager.exportAndImportAll();

		PPDataManager2 ppDataManager2 = ppvManager.getPPDataManager();

		// TODO: ライブラリの場所
		String eclipsePath = null;
		try {
			eclipsePath = Platform.getInstallLocation().getURL().toURI()
					.toString();
			// 頭に付いている"file:/"を削除
			eclipsePath = eclipsePath.split("file:/")[1];
		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("eclipsePath: " + eclipsePath);
		CDirectory libDir = CFileSystem.findDirectory(eclipsePath)
				.findOrCreateDirectory("plugins");
		// System.out.println(libDir.toString());
		ppDataManager2.setLibDir(libDir);

		// TODO Hardcoding
		System.out.println("start load and compile");
		ppProjectSet = ppDataManager2.openProjectSet("hoge", true, true, true);
		System.out.println("end load and compile");
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
