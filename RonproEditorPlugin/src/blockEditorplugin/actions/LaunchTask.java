package blockEditorplugin.actions;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;

import ronproeditorplugin.Activator;

public class LaunchTask implements IWorkspaceRunnable {
	
	private IWorkbenchWindow window;
	public void run(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		monitor.beginTask("launch実験", 100);
		try {
			ILaunchConfiguration config = createConfiguration(new SubProgressMonitor(monitor, 20));
			launch(new SubProgressMonitor(monitor, 80), config, false);
		} finally {
			monitor.done();
		}
	}
	
	private ILaunchConfiguration createConfiguration(IProgressMonitor monitor) throws CoreException {
		IEditorPart editorPart = window.getActivePage().getActiveEditor();
		final IFileEditorInput fileEditorInput = (IFileEditorInput) editorPart
				.getEditorInput();
		IFile file = fileEditorInput.getFile();
		final File target = file.getLocation().toFile();
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy config = type.newInstance(null, Activator.PLUGIN_ID);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_ALLOW_TERMINATE, true);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, file.getProject().getName());
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, "");
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, target.getName());
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "");
		return config;
	}
	
	private void launch(IProgressMonitor monitor, ILaunchConfiguration config, boolean debugMode) throws CoreException {
		monitor.beginTask("launch", 100);
		try {
			String mode = debugMode ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE;
			boolean build = false;
			boolean register = false;
			ILaunch launch = config.launch(mode, new SubProgressMonitor(monitor, 20), build, register);
			if (!launch.hasChildren()) {
				throw new OperationCanceledException();
			}
			monitor.worked(10);

			while (!launch.isTerminated()) {
				checkCancel(monitor);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					launch.terminate();
					throw new OperationCanceledException();
				}
				monitor.worked(1);
			}
			launch.terminate();
		} finally {
			monitor.done();
		}
	}
	
	private static void checkCancel(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

}
