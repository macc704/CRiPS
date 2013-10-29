package ronproeditor.ext;

import ronproeditor.REApplication;
import coco.controller.CCCompileErrorKindLoader;
import coco.controller.CCCompileErrorLoader;
import coco.model.CCCompileErrorManager;
import coco.view.CCMainFrame2;

public class RECocoViewerManager {
	REApplication application;

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppvフォルダに展開する

	public RECocoViewerManager(REApplication application) {
		this.application = application;
	}

	public void openCocoViewer() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		loadData(manager);
		new CCMainFrame2(manager).setVisible(true);
	}

	private void loadData(CCCompileErrorManager manager) {
		CCCompileErrorKindLoader kindLoader = new CCCompileErrorKindLoader(
				manager);
		String ppvRootPath = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).getAbsolutePath()
				.toString();
		kindLoader.load(ppvRootPath + "/MyErrorKinds.csv");
		CCCompileErrorLoader errorLoader = new CCCompileErrorLoader(manager);
		errorLoader.load(ppvRootPath + "/CompileErrorLog.csv");
	}
}
