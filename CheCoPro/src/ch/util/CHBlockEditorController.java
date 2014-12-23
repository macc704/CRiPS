package ch.util;

import java.io.File;

import bc.apps.JavaToBlockMain;
import edu.mit.blocks.controller.WorkspaceController;

public class CHBlockEditorController {

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
		wc.openBlockEditor(xmlFilePath);
	}

	public void reloadBlockEditor(String langDefFilePath, String xmlFilePath) {
		if (wc.isOpened()) {
			wc.setLangDefFilePath(langDefFilePath);
			wc.resetWorkspace();
			wc.loadProjectFromPath(xmlFilePath);
		}
	}

}
