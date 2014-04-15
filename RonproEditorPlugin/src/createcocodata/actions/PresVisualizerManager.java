package createcocodata.actions;

import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchWindow;

import ppv.app.datamanager.IPPVLoader;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPEclipsePPVLoader;
import clib.common.compiler.CJavaCompilerFactory;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CFilename;
import clib.common.filesystem.CPath;
import clib.common.io.CIOUtils;
import clib.common.thread.ICTask;
import clib.view.progress.CPanelProcessingMonitor;

// TODO ZIPのフォルダ構成
// TODO バックグラウンド処理
public class PresVisualizerManager {

	private String PPV_ROOT_DIR = CFileSystem.getHomeDirectory()
			.findOrCreateDirectory(".ppvdata").getAbsolutePath().toString();
	private static String PPV_TMP_DIR = "tmp";// zipファイルを展開するための一時フォルダ /.ppv中
	private static String PPV_PROJECTSET_NAME = "hoge";// projectset名
	private static IPPVLoader PPV_ROADER = new PPEclipsePPVLoader();

	private PPDataManager ppDataManager;

	private CPanelProcessingMonitor monitor = new CPanelProcessingMonitor();

	public PresVisualizerManager(IWorkbenchWindow window) {
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
		// ライブラリの場所
		String eclipsePath = null;
		try {
			eclipsePath = Platform.getInstallLocation().getURL().toURI()
					.toString();
			// 頭に付いている"file:/"を削除
			eclipsePath = eclipsePath.split("file:/")[1];
		} catch (Exception e) {
			e.printStackTrace();
		}

		CDirectory libDir = new CDirectory(new CPath(eclipsePath))
				.findOrCreateDirectory("plugins");
		ppDataManager.setLibDir(libDir);

		ppDataManager.openProjectSet(PPV_PROJECTSET_NAME, true, true, false);
	}

	public PPDataManager getPPDataManager() {
		return ppDataManager;
	}

	public void exportAndImportAll() {
		final CDirectory ppvRoot = CFileSystem.findDirectory(PPV_ROOT_DIR);

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
		// Projectのパスを持ってくる
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		String projectRoot = root.getLocation().toFile().getAbsolutePath()
				.toString();

		List<CDirectory> projects = new CDirectory(new CPath(projectRoot))
				.getDirectoryChildren(CFileFilter.IGNORE_BY_NAME_FILTER(".*"));

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
		// TODO zip export eclipse の export → archive file が使えないか？
		CFilename projectName = project.getName();
		projectName.setExtension("zip");
		CDirectory projectdir = tmpDir.findOrCreateDirectory(project
				.getNameByString());
		project.copyTo(projectdir);
		CFile zipfile = tmpDir.findOrCreateFile(projectName);
		CIOUtils.zip(projectdir, zipfile);
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
		ppDataManager.loadOneFile(zipfile, projectSetDir, PPV_ROADER);
	}
}
