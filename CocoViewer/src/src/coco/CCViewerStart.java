package src.coco;

import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.controller.CCCompileErrorLoader;
import src.coco.model.CCCompileErrorManager;
import src.coco.view.CCMainFrame2;

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
		errorloader.load("CompileErrorLog.csv");

		CCMainFrame2 frame = new CCMainFrame2(manager);
		frame.setVisible(true);
	}
}