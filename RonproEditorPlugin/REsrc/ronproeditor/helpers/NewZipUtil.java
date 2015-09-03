package ronproeditor.helpers;

import java.io.BufferedInputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;

public class NewZipUtil {

	public static void createZip(CFile zip, CDirectory base,
			CFileElement... targets) {
		try {
			ZipOutputStream out = new ZipOutputStream(zip.openOutputStream());
			for (CFileElement target : targets) {
				createZip(out, base, target);
			}
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void createZip(ZipOutputStream out, CDirectory base,
			CFileElement target) throws Exception {

		if (target.isDirectory()) {
			CDirectory targetDir = ((CDirectory) target);
			List<CFileElement> children = targetDir.getChildren();
			for (CFileElement child : children) {
				createZip(out, base, child);
			}
		} else {// File
			CFile targetFile = ((CFile) target);
			ZipEntry entry = new ZipEntry(createEntryPath(base, target));
			out.putNextEntry(entry);
			byte buf[] = new byte[1024];
			int count;
			BufferedInputStream in = new BufferedInputStream(targetFile
					.openInputStream());
			while ((count = in.read(buf, 0, 1024)) != -1) {
				out.write(buf, 0, count);
			}
			in.close();
			out.closeEntry();
		}
	}

	private static String createEntryPath(CDirectory base,
			CFileElement fileElement) {
		return fileElement.getRelativePath(base).toString().replaceAll("\\\\",
				"/");
	}
}
