package obpro.file;

/*
 * BFileWriter.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * ファイル書き込みストリームを表現するクラス（初心者用）
 * 
 * @author macchan
 * @version $Id: BFileWriter.java,v 1.1 2007/06/13 07:45:06 macchan Exp $
 */
public class BFileWriter {

	private PrintStream printStream;

	/**
	 * コンストラクタ
	 */
	public BFileWriter(FileOutputStream fileOutputStream) {
		printStream = new PrintStream(fileOutputStream);
	}

	/**
	 * 文字列を書き込む
	 */
	public void print(String s) {
		printStream.print(s);
	}
	
	/**
	 * 整数を書き込む
	 */
	public void print(long i) {
		printStream.print(i);
	}
	
	/**
	 * 実数を書き込む
	 */
	public void print(double d) {
		printStream.print(d);
	}
	
	/**
	 * 文字を書き込む
	 */
	public void print(char c) {
		printStream.print(c);
	}

	/**
	 * 文字列を書き込んで、改行する
	 */
	public void println(String s) {
		print(s);
		println();
	}
	
	/**
	 * 整数を書き込む
	 */
	public void println(long i) {
		printStream.println(i);
	}
	
	/**
	 * 実数を書き込む
	 */
	public void println(double d) {
		printStream.println(d);
	}
	
	/**
	 * 文字を書き込む
	 */
	public void println(char c) {
		printStream.println(c);
	}
	
	/**
	 * 改行する
	 */
	public void println() {
		printStream.println();
	}

	/**
	 * ストリームを閉じる
	 */
	public void close() {
		this.printStream.close();
	}

}