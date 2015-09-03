package ronproeditor.ext;

import java.util.List;

import javax.swing.JOptionPane;

import ppv.app.datamanager.IPPVLoader;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPRonproPPVLoader;
import ronproeditor.REApplication;
import clib.common.compiler.CJavaCompilerFactory;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFilename;
import clib.common.io.CIOUtils;
import clib.common.thread.ICTask;
import clib.view.progress.CPanelProcessingMonitor;

// TODO ppDataManagerがnullの時があるのがいやですね．後で修正
// TODO 毎回全部コンパイルし直すのがいやですね．あとで修正
// TODO project毎に直にPPV参照したいですね．あとで修正
public class REPresVisualizerManager {

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppvフォルダに展開する
	private static String PPV_TMP_DIR = "tmp";// zipファイルを展開するための一時フォルダ /.ppv中
	private static String PPV_PROJECTSET_NAME = "hoge";// projectset名
	private static IPPVLoader RONPRO_PPV_ROADER = new PPRonproPPVLoader();

	private REApplication application;

	private PPDataManager ppDataManager;

	private CPanelProcessingMonitor monitor = new CPanelProcessingMonitor();

	public REPresVisualizerManager(REApplication application) {
		this.application = application;
	}

	public void openPresVisualizer() {
		// 確認ダイアログ
		int res;
		if (!CJavaCompilerFactory.hasEmbededJavaCompiler()) {
			res = JOptionPane.showConfirmDialog(null,
					"JDKを利用していない場合，処理時間が長くなりますが，よろしいですか？", "コンパイラのチェック",
					JOptionPane.OK_CANCEL_OPTION);
			if (res != JOptionPane.OK_OPTION) {
				return;
			}
		}

		res = JOptionPane.showConfirmDialog(null, "データの作成には時間がかかりますが，よろしいですか？",
				"データの作成", JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		exportAndImportAll();
		ppDataManager.setLibDir(application.getLibraryManager().getDir());
		ppDataManager.openProjectSet(PPV_PROJECTSET_NAME, true, true, false);
	}

	public PPDataManager getPPDataManager() {
		return ppDataManager;
	}

	public void exportAndImportAll() {
		final CDirectory ppvRoot = application.getSourceManager()
				.getCRootDirectory().findOrCreateDirectory(PPV_ROOT_DIR);

		// 起動高速化のためcashは消さない
		monitor.setWorkTitle("Deleting...");
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					cleardata(ppvRoot);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		this.ppDataManager = new PPDataManager(ppvRoot);
		CDirectory ppvRootDir = ppDataManager.getBaseDir();
		final CDirectory tmpDir = ppvRootDir.findOrCreateDirectory(PPV_TMP_DIR);

		monitor.setWorkTitle("Zip Exporting...");
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					exportAllProjects(tmpDir);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		monitor.setWorkTitle("UnZip...");
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					importAllProjects(PPV_PROJECTSET_NAME, tmpDir);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	private void cleardata(CDirectory ppvRoot) {
		List<CFileElement> elements = ppvRoot.getChildren(CFileFilter
				.IGNORE_BY_NAME_FILTER("ppv.data"));
		elements.add(ppvRoot.findOrCreateDirectory("ppv.data")
				.findOrCreateDirectory("data"));

		monitor.setMax(elements.size());
		for (CFileElement element : elements) {
			boolean deleted = element.delete();
			if (!deleted) {
				throw new RuntimeException(elements.toString() + "を削除できませんでした．");
			}
			monitor.progress(1);
		}
	}

	private void exportAllProjects(CDirectory tmpDir) {
		List<CDirectory> projects = application.getSourceManager()
				.getAllProjects();

		monitor.setMax(projects.size());
		for (CDirectory project : projects) {
			CDirectory pres = project.findOrCreateDirectory(".pres2");
			if (pres.findFile("pres2.log") != null) {
				exportOneProject(project, tmpDir);
			} else {
				System.out.println(project.getNameByString()
						+ "においてpres2.logが見つかりません");
				// throw new RuntimeException(project.getNameByString()
				// + "においてpres2.logが見つかりません");
			}
			monitor.progress(1);
		}
	}

	private void exportOneProject(CDirectory project, CDirectory tmpDir) {
		CFilename projectName = project.getName();
		projectName.setExtension("zip");
		CFile zipfile = tmpDir.findOrCreateFile(projectName);
		CIOUtils.zip(project, zipfile);
	}

	private void importAllProjects(String projectSetName, CDirectory tmpDir) {
		CDirectory projectSetDir = ppDataManager.getDataDir()
				.findOrCreateDirectory(projectSetName);
		List<CFile> zipfiles = tmpDir.getFileChildren();
		for (CFile zipfile : zipfiles) {
			importOneProject(projectSetDir, zipfile);
		}
	}

	private void importOneProject(CDirectory projectSetDir, CFile zipfile) {
		ppDataManager.loadOneFile(zipfile, projectSetDir, RONPRO_PPV_ROADER);
	}

	public void clearCash() {
		// 確認ダイアログ
		int res = JOptionPane.showConfirmDialog(null,
				"Cashの削除には時間がかかりますが，よろしいですか？", "cashの削除",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		// cashを削除している進捗ダイヤログを利用したいので，PPDataManagerの関数を呼ぶ
		CDirectory ppvRoot = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR);

		this.ppDataManager = new PPDataManager(ppvRoot);
		try {
			ppDataManager.clearCompileCash();
		} catch (Exception ex) {
			throw new RuntimeException("cashが削除できませんでした．");
		}

		// boolean deleted = ppvRoot.findOrCreateDirectory("ppv.data")
		// .findOrCreateDirectory("cash").delete();
		// if (!deleted) {
		// throw new RuntimeException("cashを削除できませんでした．");
		// }
	}
}
