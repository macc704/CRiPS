package obpro.file;

/*
 * BFileReader.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ファイル読み込みストリームを表現するクラス（初心者用）
 * 
 * @author macchan
 * @version $Id: BFileReader.java,v 1.1 2007/06/13 07:45:06 macchan Exp $
 */
public class BFileReader {

	private String buf = null;

	private BufferedReader reader = null;

	/**
	 * コンストラクタ
	 */
	public BFileReader(InputStream inputStream) {
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
	}

	/**
	 * ストリームを最後まで読み込んだかどうか調べる
	 * （このときに、次の行を読み込んでしまう）
	 */
	public boolean isEndOfFile() {
		try {
			if (buf == null) {
				buf = this.reader.readLine();
			}
			return buf == null;
		} catch (IOException ex) {
			ex.printStackTrace();
			return true;
		}
	}

	/**
	 * 一行読み込む
	 */
	public String readLine() {
		if (isEndOfFile()) {
			return null;
		}
		String read = buf;
		buf = null;
		return read;
	}

	/**
	 * ストリームを閉じる
	 */
	public void close() {
		try {
			this.reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}