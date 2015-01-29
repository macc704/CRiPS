package cocoviewer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

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
		// engtest();
		jptest();
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

		Properties properties = new Properties();
		try {
			InputStream is = new FileInputStream("lang/coco_jp.xml");
			properties.loadFromXML(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

		CCMainFrame2 frame = new CCMainFrame2(manager, properties);
		frame.start();
		frame.setVisible(true);
	}

	public void engtest() {
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

		Properties properties = new Properties();

		try {
			InputStream is = new FileInputStream("lang/coco_en.xml");
			properties.loadFromXML(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

		CCMainFrame2 frame = new CCMainFrame2(manager, properties);
		frame.start();
		frame.setVisible(true);
	}
}