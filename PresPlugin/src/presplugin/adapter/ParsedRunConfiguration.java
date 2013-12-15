package presplugin.adapter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ParsedRunConfiguration {

	private ILaunchConfiguration conf;

	protected ParsedRunConfiguration(ILaunchConfiguration conf) {
		this.conf = conf;
	}

	protected String getProjectName() {
		try {
			return conf.getAttribute("org.eclipse.jdt.launching.PROJECT_ATTR",
					"");
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	protected String getMainClassName() {
		try {
			return conf.getAttribute("org.eclipse.jdt.launching.MAIN_TYPE", "");
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	protected String getResourceFullPathString() {
		try {
			@SuppressWarnings("rawtypes")
			List paths = conf.getAttribute(
					"org.eclipse.debug.core.MAPPED_RESOURCE_PATHS",
					new ArrayList());
			return (String) paths.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	protected IProject getProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(
				getProjectName());
	}

	protected IFile getResourceFile() {
		IProject project = getProject();
		IPath path = new Path(".." + getResourceFullPathString());
		return project.getFile(path);
	}

}
