package common.resource;

import java.io.File;
import java.net.URL;

/**
 * @author camei (original version)
 * @version $Id: Resource.java,v 1.1 2007/10/08 11:23:24 macchan Exp $
 */
public class CResourceFinder {

	private static final boolean DEBUG = false;

	private CResourceFinder() {
	}

	public static URL getResource(String path) {
		return getResource(path, CResourceFinder.class);
	}

	public static URL getResource(String path, Class<?> clazz) {
		if (DEBUG) {
			System.out.println("getResource()");
			System.out.println(clazz);
		}
		URL url = null;

		// 0. try url
		try {
			url = new URL(path);
			if (url != null) {
				return url;
			}
		} catch (Exception ex) {
			if (DEBUG) {
				ex.printStackTrace();
			}
		}

		// 1. try class local resource loader
		try {
			if (DEBUG) {
				System.out.println("1 local");
			}
			url = clazz.getResource(path);
			if (url != null) {
				return url;
			}
		} catch (Exception ex) {
			// Security exception -> do nothing
			if (DEBUG) {
				ex.printStackTrace();
			}
		}

		// 2. try class loader
		try {
			if (DEBUG) {
				System.out.println("2 class loader");
			}
			url = ClassLoader.getSystemClassLoader().getResource(path);
			if (url != null) {
				return url;
			}
		} catch (Exception ex) {
			// Security exception -> do nothing
			if (DEBUG) {
				ex.printStackTrace();
			}
		}

		// 3. try system class loader
		try {
			if (DEBUG) {
				System.out.println("3 system class loader");
			}
			url = ClassLoader.getSystemClassLoader().getResource(path);
			if (url != null) {
				return url;
			}
		} catch (Exception ex) {
			// Security exception -> do nothing
			if (DEBUG) {
				ex.printStackTrace();
			}
		}

		// 4. try file system
		try {
			if (DEBUG) {
				System.out.println("4 file system");
			}
			url = new File(path).toURI().toURL();
			if (url != null) {
				return url;
			}
		} catch (Exception ex) {
			// any exception -> do nothing
			if (DEBUG) {
				ex.printStackTrace();
			}
		}

		return null;
	}

	public static void main(String[] args) {
	}
}
