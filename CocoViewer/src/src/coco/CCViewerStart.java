package src.coco;

import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.controller.CCCompileErrorLoader;
import src.coco.model.CCCompileErrorManager;
import src.coco.view.CCMainFrame2;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

public class CCViewerStart {

	/**
	 * PPVでコンパイルしていない場合，ProjectViewerFrameは開かない（開くファイルのデータがないため）
	 * 
	 */

	public static void main(String[] args) {
		new CCViewerStart().run();
	}

	public void run() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load("ErrorKinds.csv");

		CCCompileErrorLoader errorloader = new CCCompileErrorLoader(manager);
		errorloader.load("CompileErrorLog.csv");

		CDirectory baseDir = CFileSystem.getHomeDirectory()
				.findOrCreateDirectory(".ppvdata");
		manager.setBase(baseDir);
		manager.setLibDir(baseDir.findOrCreateDirectory("ppv.lib"));

		CCMainFrame2 frame = new CCMainFrame2(manager, kindloader.getLines());
		frame.setVisible(true);
	}
}