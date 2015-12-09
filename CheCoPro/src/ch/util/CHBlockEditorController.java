package ch.util;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import bc.apps.JavaToBlockMain;
import edu.mit.blocks.controller.WorkspaceController;

public class CHBlockEditorController {

	private WorkspaceController wc;
	private boolean fileOpened;
	
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
		if (fileOpened) {
			wc.setLangDefFilePath(langDefFilePath);
			wc.openBlockEditor(xmlFilePath);
		} else {
			wc.loadFreshWorkspace();
		}
	}

	public void reloadBlockEditor(final String langDefFilePath, final String xmlFilePath) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if (wc.isOpened() && fileOpened) {
					wc.setLangDefFilePath(langDefFilePath);
					wc.resetWorkspace();
					wc.loadProjectFromPath(xmlFilePath);
				} else if (wc.isOpened() && !fileOpened) {
					wc.loadFreshWorkspace();
				}
			}
		});
	}

	public void setFileOpened(boolean fileOpened) {
		this.fileOpened = fileOpened;
	}
	
	public JFrame getBlockEditorFrame() {
		return wc.getFrame();
	}

	public boolean isFileOpened() {
		return fileOpened;
	}
	
	public void close() {
		if (wc.isOpened()) {
			wc.getFrame().dispose();
		}
	}

}
