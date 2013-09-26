package ronproeditor.ext;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.eclipse.jdt.core.dom.CompilationUnit;

import ronproeditor.ICFwResourceRepository;
import ronproeditor.REApplication;
import ronproeditor.helpers.CFrameUtils;
import bc.j2b.analyzer.JavaToBlockAnalyzer;
import bc.j2b.model.CompilationUnitModel;
import bc.utils.ASTParserWrapper;
import clib.common.thread.ICTask;
import clib.view.actions.CAction;
import clib.view.actions.CActionUtils;
import clib.view.screenshot.CScreenShotTaker;

public class REFlowViewerManager {

	private REApplication app;

	private JFrame viewerFrame;
	private JScrollPane scrollPane;

	public REFlowViewerManager(REApplication app) {
		this.app = app;

		app.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if (/*
							 * ICFwResourceRepository.PREPARE_DOCUMENT_CLOSE
							 * .equals(evt.getPropertyName()) ||
							 */ICFwResourceRepository.DOCUMENT_OPENED
								.equals(evt.getPropertyName())
						/*
						 * || ICFwResourceRepository.MODEL_REFRESHED
						 * .equals(evt.getPropertyName())
						 */) {
							refreshChart();
						}
					}
				});
	}

	public REApplication getApp() {
		return app;
	}

	public void refreshChart() {
		if (viewerFrame == null) {
			return;
		}

		// create panel
		String[] libs = app.getLibraryManager().getLibsAsArray();
		File javaFile = app.getSourceManager().getCurrentFile();

		if (javaFile == null) {
			return;
		}
		CompilationUnit unit = ASTParserWrapper.parse(javaFile,
				REApplication.SRC_ENCODING, libs);
		JavaToBlockAnalyzer visitor = new JavaToBlockAnalyzer(javaFile,
				REApplication.SRC_ENCODING);
		unit.accept(visitor);
		CompilationUnitModel root = visitor.getCompilationUnit();
		REFlowViewerPanel flowchartPanel = new REFlowViewerPanel(root);

		// “ü‚ê‘Ö‚¦
		scrollPane.setViewportView(flowchartPanel);
	}

	public void doOpenFlowViewer() {
		if (viewerFrame != null) {
			CFrameUtils.toFront(viewerFrame);
			refreshChart();
			return;
		}

		viewerFrame = new JFrame("Flow Viewer");
		viewerFrame.setBounds(100, 100, 500, 500);
		JMenuBar menuBar = new JMenuBar();
		viewerFrame.setJMenuBar(menuBar);
		JMenu menu = new JMenu("Menu");
		menuBar.add(menu);
		scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS/* _AS_NEEDED */);
		viewerFrame.getContentPane().add(scrollPane);
		{
			CAction a = CActionUtils.createAction("SS", new ICTask() {
				public void doTask() {
					CScreenShotTaker taker = new CScreenShotTaker(scrollPane
							.getViewport().getView());
					taker.takeToFile();
				}
			});
			a.setAcceralator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					KeyEvent.CTRL_DOWN_MASK));
			menu.add(a);
		}
		viewerFrame.setVisible(true);

		refreshChart();
	}
}
