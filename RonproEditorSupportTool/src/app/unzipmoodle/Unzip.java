package app.unzipmoodle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class Unzip {

	public static List<File> list(File dir) {
		return Arrays.asList(dir.listFiles());
	}

	public static void unzip(File source, File dir) throws Exception {
		if (dir == null) {
			throw new IllegalArgumentException("dir is null");
		}
		ZipFile zipFile = new ZipFile(source, "UTF-8");
		for (@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> e = zipFile.getEntries(); e.hasMoreElements();) {
			ZipEntry ze = e.nextElement();
			extractTo(zipFile, ze, dir);
		}
	}

	private static void extractTo(ZipFile zipFile, ZipEntry zipEntry, File dir)
			throws Exception {
		File file = new File(dir, zipEntry.getName());
		FileOutputStream out = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int length = 0;
		InputStream in = zipFile.getInputStream(zipEntry);
		while ((length = in.read(buffer)) >= 0) {
			out.write(buffer, 0, length);
		}
		out.close();
		in.close();
	}

}
