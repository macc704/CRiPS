package obpro.file;
/*
 * BFile.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ファイルを表現するクラス（初心者用）
 * 
 * @author macchan
 * @version $Id: BFile.java,v 1.1 2007/06/13 07:45:06 macchan Exp $
 */
public class BFile {

	private File file = null;

	/**
	 * コンストラクタ
	 */
	public BFile(String filePath) {
		try {
			file = new File(filePath);
			if (!file.exists()) {
				makeDirectory(filePath);
				file.createNewFile();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 書き込みストリームを開く
	 */
	public BFileWriter openWriter() {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			return new BFileWriter(fileOutputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 読み込みストリームを開く
	 */
	public BFileReader openReader() {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			return new BFileReader(fileInputStream);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 必要なディレクトリを作成する
	 */
	public void makeDirectory(String filePath) {
		if (filePath.contains("/")) {
			String directoryPath = filePath.substring(0, filePath
					.lastIndexOf("/"));
			File parentDirectory = new File(directoryPath);
			parentDirectory.mkdirs();
		}
	}
}
