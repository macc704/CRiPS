package ziptest;

import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new Main().run();
	}

	@SuppressWarnings("unchecked")
	void run() throws Exception {
		CDirectory dir = CFileSystem.findDirectory("data");
		List<CFile> files = dir.getFileChildren();
		for (CFile file : files) {
			System.out.println(file);
			ZipFile zipfile = new ZipFile(file.toJavaFile());
			Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipfile
					.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				System.out.println(entry);
			}
			zipfile.close();

			PPZipFileProject proj = new PPZipFileProject(file);
			show(proj.openLogFile());
		}
	}

	private void show(List<String> records) {
		for (String record : records) {
			System.out.println(record);
		}
	}
}
