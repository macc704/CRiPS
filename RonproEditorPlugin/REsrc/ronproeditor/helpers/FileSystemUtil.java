/*
 * FileSystemUtil.java
 * Created on 2007/09/21 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import ronproeditor.REApplication;
import clib.common.system.CEncoding;
import clib.common.system.CEncodingDetector;

/**
 * FileSystemUtil
 */
public class FileSystemUtil {

	public static final String PATH_SEPARATOR = File.pathSeparator;
	public static final String SEPARATOR = File.separator;
	public static final String CR = System.getProperty("line.separator");

	public static String load(File f) {
		CEncoding encoding = CEncodingDetector.detect(f);
		return load(f, encoding.toString());
	}

	public static String load(File f, String enc) {
		try {
			StringBuffer buf = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(
					new FileInputStream(f), enc);
			// InputStreamReader isr = new InputStreamReader(
			// new FileInputStream(f), REApplication.ENCODING);
			BufferedReader reader = new BufferedReader(isr);
			while (reader.ready()) {
				buf.append(reader.readLine());
				if (reader.ready()) {
					buf.append(CR);
				}
			}
			reader.close();
			return buf.toString();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void save(File f, String text) {
		try {
			FileOutputStream fos = new FileOutputStream(f);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fos, REApplication.SRC_ENCODING));
			Scanner scanner = new Scanner(text);
			while (scanner.hasNext()) {
				writer.write(scanner.nextLine());
				if (scanner.hasNext()) {
					writer.newLine();
				}
			}
			writer.flush();
			writer.close();
			scanner.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String cutExtension(File file) {
		return cutExtension(file.getName());
	}

	public static String cutExtension(String filename) {
		int index = filename.lastIndexOf('.');
		if (index != -1) {
			return filename.substring(0, index);
		} else {
			return filename;
		}
	}

	public static JavaEnv createJavaEnv(File root, File file) {
		// dir
		root = root.getAbsoluteFile();
		File dir = file;
		while (!dir.getParentFile().equals(root)) {
			dir = dir.getParentFile();
		}

		// path
		int index = dir.getAbsolutePath().length() + 1;
		String path = file.getAbsolutePath().substring(index);

		// clname
		String classname = path;
		classname = MatcherUtil.replaceAll(classname, SEPARATOR, ".");
		classname = cutExtension(classname);

		return new JavaEnv(dir, path, classname);
	}

	/**
	 * ファイルをコピーする
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyFile(File source, File target) {
		try {
			// //エラーチェック
			// if (!source.exists()) {
			// throw new RuntimeException("ファイルが存在しません");
			// }
			// if (target.exists()) {
			// System.out.println("すでに存在するファイルを上書きします");
			// }

			// 前処理
			FileInputStream in = new FileInputStream(source);
			FileOutputStream out = new FileOutputStream(target);

			// コピーする
			byte[] buf = new byte[1024];
			int nByte = 0;
			while ((nByte = in.read(buf)) > 0) {
				out.write(buf, 0, nByte);
			}

			// 後処理
			in.close();
			out.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
