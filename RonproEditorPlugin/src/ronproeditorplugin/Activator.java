package ronproeditorplugin;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ppv.app.datamanager.PPProjectSet;
import ch.actions.CheCoProManager;
import ch.conn.framework.CHUserLogWriter;
import ch.library.CHFileSystem;
import clib.common.table.CCSVFileIO;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "RonproEditorPlugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private PPProjectSet ppProjectset = null;
	private boolean compileErrorCashCreating = false;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		File file = new File(System.getProperty("user.dir"));
		// System.out.println(System.getProperty("os.name"));
		if (!System.getProperty("user.dir").contains("testbase")
				&& System.getProperty("os.name").contains("Mac")) {
			System.setProperty("user.dir", file.getParentFile().getParentFile()
					.getParentFile().getAbsolutePath());
		}

		// System.setProperty("user.dir",
		// file.getParentFile().getParentFile().getAbsolutePath());
		plugin = this;

		String[][] table = new String[1][3];
		table = CCSVFileIO.load(CHFileSystem.getPrefFile());
		if (table.length == 0) {
			CheCoProManager.setLog(new CHUserLogWriter());
		} else {
			CheCoProManager.setLog(new CHUserLogWriter(table[0][0]));
		}
		CheCoProManager.getLog().eclipseOpen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		CheCoProManager.getLog().eclipseClose();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public PPProjectSet getppProjectset() {
		return ppProjectset;
	}

	public void setppProjectset(PPProjectSet ppProjectset) {
		this.ppProjectset = ppProjectset;
	}

	public void setcompileErrorCashCreating(boolean compileErrorCashCreating) {
		this.compileErrorCashCreating = compileErrorCashCreating;
	}

	public boolean getcompileErrorCashCreating() {
		return compileErrorCashCreating;
	}
}
