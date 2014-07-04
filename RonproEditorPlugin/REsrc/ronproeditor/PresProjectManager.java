package ronproeditor;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import pres.core.IPRRecordingProject;
import pres.core.PRNullRecordingProject;
import pres.core.PRRecordingProject;
import pres.core.PRThreadRecordingProject;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileFilter;

public class PresProjectManager {

	private Map<String, IPRRecordingProject> rprojects;

	private static final IPRRecordingProject NULL_PROJECT = new PRNullRecordingProject();

	public PresProjectManager() {
		initialize();
	}

	public synchronized void initialize() {
		rprojects = new LinkedHashMap<String, IPRRecordingProject>();
	}

	public synchronized void terminate() {
		try {
			for (IPRRecordingProject rproject : rprojects.values()) {
				rproject.checkTargetFilesAndUpdate();
				rproject.stop();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void refresh() {
		try {
			for (Iterator<IPRRecordingProject> i = rprojects.values()
					.iterator(); i.hasNext();) {
				IPRRecordingProject rproject = i.next();
				if (!rproject.valid()) {
					rproject.stop();
					i.remove();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public synchronized IPRRecordingProject getRecordingProject(
			CDirectory project) {
		try {
			refresh();
			String pjName = project.getNameByString();
			if (!rprojects.containsKey(pjName)) {
				IPRRecordingProject newRProject = createNewRecordingProject(project);
				if (newRProject.valid()) {
					newRProject.start();
					newRProject.checkTargetFilesAndUpdate();
					rprojects.put(pjName, newRProject);
				}
			}
			IPRRecordingProject rProject = rprojects.get(pjName);
			if (rProject == null) {
				return NULL_PROJECT;
			}
			return rProject;

		} catch (Exception ex) {
			ex.printStackTrace();
			return NULL_PROJECT;
		}
	}

	private IPRRecordingProject createNewRecordingProject(CDirectory project) {
		PRRecordingProject rproject = new PRRecordingProject(project);
		rproject.setDirFilter(CFileFilter.IGNORE_BY_NAME_FILTER(".*", "CVS",
				"bin"));
		rproject.setFileFilter(CFileFilter.ACCEPT_BY_NAME_FILTER("*.java",
				"*.hcp", "*.c", "*.cpp", "Makefile", "*.oil", "*.rb", "*.bat",
				"*.tex"));
		return new PRThreadRecordingProject(rproject);
	}

}
