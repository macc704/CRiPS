package app.filenametranslator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ExtractedFileIterator implements Iterator<File> {

	private Enumeration<ZipEntry> e;
	private ZipFile zipFile;
	private File path;

	@SuppressWarnings("unchecked")
	public ExtractedFileIterator(ZipFile zipFile, File path) {
		this.zipFile = zipFile;
		if (path == null) {
			this.path = new File(System.getProperty("user.dir"));
		} else {
			this.path = path;
		}
		e = zipFile.getEntries();
	}

	@Override
	public boolean hasNext() {
		return e.hasMoreElements();
	}

	@Override
	public File next() {
		byte[] buffer = new byte[1024];
		int length = 0;
		ZipEntry ze = (ZipEntry) e.nextElement();
		InputStream inputStream = null;
		try {
			inputStream = zipFile.getInputStream(ze);
		} catch (ZipException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		FileOutputStream fos = null;
		File file = new File(path, ze.getName());
		try {
			fos = new FileOutputStream(file);
			while ((length = inputStream.read(buffer)) >= 0) {
				fos.write(buffer, 0, length);
			}
			fos.close();
			fos = null;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	@Override
	public void remove() {
	}

}
