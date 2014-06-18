package obpro.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ObproPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "jp.ac.keio.sfc.crew.obpro";

	// The shared instance
	private static ObproPlugin plugin;
	public static final String OBPRO_JAR = "obpro.jar";
	public static final String OBPRO_JAR_VERSION = "obpro.version";
	public static final String OBPRO_JAR_PATH_VARIABLE = "OBPRO_LIB";

	public static final String TEMPLATE_ENCODING = "SJIS";

	/**
	 * The constructor
	 */
	public ObproPlugin() {
		plugin = this;
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ObproPlugin getDefault() {
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

	/**********************************************
	 * オブプロライブラリ関係
	 **********************************************/

	/**
	 * update library jar file in workspace.
	 * 
	 * @throws Exception
	 */
	public void updateLibrary() throws Exception {
		// ライブラリのバージョンを調べ、更新の必要性があるか調べる
		URL newVersionURL = getURL(OBPRO_JAR_VERSION);
		double newVersion = getLibraryVersion(newVersionURL.openStream());
		IPath currentVersionPath = getStateLocation().append(OBPRO_JAR_VERSION);
		double currentVersion = getLibraryVersion(currentVersionPath.toFile());
		boolean shouldUpdateTurtle = newVersion > currentVersion;

		// ライブラリを更新する
		if (shouldUpdateTurtle) {
			URL turtleURL = getURL(OBPRO_JAR);
			InputStream input = turtleURL.openStream();

			IPath newTurtle = getLibraryPath();
			File newTurtleFile = newTurtle.makeAbsolute().toFile();
			OutputStream output = new FileOutputStream(newTurtleFile);

			fileCopy(input, output);
			fileCopy(newVersionURL.openStream(), new FileOutputStream(
					currentVersionPath.toFile()));
		}

		// ライブラリのクラスパス変数を設定
		JavaCore.setClasspathVariable(OBPRO_JAR_PATH_VARIABLE,
				getLibraryPath(), null);
		// IPath path = JavaCore.getClasspathVariable(OBPRO_JAR_PATH_VARIABLE);
		// System.out.println(path);
	}

	public IPath getLibraryPath() {
		IPath pluginBase = getStateLocation();
		IPath libraryPath = pluginBase.append(OBPRO_JAR);
		return libraryPath;
	}

	private void fileCopy(InputStream in, OutputStream out)
			throws FileNotFoundException, IOException {
		// コピーする
		byte[] buf = new byte[1024];
		int nByte = 0;
		while ((nByte = in.read(buf)) > 0) {
			out.write(buf, 0, nByte);
		}

		// 後処理
		in.close();
		out.close();
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public URL getURL(String filePath) {
		if (filePath == null) {
			throw new IllegalArgumentException();
		}

		// if the bundle is not ready then there is no image
		if (!BundleUtility.isReady(getBundle())) {
			return null;
		}

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = BundleUtility.find(getBundle(), filePath);
		if (fullPathString == null) {
			try {
				fullPathString = new URL(filePath);
			} catch (MalformedURLException e) {
				return null;
			}
		}

//		if (fullPathString == null) {
//			return null;
//		}

		return fullPathString;
	}

	public double getLibraryVersion(File file) {
		try {
			return getLibraryVersion(new FileInputStream(file));
		} catch (Exception ex) {
			return -1;
		}
	}

	public double getLibraryVersion(InputStream input) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String line = br.readLine();
			br.close();
			return Double.parseDouble(line);
		} catch (Exception ex) {
			return 0d;
		}
	}

	public static boolean isObproProject(IJavaProject project) {
		try {
			IClasspathEntry[] cps = project.getRawClasspath();
			for (int i = 0; i < cps.length; i++) {
				if (cps[i].getPath().toString().equals(OBPRO_JAR_PATH_VARIABLE)) {
					return true;
				}
			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}
}
