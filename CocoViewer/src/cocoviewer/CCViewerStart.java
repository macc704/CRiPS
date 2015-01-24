package cocoviewer;

import coco.controller.CCCompileErrorKindLoader;
import coco.controller.CCCompileErrorLoader;
import coco.controller.CCMetricsLoader;
import coco.model.CCCompileErrorManager;
import coco.view.CCMainFrame2;

public class CCViewerStart {

	public static void main(String[] args) {
		new CCViewerStart().run();
	}

	public void run() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load("ErrorKindsEng.csv");

		CCCompileErrorLoader errorloader = new CCCompileErrorLoader(manager);
		errorloader.load("CompileErrorLog.csv");

		CCMetricsLoader metricsloader = new CCMetricsLoader(manager);
		metricsloader.load("FileMetrics.csv");

		// 事前にEclipseからPPVにかけておけば，ソースコードを閲覧できる（ただし時間がかかる）
		// if (CFileSystem.getHomeDirectory().findDirectory(".ppvdata") != null)
		// {
		// CDirectory dir = CFileSystem.getHomeDirectory()
		// .findOrCreateDirectory(".ppvdata");
		// manager.setLibDir(dir.findOrCreateDirectory("ppv.lib"));
		// manager.setBaseDir(dir);
		// }

		CCMainFrame2 frame = new CCMainFrame2(manager, "EN");
		frame.start();
		frame.setVisible(true);
	}
}