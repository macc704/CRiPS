package obpro.common;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * キャストメソッド集です
 * 
 * @author macchan
 * @version $Id: BConverter.java,v 1.2 2007/06/14 02:50:53 macchan Exp $
 */
public class BConverter {

	//	short => String
	public static String shortToString(short s) {
		return Short.toString(s);
	}

	//	int => String
	public static String intToString(int i) {
		return Integer.toString(i);
	}

	//	long => String
	public static String longToString(long l) {
		return Long.toString(l);
	}

	//	float => String
	public static String floatToString(float f) {
		return Float.toString(f);
	}

	//	double => String
	public static String doubleToString(double d) {
		return Double.toString(d);
	}

	//	boolean => String
	public static String booleanToString(boolean b) {
		return Boolean.toString(b);
	}

	//	byte => String
	public static String byteToString(byte b) {
		return Byte.toString(b);
	}

	//	char => String
	public static String charToString(char c) {
		return Character.toString(c);
	}

	//	short => String
	public static short stringToShort(String string) {
		return Short.parseShort(string);
	}

	//	String => int
	public static int stringToInt(String string) {
		return Integer.parseInt(string);
	}

	//	String => long
	public static long stringToLong(String string) {
		return Long.parseLong(string);
	}

	//	String => float
	public static float stringToFloat(String string) {
		return Float.parseFloat(string);
	}

	//	String => double
	public static double stringToDouble(String string) {
		return Double.parseDouble(string);
	}

	//	String => boolean
	public static boolean stringToBoolean(String string) {
		return Boolean.getBoolean(string);
	}

	//Mapのkeyのリストを取得する
//	public static <T> ArrayList getKeys(Map<T, ?> map) {
//		return new ArrayList<T>(map.keySet());
//	}
//
//	//Mapインスタンスのvalueのリストを取得する
//	public static <T> ArrayList getValues(Map<?, T> map) {
//		return new ArrayList<T>(map.values());
//	}

	public static ArrayList/*<String>*/ split(String string, String regex) {
		String[] tokens = string.split(regex);
		return new ArrayList/*<String>*/(Arrays.asList(tokens));
	}

}