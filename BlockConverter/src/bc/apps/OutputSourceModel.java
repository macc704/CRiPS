package bc.apps;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import bc.BCSystem;
import bc.j2b.analyzer.JavaCommentManager;
import bc.utils.ASTParserWrapper;
import bc.utils.FileReader;

public class OutputSourceModel {

	private File file;
	private String enc;
	private String[] classpaths;

	private CompilationUnit unit; // cash

	private Map<String, String> requests = new LinkedHashMap<String, String>();
	private Map<String, String> constructorRequests = new LinkedHashMap<String, String>();// ohata
																							// added
	private Map<String, String> privateRequests = new LinkedHashMap<String, String>();// ohata
																						// added

	public OutputSourceModel(File file, String enc, String[] classpaths) {
		this.file = file;
		this.enc = enc;
		this.classpaths = classpaths;

	}

	public void save() throws Exception {
		// #ohata プライベート変数の作成と変換
		createPrivateValues();
		replacePrivateValues();
		// 2013.09.10 #ohata 予定ではここでコンストラクタの作成と変換を行う予定
		// createConstructors();
		// replaceConstructors();
		// 2013.09.10 #ohata 現状はここでコンストラクタの作成、変換を行っている
		createNewMethods();// まず，ないメソッドは作る
		replace();
	}

	// #ohata added
	private void replacePrivateValues() throws Exception {

		this.unit = ASTParserWrapper.parse(file, enc, classpaths);
		String src = FileReader.readFile(file, enc);

		BCSystem.out.println("read file:" + src);

		List<FieldDeclaration> privateValues = getPrivateValues();
		Collections.reverse(privateValues);
		for (FieldDeclaration privateValue : privateValues) {
			String name = getPrivateValueName(privateValue.fragments().get(0)
					.toString());
			if (privateRequests.containsKey(name)) {
				// 入替
				String blockString = privateRequests.get(name).substring(0,
						privateRequests.get(name).length() - 1);
				String oldString = getOldPrivateString(name, src);
				src = src.replace(oldString, blockString);
			} else {
				// 消去
				String blockString = "";
				String oldString = getOldPrivateString(name, src);
				src = src.replace(oldString, blockString);
			}
		}
		PrintStream ps = new PrintStream(file, enc);
		ps.print(src);
		ps.close();
	}

