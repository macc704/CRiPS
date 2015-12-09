package ch.library;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;

public class CHLib {

	public static boolean isCorrectID(String id) {
		String regex = "70[0-9]{6}";
		return createMatcher(id, regex).find();
	}
	
	public static boolean isCorrectPass(String pass) {
		String regex = ".{4,12}";
		return createMatcher(pass, regex).find();
	}
	
	public static Matcher createMatcher(String str, String regex) {
		Pattern p = Pattern.compile(regex);
		return p.matcher(str);
	}
	
	public static String encrypt(String str) {
		return DigestUtils.md5Hex(str);
	}
	
	public static void main(String[] args) {
		if(isCorrectID("70010032")) {
			System.out.println("TRUE");
		} else {
			System.out.println("FALSE");
		}
		
		if (isCorrectPass("aA1-.")) {
			System.out.println("TRUE");
		} else {
			System.out.println("FALSE");
		}
	}
}
