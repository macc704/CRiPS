package ronproeditor.helpers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	/**
	 * targetFilesをZIP圧縮してzipFileに出力する。
	 * 
	 * @param zipFile
	 *            ZIP出力ファイル名
	 * @param targetFiles
	 *            入力ファイル名(ディレクトリ)配列
	 * @throws IOException
	 *             入出力例外
	 */
	public static void createZip(String zipFile, String targetDir ) throws IOException {
		createZip( zipFile, new String[]{ targetDir } );
	}
	public static void createZip(String zipFile, String[] targetFiles)
			throws IOException {

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		
//		if( OSUtil.isWindows() )
//			out.setEncoding("MS932"); 
//		else if( OSUtil.isMac() )
//			out.setEncoding("UTF-8");
		
		for (int i = 0; i < targetFiles.length; i++) {
			File file = new File(targetFiles[i]);
			createZip(out, file);
		}
		out.close();
	}

	/**
	 * targetFileをoutのZIPエントリへ出力する。
	 * 
	 * @param out
	 *            ZIP出力先
	 * @param targetFile
	 *            入力ファイル名(ディレクトリ)
	 * @throws IOException
	 *             入出力例外
	 */
	private static void createZip(ZipOutputStream out, File targetFile)
			throws IOException {

		if (targetFile.isDirectory()) {
			File[] files = targetFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				createZip(out, files[i]);
			}
		} else {
			ZipEntry target = new ZipEntry(getEntryPath(targetFile));
			out.putNextEntry(target);
			byte buf[] = new byte[1024];
			int count;
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(targetFile));
			while ((count = in.read(buf, 0, 1024)) != -1) {
				out.write(buf, 0, count);
			}
			in.close();
			out.closeEntry();
		}
	}

	/**
	 * ZIPエントリパスを返す。
	 * 
	 * @param file
	 *            ZIPエントリ対象ファイル
	 * @return ZIPエントリのパス
	 */
	private static String getEntryPath(File file) {
//		int deleteLength = file.getPath().length() - file.getName().length();
//		return file.getPath().replaceAll("\\\\", "/").substring(deleteLength);
		return file.getPath().replaceAll("\\\\", "/");
	}

}