	private void replace() throws Exception {
		this.unit = ASTParserWrapper.parse(file, enc, classpaths);
		String src = FileReader.readFile(file, enc);

		// "今ある順の"　後ろから置き換え処理 (しないと，後のメソッド位置が都度後方へずれるため)
		List<MethodDeclaration> methods = getMethods();
		Collections.reverse(methods);
		for (MethodDeclaration method : methods) {
			String name = method.getName().toString();
			if ((method.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
				continue;
			}
			if (requests.containsKey(name)) {
				// 入替
				String blockString = requests.get(name);
				String oldString = getOldString(name, src);
				src = src.replace(oldString, blockString);
			} else {
				// 消去
				String blockString = "";
				String oldString = getOldString(name, src);
				src = src.replace(oldString, blockString);
			}
		}

		// private変数のリストを取ってくる
		// すべてのリスト要素に対し、メソッド同様に入れ替え処理を行う
		PrintStream ps = new PrintStream(file, enc);
		BCSystem.out.println("print src:" + src + "at output source model");
		ps.print(src);
		ps.close();
	}

	private void createPrivateValues() throws Exception {// #ohata added
		this.unit = ASTParserWrapper.parse(file, enc, classpaths);
		// check
		List<String> newNames = calcNewPrivateValueNames();
		if (newNames.size() <= 0) {
			return;
		}

		createNewPrivateValue(newNames);
	}

	private void createNewMethods() throws Exception {
		this.unit = ASTParserWrapper.parse(file, enc, classpaths);
		// check
		List<String> newNames = calcNewNames();

		if (newNames.size() <= 0) {
			return;
		}
		createNewMethods(newNames);
	}

	private void createNewPrivateValue(List<String> newNames) throws Exception {// #ohata
																				// added
		String src = FileReader.readFile(file, enc);
		int cursor = getFirstPrivateVariableStartPosition();

		for (String newName : newNames) {
			String blockString = privateRequests.get(newName);
			BCSystem.out.println("blockString:" + blockString);
			String newSrc = src.substring(0, cursor) + blockString
					+ src.substring(cursor);
			src = newSrc;
		}

		PrintStream ps = new PrintStream(file, enc);
		ps.print(src);
		ps.close();
	}

	private int getFirstPrivateVariableStartPosition() {// #ohata added
		int cursor;

		List<FieldDeclaration> privateValues = getPrivateValues();

		if (privateValues.size() == 0) {
			cursor = getFirstMethodBeginPosition();
		} else {
			cursor = privateValues.get(0).getStartPosition();
		}
		return cursor;
	}

	/*
	 * private int getLastPrivateValueEndPosition() { int cursor;
	 * List<FieldDeclaration> privateValues = getPrivateValues();
	 * JavaCommentManager cm = new JavaCommentManager(file, enc);
	 * 
	 * if (privateValues.size() <= 0) { return -1; } else { //int len =
	 * privateValues.get(privateValues.size() - 1).getLength(); int start =
	 * privateValues.get(privateValues.size() - 1) .getStartPosition(); cursor =
	 * cm.getLineCommentEndPosition(start); }
	 * 
	 * return cursor;
	 * 
	 * }
	 */
	private void createNewMethods(List<String> newNames) throws Exception {
		String src = FileReader.readFile(file, enc);

		int cursor = getLastMethodFinishPosition();

		for (String newName : newNames) {
			String newStub = "\n\n" + "void " + newName + "(){}";
			String newSrc = src.substring(0, cursor) + newStub
					+ src.substring(cursor);
			src = newSrc;
		}

		PrintStream ps = new PrintStream(file, enc);
		ps.print(src);
		ps.close();
	}

	private int getFirstMethodBeginPosition() {
		List<MethodDeclaration> methods = getMethods();
		int start;
		if (methods.size() <= 0) {// 現状の仕様では，メソッドが一つ以上ないといけない
			Pattern p = Pattern
					.compile("(public)?[ ]+class[ ]+(extends[ ]+)?.+[ ]+[{]");
			String src = FileReader.readFile(file, enc);
			Matcher m = p.matcher(src);
			if (m.find()) {
				start = m.group().length();
			} else {
				throw new RuntimeException("Class Declaration Not Found.");
			}
		} else {
			MethodDeclaration last = methods.get(0);

			start = last.getStartPosition();
		}
		return start;
	}

	private int getLastMethodFinishPosition() {
		List<MethodDeclaration> methods = getMethods();
		int end;
		if (methods.size() <= 0) {// 現状の仕様では，メソッドが一つ以上ないといけない
			// public class hoge{までの文字数を獲得する
			// (public)? +class + (.)+ +{
			Pattern p = Pattern
					.compile("(public)?[ ]+class[ ]+(extends[ ]+)?.+[ ]+[{]");
			String src = FileReader.readFile(file, enc);
			Matcher m = p.matcher(src);
			if (m.find()) {
				end = m.group().length();
			} else {
				throw new RuntimeException("Class Declaration Not Found.");
			}

		} else {
			MethodDeclaration last = methods.get(methods.size() - 1);
			int start = last.getStartPosition();
			int len = last.getLength();
			end = start + len;
		}

		// the last position
		return end;
	}

	private List<String> calcNewPrivateValueNames() {
		BCSystem.out.println("calc newPrivateNames");
		List<String> newNames = new ArrayList<String>();

		for (String privateRequest : privateRequests.keySet()) {
			if (findPrivateValue(privateRequest) == null) {
				newNames.add(privateRequest);
			}
		}
		BCSystem.out.println("calc New Names return :" + newNames);
		return newNames;
	}

	private List<String> calcNewNames() {
		BCSystem.out.println("calc newNames");
		List<String> newNames = new ArrayList<String>();
		List<String> names = new ArrayList<String>(requests.keySet());

		for (String name : names) {
			BCSystem.out.println("name:" + name);
			if (findMethod(name) == null) {
				newNames.add(name);
			}
		}

		BCSystem.out.println("calc New Names return :" + newNames);
		return newNames;
	}

	/*
	 * private Boolean checkPrivateVariables(String name){ for(String
	 * privateVariable : privateRequests){ if(privateVariable.equals(name)){
	 * System.out.println("privateValue!"); return true; } } return false; }
	 */

	private List<MethodDeclaration> getMethods() {
		List<TypeDeclaration> types = getTypes();
		if (types.size() == 0) {
			throw new RuntimeException("Class Declaration Not Found.");
		}
		if (types.size() > 1) {
			throw new RuntimeException(
					"More than two Class Declaration has been Found.");
		}
		BCSystem.out.println("types.get(0).getMethods:"
				+ types.get(0).getMethods());
		BCSystem.out.println("get(0) end");
		return Arrays.asList(types.get(0).getMethods());
	}

	private List<FieldDeclaration> getPrivateValues() {
		List<TypeDeclaration> types = getTypes();
		if (types.size() == 0) {
			throw new RuntimeException("Class Declaration Not Found.");
		}
		if (types.size() > 1) {
			throw new RuntimeException(
					"More than two Class Declaration has been Found.");
		}
		BCSystem.out.println("types.get(0):" + types.get(0));
		BCSystem.out.println("get(0) end");

		return Arrays.asList(types.get(0).getFields());
	}

	private List<TypeDeclaration> getTypes() {
		List<TypeDeclaration> types = new ArrayList<TypeDeclaration>();
		for (Object each : unit.types()) {
			BCSystem.out.println("unit.types:" + unit.types());
			if (each instanceof TypeDeclaration) {
				types.add((TypeDeclaration) each);
			}
		}
		BCSystem.out.println("types at getTypes:" + types);
		return types;
	}

	private FieldDeclaration findPrivateValue(String name) {

		String value = privateRequests.get(name);
		for (FieldDeclaration privateValue : getPrivateValues()) {
			String fragmentsValue = getFragmentsValue(privateValue.fragments()
					.get(0).toString());
			BCSystem.out.println("fragmentsValue:" + fragmentsValue + "value:"
					+ value);
			BCSystem.out
					.println("getPrivateValueName(variable.fragments().get(0).toString()):"
							+ getPrivateValueName(privateValue.fragments()
									.get(0).toString()));
			BCSystem.out.println("name:" + name);
			if (getPrivateValueName(privateValue.fragments().get(0).toString())
					.equals(name)) {
				BCSystem.out.println("find same private value:" + name);
				return privateValue;
			}
		}
		return null;
	}

	private String getFragmentsValue(String fragment) {
		int index = fragment.indexOf("=");
		if (index == -1) {
			return null;
		}
		return fragment.substring(index + 1, fragment.length());
	}

	private String getPrivateValueName(String fragment) {
		int index = fragment.indexOf("=");
		if (index == -1) {
			return fragment;
		}
		return fragment.substring(0, index);
	}

	private MethodDeclaration findMethod(String name) {
		for (MethodDeclaration method : getMethods()) {
			if (method.getName().toString().equals(name)) {
				return method;
			}
		}
		return null;
	}

	/*
	 * private VariableDeclarationFragment findPrivateVariable(String name){ for
	 * (VariableDeclarationFragment privateVariable : getPrivateValue()) { if
	 * (privateVariable.getName().toString().equals(name)) { return
	 * privateVariable; } } return null; }
	 */

	private String getOldPrivateString(String name, String src)
			throws Exception {
		// #ohata
		FieldDeclaration privateValue = findPrivateValue(name);
		if (privateValue == null) {
			throw new RuntimeException("private value not found" + name);
		}

		int start = privateValue.getStartPosition();
		int len = privateValue.getLength();
		int end = start + len;
		String oldPrivateString = "";

		JavaCommentManager jcm = new JavaCommentManager(file, enc);
		// フィールド変数終了ポジションから改行までの間にコメントがあるか確認し、コメントがある場合はコメント込の文字列を取得する
		if (jcm.getLineCommentPosition(end) != -1) {
			String comment = jcm
					.getLineComment(jcm.getLineCommentPosition(end));
			oldPrivateString = src.substring(start,
					jcm.getLineCommentPosition(end) + comment.length() + 2);
		} else {
			oldPrivateString = src.substring(start, end);
		}

		return oldPrivateString;
	}

	private String getOldString(String name, String src) {
		MethodDeclaration method = findMethod(name);
		if (method == null) {
			throw new RuntimeException("method not found:" + name);
		}
		int start = method.getStartPosition();
		int len = method.getLength();
		int end = start + len;
		String oldString = "";

		JavaCommentManager jcm = new JavaCommentManager(file, enc);
		// フィールド変数終了ポジションから改行までの間にコメントがあるか確認し、コメントがある場合はコメント込の文字列を取得する
		if (jcm.getLineCommentPosition(end) != -1) {
			String comment = jcm
					.getLineComment(jcm.getLineCommentPosition(end));
			oldString = src.substring(start, jcm.getLineCommentPosition(end)
					+ comment.length() + 2);
		} else {
			oldString = src.substring(start, end);
		}

		return oldString;
	}

	public void replace(String name, String blockString) {
		BCSystem.out.println("put name :" + name + "and blockString"
				+ blockString);
		requests.put(name, blockString);
	}

	public void replaceConstructor(String name, String blockString) {
		constructorRequests.put(name, blockString);
	}

	public void replacePrivateValue(String name, String blockString) {// private変数名を保存しとく　
		privateRequests.put(name, blockString);
	}

}
