package ziptest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import clib.common.filesystem.CFile;

public class PPZipFileProject implements IPPProject {

	private ZipFile zipfile;

	public PPZipFileProject(CFile file) {
		try {
			zipfile = new ZipFile(file.toJavaFile());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public List<String> openLogFile() {
		try {
			List<String> lines = new ArrayList<String>();
			ZipEntry logfile = zipfile.getEntry(".pres2/pres2.log");
			InputStream stream = zipfile.getInputStream(logfile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
			return lines;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
