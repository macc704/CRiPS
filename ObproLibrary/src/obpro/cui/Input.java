package obpro.cui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * コンソールからの入力メソッドを提供するクラス
 * 
 * @author Manabu Sugiura
 * @version $Id: Input.java,v 1.1 2007/06/13 07:45:04 macchan Exp $
 */
public class Input {

	private static BufferedReader br;

	/**
	 * コンソールから文字を読み込む
	 * 
	 * @return String
	 */
	public static String getString() {
		String returnString = null;
		try {
			if (br == null) {
				br = new BufferedReader(new InputStreamReader(System.in));
			}
			returnString = br.readLine();
			return returnString;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * コンソールから数字を読み込む
	 * 
	 * @return int
	 */
	public static int getInt() {
		int returnInt = 0;
		returnInt = Integer.parseInt(getString());
		return returnInt;

	}

	/**
	 * コンソールからdouble型の数字を読み込む
	 * 
	 * @return double
	 */
	public static double getDouble() {
		double returnDouble = 0.0;
		returnDouble = Double.parseDouble(getString());
		return returnDouble;

	}

	/**
	 * 入力された文字列がint型に変換できるかどうかを調べる
	 * 
	 * @return int
	 */
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * 入力された文字列がint型に変換できるかどうかを調べる
	 * 
	 * @return int
	 */
	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * 任意の文字列を数字に変換する.
	 * 出力される数値は0～9999の値.
	 * 変換規則は特に意味のあるものではないが、
	 * 同一の入力に対して、同一の出力になることは保障する.
	 * @param string
	 * @return
	 */
	public static int convertStoI(String string) {
		return Math.abs(string.hashCode()) % 10000;
	}

}
