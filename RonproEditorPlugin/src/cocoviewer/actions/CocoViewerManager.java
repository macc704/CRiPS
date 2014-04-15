package cocoviewer.actions;

import org.eclipse.ui.IWorkbenchWindow;

import ppv.app.datamanager.PPProjectSet;
import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.controller.CCCompileErrorLoader;
import src.coco.controller.CCMetricsLoader;
import src.coco.model.CCCompileErrorManager;
import src.coco.view.CCMainFrame2;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

public class CocoViewerManager {

	// TODO: データを置く場所・ppvrootフォルダの場所
	private String PPV_ROOT_DIR = CFileSystem.getHomeDirectory()
			.findOrCreateDirectory(".ppvdata").getAbsolutePath().toString();
	private String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv";
	private String ERROR_LOG_FILE = "/CompileErrorLog.csv";
	private String METRICS_FILE = "/FileMetrics.csv";

	private CDirectory baseDir;
	private CDirectory libDir;
	private PPProjectSet projectset;

	public CocoViewerManager(IWorkbenchWindow window) {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load(KINDS_FILE);

		CCCompileErrorLoader errorloader = new CCCompileErrorLoader(manager);
		errorloader.load(PPV_ROOT_DIR + ERROR_LOG_FILE);

		CCMetricsLoader metricsloader = new CCMetricsLoader(manager);
		metricsloader.load(PPV_ROOT_DIR + METRICS_FILE);

		manager.setBaseDir(baseDir);
		manager.setLibDir(libDir);
		manager.setPPProjectSet(projectset);

		CCMainFrame2 frame = new CCMainFrame2(manager);
		frame.toFront();
		frame.setVisible(true);
	}

	public void setBaseDir(CDirectory baseDir) {
		this.baseDir = baseDir;
	}

	public void setLibDir(CDirectory libDir) {
		this.libDir = libDir;
	}

	public void setProjectset(PPProjectSet projectset) {
		this.projectset = projectset;
	}
}
