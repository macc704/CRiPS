package cocoviewer.actions;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;

import ppv.app.datamanager.PPProjectSet;
import pres.core.IPRRecordingProject;
import pres.core.model.PRLog;
import pres.loader.logmodel.PRCocoViewerLog;
import presplugin.PresPlugin;
import ronproeditorplugin.Activator;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import coco.controller.CCCompileErrorKindLoader;
import coco.controller.CCCompileErrorLoader;
import coco.controller.CCMetricsLoader;
import coco.model.CCCompileErrorManager;
import coco.view.CCMainFrame2;

public class CocoViewerManager {

	// TODO: データを置く場所・ppvrootフォルダの場所
	private String PPV_ROOT_DIR = CFileSystem.getHomeDirectory()
			.findOrCreateDirectory(".ppvdata").getAbsolutePath().toString();
	private String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv";
	private String ERROR_LOG_FILE = "/CompileErrorLog.csv";
	private String METRICS_FILE = "/FileMetrics.csv";

	private CDirectory baseDir;
	private CDirectory libDir;

	private IWorkbenchWindow window;

	private CPath path;
	private IPRRecordingProject project;

	public CocoViewerManager(IWorkbenchWindow window) {
		if (Activator.getDefault().getppProjectset() == null) {
			int res = JOptionPane.showConfirmDialog(null,
					"CocoViewer用のデータが作成されていない可能性がありますが，よろしいですか？",
					"CocoViewer起動確認", JOptionPane.OK_CANCEL_OPTION);
			if (res != JOptionPane.OK_OPTION) {
				return;
			}
		}
		this.window = window;

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

		// アクティベーターに保持したコンパイル情報を呼び出し
		PPProjectSet projectset = Activator.getDefault().getppProjectset();
		manager.setPPProjectSet(projectset);

		writeCocoViewerLog(PRCocoViewerLog.SubType.COCOVIEWER_OPEN);
		manager.setProjectPath(path);
		manager.setRecordingProject(project);

		CCMainFrame2 frame = new CCMainFrame2(manager);
		frame.toFront();
		frame.setVisible(true);
	}

	private void writeCocoViewerLog(PRCocoViewerLog.SubType subType,
			Object... texts) {
		try {
			// if (!app.getSourceManager().hasCurrentFile()) {
			// return;
			// }

			IEditorPart editorPart = window.getActivePage().getActiveEditor();
			final IFileEditorInput fileEditorInput = (IFileEditorInput) editorPart
					.getEditorInput();
			IFile file = fileEditorInput.getFile();
			CFile target = (CFile) CFileSystem.convertToCFile(file
					.getLocation().toFile());
			CDirectory project = new CDirectory(new CPath(file.getProject()
					.getProject().getLocation().toFile()));

			CPath path = target.getRelativePath(project);
			this.path = path;

			PRLog log = new PRCocoViewerLog(subType, path, texts);
			writePresLog(log, file);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writePresLog(PRLog log, IFile file) {
		project = PresPlugin.getDefault().getPres().getManager()
				.getRecordingProject(file);

		project.record(log);
	}
}