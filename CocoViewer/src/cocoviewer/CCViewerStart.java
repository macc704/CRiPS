package cocoviewer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import coco.controller.CCCompileErrorKindLoader;
import coco.controller.CCCompileErrorLoader;
import coco.controller.CCMetricsLoader;
import coco.controller.CCPropertiesLoader;
import coco.model.CCCompileErrorManager;
import coco.view.CCMainFrame2;

public class CCViewerStart {

	public static void main(String[] args) {
		new CCViewerStart().run();
	}

	public void run() {
		engtest();
		// jptest();
	}

	public void jptest() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load("ErrorKinds.csv");

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

		CCPropertiesLoader propertiesloader = new CCPropertiesLoader("lang/coco_jp.xml");

		CCMainFrame2 frame = new CCMainFrame2(manager, propertiesloader.getProperties("JP"));
		frame.start();
	}

	public void engtest() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load("ErrorKindsEng.csv");

		CCCompileErrorLoader errorloader = new CCCompileErrorLoader(manager);
		errorloader.load("CompileErrorLogTest.csv");

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

		CCPropertiesLoader propertiesloader = new CCPropertiesLoader("lang/coco_en.xml");

		CCMainFrame2 frame = new CCMainFrame2(manager, propertiesloader.getProperties("EN"));
		frame.start();
	}
}