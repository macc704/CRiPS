package ch.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import bc.apps.JavaToBlockMain;
import edu.mit.blocks.controller.WorkspaceController;
import edu.mit.blocks.workspace.WorkspaceEvent;
import edu.mit.blocks.workspace.WorkspaceListener;

public class CHBlockEditorController {

	private WorkspaceController wc;
	private boolean isOpen = false;

	public CHBlockEditorController(String user) {
		wc = new WorkspaceController(user, true);
		
		wc.getWorkspace().addWorkspaceListener(new WorkspaceListener() {
			
			@Override
			public void workspaceEventOccurred(WorkspaceEvent event) {
				
				if (event.getEventType() == WorkspaceEvent.WORKSPACE_FINISHED_LOADING) {
					isOpen = true;
					addWindowListnerToWorkSpace();
				}
			}
		});
	}

	public void addWindowListnerToWorkSpace() {

		wc.getFrame().addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				isOpen = true;
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				isOpen = false;
			}
		}); 
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
		if (isOpen) {
			wc.setLangDefFilePath(langDefFilePath);
			wc.resetWorkspace();
			wc.loadProjectFromPath(xmlFilePath);
		}
	}

}
