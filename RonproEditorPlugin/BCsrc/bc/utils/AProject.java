package bc.utils;

import java.io.File;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * 建設中
 */
@Deprecated
public class AProject extends Project {

	public static AProject create(File dir) {
		if (!dir.isDirectory()) {
			throw new RuntimeException("dir is not directory");
		}
		IPath path = new Path(dir.getAbsolutePath());
		Workspace.defaultWorkspaceDescription();
		Workspace ws = new Workspace();
		return new AProject(path, ws);
	}

	public AProject(IPath path, Workspace container) {
		super(path, container);
	}

}
