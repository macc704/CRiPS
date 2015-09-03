package app.filenametranslator;

import java.io.File;
import java.util.Iterator;

import org.apache.tools.zip.ZipFile;

public class SourceFile {

	// private File file;
	private ZipFile zipFile;
	private File path = null;

	// private String studentId;
	// private String filename;

	public SourceFile(File file) throws Exception {
		// this.file = file;
		this.zipFile = new ZipFile(file, "UTF-8");
	}

	public void setExtractPath(File path) {
		this.path = path;
	}

	@SuppressWarnings("rawtypes")
	public Iterator iterator() {
		return new ExtractedFileIterator(zipFile, path);
	}

	// private void decode(File zipf) throws Exception {
	// ZipFile zf = new ZipFile(zipf, "UTF-8");
	// for (Enumeration e = zf.getEntries(); e.hasMoreElements();) {
	// ZipEntry ze = (ZipEntry) e.nextElement();
	// System.out.println(ze.getName());
	// if (ze.isDirectory())
	// continue;
	//
	// InputStream is = zf.getInputStream(ze);
	// // for (;;) {
	// // int len = is.read(buf);
	// // if (len < 0) break;
	// // //bufを使って処理
	// // }
	// // is.close();
	// }
	// zf.close();
	// }

	// private void studentId(Map<String, String> map) {
	// String name = filename.substring(0, filename.indexOf('_'));
	// if (map.get(name) != null)
	// studentId = map.get(name);
	// }

	// private void convertCharset(String charset)
	// throws UnsupportedEncodingException {
	// String filename = file.getName();
	// for (int i = 0; i < filename.length(); i++) {
	// char c = filename.charAt(i);
	// byte b1 = (byte) (c >> 8);
	// byte b2 = (byte) c;
	// System.out.printf("%02X %02X ", b1, b2);
	// }
	// System.out.println();
	// byte[] bytes = filename.getBytes();
	// this.filename = new String(bytes, "UTF-8");
	// System.out.print(this.filename + ": ");
	// for (byte b : bytes) {
	// System.out.printf("%02X ", b);
	// }
	// System.out.println();
	// /**
	// * 五E4BA94　→　E4 BA 94 十E58D81　→　E5 8D 81 嵐E5B590　→　E5 B5 81 幹E5B9B9　→　45
	// * E5 B9 夫E5A4AB　→　E5 A4 AB
	// *
	// */
	// }

	// private void rename(int noLecture) {
	// File after = new File(file.getParent() + File.separator + studentId
	// + "-" + noLecture + ".zip");
	// if (!file.renameTo(after)) {
	// System.out.println("変換失敗");
	// }
	// }
}
