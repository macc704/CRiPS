package cocoviewer.actions;

import org.eclipse.ui.IWorkbenchWindow;

import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.controller.CCCompileErrorLoader;
import src.coco.controller.CCMetricsLoader;
import src.coco.model.CCCompileErrorManager;
import src.coco.view.CCMainFrame2;
import clib.common.filesystem.CFileSystem;

public class CocoViewerManager {

	// TODO: データを置く場所・ppvrootフォルダの場所
	private String PPV_ROOT_DIR = CFileSystem.getHomeDirectory()
			.findOrCreateDirectory(".ppvdata").getAbsolutePath().toString();
	private String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv";
	// private String ERROR_DATA_FILE = "CompileError.csv";
	private String ERROR_LOG_FILE = "/CompileErrorLog.csv";
	private String METRICS_FILE = "/FileMetrics.csv";

	public CocoViewerManager(IWorkbenchWindow window) {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load(KINDS_FILE);

		CCCompileErrorLoader errorloader = new CCCompileErrorLoader(manager);
		errorloader.load(PPV_ROOT_DIR + ERROR_LOG_FILE);

		CCMetricsLoader metricsloader = new CCMetricsLoader(manager);
		metricsloader.load(PPV_ROOT_DIR + METRICS_FILE);

		CCMainFrame2 frame = new CCMainFrame2(manager);
		frame.setVisible(true);
	}

}
