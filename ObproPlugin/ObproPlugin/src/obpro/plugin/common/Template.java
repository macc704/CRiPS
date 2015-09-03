/*
 * Created on 2007/06/13
 *
 * Copyright (c) 2007 camei.  All rights reserved.
 */
package obpro.plugin.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import obpro.plugin.ObproPlugin;

/**
 * テンプレートジェネレータ Javaのクラス作成に特化してます。
 * 
 * @author camei
 * @version $Id: Template.java,v 1.2 2009/05/08 09:20:50 macchan Exp $
 */
public class Template {

	// 公開定数
	public static final String LINE_DELIMITER = System
			.getProperty("line.separator");

	// 定数
	private static Pattern variablePattern = Pattern
			.compile("\\$\\{([^\\}]*)\\}");

	// 属性
	private Map<String, String> variables = new HashMap<String, String>();

	// : String>

	public String generate(URL url) {
		try {
			InputStream inputStream = url.openStream();
			return generate(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String generate(InputStream inputStream) {
		StringWriter os = new StringWriter();
		generate(os, inputStream);
		return os.toString();
	}

	/**
	 * @param reader
	 * @param os
	 */
	public void generate(Writer os, InputStream inputStream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, ObproPlugin.TEMPLATE_ENCODING));
			BufferedWriter writer = new BufferedWriter(os);

			String line;
			while ((line = reader.readLine()) != null) {
				// 変数部分を置換する
				String replaced = replaceVariable(line);

				// 置換結果を出力
				writer.write(replaced);
				writer.newLine();
			}
			reader.close();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param line
	 * @return
	 */
	private String replaceVariable(String line) {
		// 変数表現にマッチさせる
		Matcher matcher = variablePattern.matcher(line);

		// マッチした部分を置換
		StringBuffer replaced = new StringBuffer();
		while (matcher.find()) {
			String key = matcher.group(1);
			String variable = getVariable(key);
			if (variable == null) {
				variable = "unknown";
			}
			matcher.appendReplacement(replaced, variable);
		}
		matcher.appendTail(replaced);
		return replaced.toString();
	}

	/**********
	 * 変数設定
	 **********/

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void setVariable(String key, String value) {
		variables.put(key, value);
	}

	public String getVariable(String key) {
		return (String) variables.get(key);
	}

	public boolean containsKey(String key) {
		return variables.containsKey(key);
	}

	/*********
	 * utility
	 *********/

	/**
	 * クラス名を変数名に変換
	 * 
	 * @param className
	 *            クラス名
	 * @return クラス名をjava規約に基づき変数名にした文字列
	 */
	public static String toInstanceName(String className) {
		return className.substring(0, 1).toLowerCase() + className.substring(1);
	}

	public static String createImportText(String[] classPaths) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < classPaths.length; i++) {
			buf.append("import " + classPaths[i] + ";" + LINE_DELIMITER);
		}
		return buf.toString();

	}

	/*********
	 * test
	 *********/

	public static void main(String[] args) {
		new Template().test();
	}

	private void test() {
		String templateData = "僕は${name}です。\n年齢は${age}歳です。";

		setVariable("name", "太郎\n改行して");
		setVariable("age", "31");

		System.out.println(replaceVariable(templateData));
	}
}
