package cocoviewer;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;
import coco.controller.CCCompileErrorKindLoader;
import coco.controller.CCCompileErrorLoader;
import coco.controller.CCMetricsLoader;
import coco.model.CCCompileErrorManager;
import coco.view.CCMainFrame;

public class CCViewerStart {

	public static void main(String[] args) {
		new CCViewerStart().run();
	}

	public void run() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load("MyErrorKinds.csv");

		CCCompileErrorLoader errorloader = new CCCompileErrorLoader(manager);
		errorloader.load("CCCompileErrorLog.csv");

		CCMetricsLoader metricsloader = new CCMetricsLoader(manager);
		metricsloader.load("FileMetrics.csv");
		
		// debug
		// manager = setPPVdata(manager);

		CCMainFrame frame = new CCMainFrame(manager);
		frame.setVisible(true);
	}

	// 事前にEclipseからPPVにかけておけば，ソースコードを閲覧できる（ただし時間がかかる）
	private CCCompileErrorManager setPPVdata(CCCompileErrorManager manager) {
		if (CFileSystem.getHomeDirectory().findDirectory(".ppvdata") != null)
		{
			CDirectory dir = CFileSystem.getHomeDirectory()
					.findOrCreateDirectory(".ppvdata");
			manager.setLibDir(dir.findOrCreateDirectory("ppv.lib"));
			manager.setBaseDir(dir);
		}
		return manager;
	}
}