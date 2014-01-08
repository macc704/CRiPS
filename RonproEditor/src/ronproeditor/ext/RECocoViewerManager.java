package ronproeditor.ext;

import ppv.app.datamanager.PPProjectSet;
import ronproeditor.REApplication;
import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.controller.CCCompileErrorLoader;
import src.coco.controller.CCMetricsLoader;
import src.coco.model.CCCompileErrorManager;
import src.coco.view.CCMainFrame2;
import clib.common.filesystem.CDirectory;

public class RECocoViewerManager {
	REApplication application;

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppvフォルダに展開する
	private static String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv"; // ext内のErrorKinds
	private static String DATA_FILE = "CompileErrorLog.csv";
	private static String METRICS_FILE = "FileMetrics.csv";

	private int errorKindsCount;

	public RECocoViewerManager(REApplication application) {
		this.application = application;
	}

	public void openCocoViewer(PPProjectSet ppProjectSet) {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		loadData(manager);

		CDirectory ppvRoot = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR);
		CDirectory libDir = application.getLibraryManager().getDir();
		manager.setBase(ppvRoot);
		manager.setLibDir(libDir);
		manager.setppProjectSet(ppProjectSet);
		new CCMainFrame2(manager, errorKindsCount).setVisible(true);
	}

	private void loadData(CCCompileErrorManager manager) {
		String ppvRootPath = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).getAbsolutePath()
				.toString()
				+ "/";

		CCCompileErrorKindLoader kindLoader = new CCCompileErrorKindLoader(
				manager);
		kindLoader.load(KINDS_FILE);
		errorKindsCount = kindLoader.getLines();

		CCCompileErrorLoader errorLoader = new CCCompileErrorLoader(manager);
		errorLoader.load(ppvRootPath + DATA_FILE);

		CCMetricsLoader metricsloader = new CCMetricsLoader(manager);
		metricsloader.load(ppvRootPath + METRICS_FILE);
	}
}