package presplugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import presplugin.adapter.EclipsePresAdapter;

/*
 * PresPlugin
 *
 * バージョンコメントは，README.txtへ
 */
public class PresPlugin extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "jp.ac.shizuoka.inf.cs.ce.pres.eclipseui";

	// The shared instance
	private static PresPlugin plugin;

	private EclipsePresAdapter pres;

	/**
	 * The constructor
	 */
	public PresPlugin() {
	}

	public void earlyStartup() {
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
		plugin = this;
		pres = new EclipsePresAdapter();
		pres.initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		pres.terminate();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PresPlugin getDefault() {
		if (plugin == null) {
			throw new RuntimeException();
		}
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

	public EclipsePresAdapter getPres() {
		return this.pres;
	}

}
