package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LangDefFilesRewriter {

	private File file;
	// private String[] classpaths;
	private List<ObjectBlockModel> requestObjectBlock = new LinkedList<ObjectBlockModel>();
	private FileInputStream ldfReader;
	private String javaFileName;
	private Map<String, String> addedMethods = new HashMap<String, String>();
	private Map<String, String> addedMethodsJavaType = new HashMap<String, String>();
	private List<ConvertBlockModel> requestConvertBlockModel = new LinkedList<ConvertBlockModel>();

	public LangDefFilesRewriter(File file, String javaFileName) {
		this.file = file;
		this.javaFileName = javaFileName.substring(0,
				javaFileName.indexOf(".java"));
	}

	public void setSelDefClassModel(List<ObjectBlockModel> models) {
		for (ObjectBlockModel model : models) {
			requestObjectBlock.add(model);
		}
	}

	public void setLocalVariableBlockModel(String fileName,
			Map<String, List<PublicMethodInfo>> methods) {
		ObjectBlockModel classObjectModel = new ObjectBlockModel(
				"local-var-object-" + fileName, "local-variable", "initname",
				fileName + "型の変数をつくり", "と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classObjectModel.setMethods(methods);
		classObjectModel.setClassName(fileName);
		requestObjectBlock.add(classObjectModel);

		// 配列の追加
		ObjectBlockModel classObjectArrayModel = new ObjectBlockModel(
				"local-var-object-" + fileName + "-arraybject",
				"local-variable", "initname", fileName + "[]" + "型の変数をつくり",
				"と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classObjectArrayModel.setMethods(methods);
		classObjectArrayModel.setClassName(fileName);
		requestObjectBlock.add(classObjectArrayModel);

	}

	public void setConvertBlockModel(String className) {
		ConvertBlockModel model = new ConvertBlockModel("to" + className
				+ "FromObject", "function", className + "型に変換する", "", "",
				"45 201 255");
		requestConvertBlockModel.add(model);
	}

	public void setInstanceVariableBlockMode(String fileName,
			Map<String, List<PublicMethodInfo>> methods) {
		ObjectBlockModel classModel = new ObjectBlockModel(
				"private-var-object-" + fileName, "global-variable",
				"initname", fileName + "型のインスタンス変数をつくり", "と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classModel.setMethods(methods);
		classModel.setClassName(fileName);
		requestObjectBlock.add(classModel);

		// 配列の追加
		ObjectBlockModel classObjectArrayModel = new ObjectBlockModel(
				"private-var-object-" + fileName + "-arrayobject",
				"global-variable", "initname", fileName + "[]"
						+ "型のインスタンス変数をつくり", "と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classObjectArrayModel.setMethods(methods);
		classObjectArrayModel.setClassName(fileName);
		requestObjectBlock.add(classObjectArrayModel);

	}

	public void printGenus() throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(byteArray);

		ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		for (ObjectBlockModel selDefClass : requestObjectBlock) {
			selDefClass.print(ps, 0);
		}

		for (ConvertBlockModel model : requestConvertBlockModel) {
			model.print(ps, 0);
		}

		String blockString = byteArray.toString();

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		bw.write(blockString);
		bw.flush();
		bw.close();

		ps.close();
	}

	public void printMenu(File menuFile, File originFile) {
		BufferedReader br;
		int lineNum = 0;
		try {
			ldfReader = new FileInputStream(originFile);

			InputStreamReader ldfISR = new InputStreamReader(ldfReader, "UTF-8");
			br = new BufferedReader(ldfISR);

			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(turtleByteArray);
			// </BlockDrawerSet>までコピー
			String line;
			while (!(line = br.readLine()).equals("</BlockDrawerSet>")) {
				// 一行書き込み >>lang_def.xml
				ps.println(line);
			}

			makeIndent(ps, ++lineNum);
			ps.println("<BlockDrawer name=\"Project-Objects\" type=\"factory\" button-color=\"255 155 64\">");
			lineNum++;

			for (ObjectBlockModel selDefClass : requestObjectBlock) {
				selDefClass.printMenuItem(ps, lineNum);
			}

			makeIndent(ps, --lineNum);
			ps.println("</BlockDrawer>");

			makeIndent(ps, ++lineNum);

			ps.println("<BlockDrawer name=\"Project-Converter\" type=\"factory\" button-color=\"255 155 64\">");

			for (ConvertBlockModel model : requestConvertBlockModel) {
				model.printMenuItem(ps, lineNum);
			}

			makeIndent(ps, --lineNum);
			ps.println("</BlockDrawer>");

			makeIndent(ps, ++lineNum);

			ps.println("<BlockDrawer name=\"Project-Methods\" type=\"factory\" button-color=\"255 155 64\">");

			Map<String, PublicMethodInfo> addedMethods = new HashMap<String, PublicMethodInfo>();
			for (ObjectBlockModel selDefClass : requestObjectBlock) {
				if (selDefClass.getMethods() != null) {
					for (String key : selDefClass.getMethods().keySet()) {
						for (PublicMethodInfo method : selDefClass.getMethods()
								.get(key)) {
							if (addedMethods.get(Integer.toString((method
									.hashCode()))) == null) {
								PublicMethodCommandWriter writer = new PublicMethodCommandWriter();
								writer.setMethods(method);
								writer.printMenuItem(ps, lineNum);
								addedMethods.put(
										Integer.toString(method.hashCode()),
										method);
								String paramSize = Integer.toString(method
										.getParameters().size());
								if (paramSize.equals("0")) {
									paramSize = "";
								}
								String addedMethodName = method.getName() + "("
										+ paramSize + ")";
								this.addedMethods.put(addedMethodName,
										method.getReturnType());
								this.addedMethodsJavaType.put(
										method.getFullName(),
										method.getJavaType());
							}
						}
					}

				}
			}

			makeIndent(ps, --lineNum);
			ps.println("</BlockDrawer>");

			makeIndent(ps, lineNum++);
			ps.println("<BlockDrawer name=\"継承メソッド\" type=\"factory\" button-color=\"255 155 64\">");

			addedMethods.clear();
			for (ObjectBlockModel request : requestObjectBlock) {
				if (request.getClassName().equals(javaFileName)) {
					if (request.getMethods() != null) {
						for (String key : request.getMethods().keySet()) {
							for (PublicMethodInfo method : request.getMethods()
									.get(key)) {
								if (addedMethods.get(Integer.toString((method
										.hashCode()))) == null
										&& !key.equals(javaFileName)) {
									PublicMethodCommandWriter writer = new PublicMethodCommandWriter();
									writer.setMethods(method);
									writer.printMenuItem(ps, lineNum);
									addedMethods
											.put(Integer.toString(method
													.hashCode()), method);
								}
							}
						}
					}
				}
			}

			// requestObjectBlock.get(0).getClassName().equals()
			// selDefClass.printMenuItem(ps, lineNum);

			makeIndent(ps, --lineNum);
			ps.println("</BlockDrawer>");

			makeIndent(ps, --lineNum);
			ps.println("</BlockDrawerSet>");

			// psに書きだしたものをすべて文字列に変換する
			String ldfString = turtleByteArray.toString();

			FileOutputStream ldfOS = new FileOutputStream(menuFile);

			OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS, "UTF-8");
			BufferedWriter ldfWriter = new BufferedWriter(ldfFOS);

			ldfWriter.write(ldfString);
			ldfWriter.flush();
			ldfWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Map<String, String> getAddedMethods() {
		return this.addedMethods;
	}

	public Map<String, String> getAddedMethodsJavaType() {
		return this.addedMethodsJavaType;
	}

	public void makeIndent(PrintStream out, int number) {
		for (int i = 0; i < number; i++) {
			out.print("\t");
		}
	}
}
