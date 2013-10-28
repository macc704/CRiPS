package ronproeditor.ext;

import java.util.List;

import ppv.app.datamanager.IPPVLoader;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPRonproPPVLoader;
import ronproeditor.REApplication;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFilename;
import clib.common.io.CIOUtils;

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

	public REPresVisualizerManager(REApplication application) {
		this.application = application;
	}

	public void exportAndImportAll() {
		CDirectory ppvRoot = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR);
		boolean deleted = ppvRoot.delete();
		if (!deleted) {
			throw new RuntimeException("ppvRootを削除できませんでした．");
		}
		this.ppDataManager = new PPDataManager(ppvRoot);
		CDirectory ppvRootDir = ppDataManager.getBaseDir();
		CDirectory tmpDir = ppvRootDir.findOrCreateDirectory(PPV_TMP_DIR);
		exportAllProjects(tmpDir);
		importAllProjects(PPV_PROJECTSET_NAME, tmpDir);
	}

	private void exportAllProjects(CDirectory tmpDir) {
		List<CDirectory> projects = application.getSourceManager()
				.getAllProjects();
		for (CDirectory project : projects) {
			exportOneProject(project, tmpDir);
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

	public void openPresVisualizer() {
		exportAndImportAll();
		ppDataManager.setLibDir(application.getLibraryManager().getDir());
		ppDataManager.openProjectSet(PPV_PROJECTSET_NAME, true, true);
	}
}
