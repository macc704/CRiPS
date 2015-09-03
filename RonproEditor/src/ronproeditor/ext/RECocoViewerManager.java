package ronproeditor.ext;

import coco.controller.CCCompileErrorKindLoader;
import coco.controller.CCCompileErrorLoader;
import coco.controller.CCMetricsLoader;
import coco.model.CCCompileErrorManager;
import coco.view.CCMainFrame2;
import ppv.app.datamanager.PPProjectSet;
import ronproeditor.REApplication;
import clib.common.filesystem.CDirectory;

public class RECocoViewerManager {
	private REApplication application;

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppvフォルダに展開する
	private static String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv";
	private static String DATA_FILE = "CompileErrorLog.csv";
	private static String METRICS_FILE = "FileMetrics.csv";

	public RECocoViewerManager(REApplication application) {
		this.application = application;
	}

	public void openCocoViewer(PPProjectSet ppProjectSet) {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		loadData(manager);

		CDirectory ppvRoot = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR);
		CDirectory libDir = application.getLibraryManager().getDir();
		manager.setBaseDir(ppvRoot);
		manager.setLibDir(libDir);
		manager.setPPProjectSet(ppProjectSet);

		new CCMainFrame2(manager).setVisible(true);
	}

	private void loadData(CCCompileErrorManager manager) {
		String ppvRootPath = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).getAbsolutePath()
				.toString()
				+ "/";

		CCCompileErrorKindLoader kindLoader = new CCCompileErrorKindLoader(
				manager);
		kindLoader.load(KINDS_FILE);

		CCCompileErrorLoader errorLoader = new CCCompileErrorLoader(manager);
		errorLoader.load(ppvRootPath + DATA_FILE);

		CCMetricsLoader metricsLoader = new CCMetricsLoader(manager);
		metricsLoader.load(ppvRootPath + METRICS_FILE);
	}
}
