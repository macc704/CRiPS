package coco.action;

import java.util.List;

import javax.swing.JOptionPane;

import clib.common.compiler.CJavaCompilerFactory;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFilename;
import clib.common.io.CIOUtils;
import clib.common.thread.ICTask;
import clib.view.progress.CPanelProcessingMonitor;
import coco.controller.CCCompileErrorConverter;
import coco.controller.CCCompileErrorKindLoader;
import coco.model.CCCompileErrorManager;
import ppv.app.datamanager.IPPVLoader;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import ppv.app.datamanager.PPRonproPPVLoader;


// TODO: pathとdirを変えた部分があるので，再度調整する
public class CCCreateCocodata {

	private CCCompileErrorManager ccManager;
	private PPDataManager ppDataManager;
	private PPProjectSet ppProjectSet;

	private CPanelProcessingMonitor monitor = new CPanelProcessingMonitor();

	public CCCreateCocodata(CCCompileErrorManager ccManager) {
		this.ccManager = ccManager;
	}

	public void createData() {
		if(startCheck()) {
			return;
		}

		// ppv を利用してデータ作製
		// Export and Import
		autoExportAndImport();

		// Create Cash
		createCash();

		// Convert for Cocoviewer
		convertCompileErrorData();
	}

	private boolean startCheck() {
		// JDK check
		if (!CJavaCompilerFactory.hasEmbededJavaCompiler()) {
			JOptionPane.showMessageDialog(null, "JDKで起動されていません", "データ生成ができません", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(null,
				"データの作成には時間がかかりますが，よろしいですか？", "データの作成",
				JOptionPane.OK_CANCEL_OPTION);
	}

	/************************
	 *  Export and Import Actions
	 ************************/

	private void autoExportAndImport() {
		final CDirectory ppvRoot = ccManager.getPathdata().getPPVRootDir();

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

		ppDataManager = new PPDataManager(ppvRoot);
		CDirectory ppvRootDir = ppDataManager.getBaseDir();
		final CDirectory tmpDir = ccManager.getPathdata().getPPVTempDir();

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
					importAllProjects(ccManager.getPathdata().getPPVProjectSetName(), tmpDir);
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
		List<CDirectory> projects = ccManager.getProjects();

		monitor.setMax(projects.size());
		for (CDirectory project : projects) {
			CDirectory pres = project.findOrCreateDirectory(".pres2");
			if (pres.findFile("pres2.log") != null) {
				exportOneProject(project, tmpDir);
			} else {
				System.out.println(project.getNameByString()
						+ "においてpres2.logが見つかりません");
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

	private void importAllProjects(CDirectory projectSetNameDir, CDirectory tmpDir) {
		CDirectory projectSetDir = projectSetNameDir;
		List<CFile> zipfiles = tmpDir.getFileChildren();
		for (CFile zipfile : zipfiles) {
			importOneProject(projectSetDir, zipfile);
		}
	}

	private void importOneProject(CDirectory projectSetDir, CFile zipfile) {
		ppDataManager.loadOneFile(zipfile, projectSetDir, ccManager.getPPVLoader());
	}

	/************************
	 * Create Cash Action
	 ************************/
	private void createCash() {
		ppDataManager.setLibDir(ccManager.getPathdata().getPPVLibDir());
		// TODO Hardcoding
		ppProjectSet = ppDataManager.openProjectSet("hoge", true, true, true);
	}

	/************************
	 * Convert Compile error Data for CocoViewer
	 ************************/
	private void convertCompileErrorData() {
		String ppvRootPath = ccManager.getPathdata().getPPVRootDir().getAbsolutePath()
				.toString() + "/";

		// エラーの種類データをロード
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				ccManager);
		kindloader.load(ccManager.getPathdata().getKindsFilePath());

		// CompileErrorデータをCoco用にコンバート
		try {
			CCCompileErrorConverter errorConverter = new CCCompileErrorConverter(
					ccManager);
			errorConverter.convertData(ccManager.getPathdata().getOriginalDataFilePath(),
					ccManager.getPathdata().getDataFilePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/************************
	 * Clear Cash Action
	 ************************/
	public void clearCash() {
		// 確認ダイアログ
		int res = JOptionPane.showConfirmDialog(null,
				"Cashの削除には時間がかかりますが，よろしいですか？", "cashの削除",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		// cashを削除している進捗ダイヤログを利用したいので，PPDataManagerの関数を呼ぶ
		CDirectory ppvRoot = ccManager.getPathdata().getPPVRootDir();

		ppDataManager = new PPDataManager(ppvRoot);
		try {
			ppDataManager.clearCompileCash();
		} catch (Exception ex) {
			throw new RuntimeException("cashが削除できませんでした．");
		}
	}
}
