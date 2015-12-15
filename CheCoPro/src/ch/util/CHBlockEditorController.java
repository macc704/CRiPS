package ch.util;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import bc.apps.JavaToBlockMain;
import edu.mit.blocks.controller.WorkspaceController;

public class CHBlockEditorController {

	public static String DEFAULT_LANGDEF_PATH = "ext/block/lang_def.xml";
	
	private WorkspaceController wc;

	public CHBlockEditorController(String user) {
		wc = new WorkspaceController(user, true);
	}

	public String createXmlFromJava(File selectedFile, String encoding, String[] libs) {
		try {
			return new JavaToBlockMain(true).run(selectedFile, encoding, libs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void openBlockEditor(String langDefFilePath, String xmlFilePath) {
		wc.setLangDefFilePath(langDefFilePath);
		if (xmlFilePath.equals("")) {
			wc.loadFreshWorkspace();
			wc.createAndShowGUI();
		} else {
			wc.openBlockEditor(xmlFilePath);
		}
	}

	public void reloadBlockEditor(String langDefFilePath, String xmlFilePath) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				wc.setLangDefFilePath(langDefFilePath);
				wc.resetWorkspace();
				wc.loadFreshWorkspace();
				if (!xmlFilePath.equals("")) {
					wc.loadProjectFromPath(xmlFilePath);
				}
			}
		});
	}
	
	public WorkspaceController getWorkspaceController() {
		return wc;
	}
	
	public JFrame getBlockEditorFrame() {
		return wc.getFrame();
	}

	public void close() {
		if (wc.isOpened()) {
			wc.getFrame().dispose();
		}
	}

}
