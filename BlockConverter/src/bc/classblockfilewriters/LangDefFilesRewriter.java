package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import bc.classblockfilewriters.model.ConvertBlockModel;
import bc.classblockfilewriters.model.ObjectArrayBlockModel;
import bc.classblockfilewriters.model.ObjectBlockModel;
import bc.classblockfilewriters.model.ParameterBlockModel;

public class LangDefFilesRewriter {

	private File file;
	private List<ObjectBlockModel> requestObjectBlock = new LinkedList<ObjectBlockModel>();
	private FileInputStream ldfReader;
	private String javaFileName;

	private List<ConvertBlockModel> requestConvertBlockModel = new LinkedList<ConvertBlockModel>();
	private List<ParameterBlockModel> requestParameterBlockModel = new LinkedList<ParameterBlockModel>();

	public LangDefFilesRewriter(File file, String javaFileName) {
		this.file = file;
		this.javaFileName = javaFileName.substring(0, javaFileName.indexOf(".java"));
	}

	public void setSelDefClassModel(List<ObjectBlockModel> models) {
		for (ObjectBlockModel model : models) {
			requestObjectBlock.add(model);
		}
	}

	public void setLocalVariableBlockModel(String fileName, Map<String, List<PublicMethodInfo>> methods, String superClassName) {
		ObjectBlockModel classObjectModel = new ObjectBlockModel("local-var-object-" + fileName, "local-variable", "initname", fileName + "型の変数をつくり", "と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classObjectModel.setMethods(methods);
		classObjectModel.setClassName(fileName);
		requestObjectBlock.add(classObjectModel);
		classObjectModel.setSuperClassName(superClassName);

		// // 配列の追加
		ObjectBlockModel classObjectArrayModel = new ObjectArrayBlockModel("local-var-object-" + fileName + "-arrayobject", "local-variable", "initname", fileName + "[]" + "型の変数をつくり", "と名付ける", "230 0 255");
		classObjectArrayModel.setSuperClassName(superClassName);
		// 定義クラスブロックのプロパティをセットする
		classObjectArrayModel.setMethods(methods);
		classObjectArrayModel.setClassName(fileName + "[]");
		requestObjectBlock.add(classObjectArrayModel);

	}

	public void setConvertBlockModel(String className) {
		ConvertBlockModel model = new ConvertBlockModel("to" + className + "FromObject", "function", className + "型に変換する", "", "", "45 201 255", className);
		requestConvertBlockModel.add(model);
	}

	public void setParameterBlockModel(String className, Map<String, List<PublicMethodInfo>> methods) {
		ParameterBlockModel model = new ParameterBlockModel("proc-param-object-" + className.toLowerCase(), "param", className + "型引数", className + "型の仮引数を作り、", "と名付ける", "200 200 200", className);
		model.setMethods(methods);
		requestParameterBlockModel.add(model);
	}

	public void setArrayParameterBlockModel(String className, Map<String, List<PublicMethodInfo>> methods) {
		ParameterBlockModel model = new ParameterBlockModel("proc-param-object-" + className.toLowerCase() + "-arrayobject", "param", className + "[]型引数", className + "[]型の仮引数を作り、", "と名付ける", "200 200 200", className);
		// model.setMethods(methods);
		requestParameterBlockModel.add(model);
	}

	public void setInstanceVariableBlockMode(String fileName, Map<String, List<PublicMethodInfo>> methods, String superClassName) {
		ObjectBlockModel classModel = new ObjectBlockModel("private-var-object-" + fileName, "global-variable", "initname", fileName + "型のインスタンス変数をつくり", "と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classModel.setMethods(methods);
		classModel.setClassName(fileName);
		requestObjectBlock.add(classModel);
		classModel.setSuperClassName(superClassName);

		// 配列の追加
		ObjectBlockModel classObjectArrayModel = new ObjectArrayBlockModel("private-var-object-" + fileName + "-arrayobject", "global-variable", "initname", fileName + "[]" + "型のインスタンス変数をつくり", "と名付ける", "230 0 255");
		classObjectArrayModel.setSuperClassName(superClassName);
		// 定義クラスブロックのプロパティをセットする
		classObjectArrayModel.setMethods(methods);
		classObjectArrayModel.setClassName(fileName + "[]");
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

		for (ParameterBlockModel model : requestParameterBlockModel) {
			model.print(ps, 0);
		}

		String blockString = byteArray.toString();

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

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

			ps.println("<BlockDrawer name=\"Project-Parameter\" type=\"factory\" button-color=\"255 155 64\">");

			for (ParameterBlockModel model : requestParameterBlockModel) {
				model.printMenuItem(ps, lineNum);
			}

			makeIndent(ps, --lineNum);
			ps.println("</BlockDrawer>");

			makeIndent(ps, lineNum++);
			ps.println("<BlockDrawer name=\"継承メソッド\" type=\"factory\" button-color=\"255 155 64\">");

			addInheritanceMethodBlocksToMenu(ps, lineNum);

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
			int res = JOptionPane.showConfirmDialog(null, "Blockへの変換中にエラーが発生しました：lang_def_files message:" + e.getStackTrace().toString(), "警告", JOptionPane.DEFAULT_OPTION);
			if (res == 1) {
				e.printStackTrace();
				return;
			}
		}

	}

	public void printProjectClassInfo(){
		try {
			ProjectInfoSerializer piSerializer = new ProjectInfoSerializer();

			List<String> addedMethodsCash = new ArrayList<String>();
			for (ObjectBlockModel selDefClass : requestObjectBlock) {
				if (selDefClass.getMethods() != null) {
					piSerializer.addAddedMethods(getAddedMethodsInfo(selDefClass, addedMethodsCash));
				}
			}
			piSerializer.print(file.getParentFile().getPath());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private List<PublicMethodInfo> getAddedMethodsInfo(ObjectBlockModel selDefClass, List<String> addedMethodsCash) {
		List<PublicMethodInfo> methods = new ArrayList<PublicMethodInfo>();
		for (String key : selDefClass.getMethods().keySet()) {
			for (PublicMethodInfo method : selDefClass.getMethods().get(key)) {
				if (addedMethodsCash.indexOf(method.getFullName()) == -1) {
					methods.add(method);
					addedMethodsCash.add(method.getFullName());
				}
			}
		}
		return methods;
	}

	private void addInheritanceMethodBlocksToMenu(PrintStream ps, int lineNum) {
		Map<String, PublicMethodInfo> addedMethods = new HashMap<String, PublicMethodInfo>();
		List<PublicMethodInfo> superConstructors = new ArrayList<PublicMethodInfo>();
		String SuperClassName = null;

		ObjectBlockModel request = getObjectBlockModel(javaFileName);

		SuperClassName = request.getSuperClassName();

		if (request != null && request.getMethods() != null) {
			for (String key : request.getMethods().keySet()) {
				for (PublicMethodInfo method : request.getMethods().get(key)) {
					if (addedMethods.get(method.getFullName()) == null && !key.equals(javaFileName)) {
						PublicMethodCommandWriter writer = new PublicMethodCommandWriter();
						writer.setMethods(method);
						writer.printMenuItem(ps, lineNum);
						addedMethods.put(method.getFullName(), method);

						// superの追加
						if (method.getName().startsWith("new-") && key.equals(request.getSuperClassName())) {
							// モデルを追加
							PublicMethodInfo superConstructorCaller = method;
							superConstructorCaller.setColor("\"255 0 0\"");

							superConstructorCaller.setName("super");
							superConstructorCaller.setFullName(calcFullName("super", method.getParameters()));
							superConstructorCaller.setReturnType("void");

							superConstructors.add(superConstructorCaller);

						}
					}
				}
			}
		}

		// super呼び出しを追加する
		for (PublicMethodInfo superConstructor : superConstructors) {
			PublicMethodCommandWriter writer = new PublicMethodCommandWriter();
			writer.setMethods(superConstructor);
			writer.printMenuItem(ps, lineNum);

			request.getMethods().get(SuperClassName).add(superConstructor);
		}

	}

	public ObjectBlockModel getObjectBlockModel(String name) {
		for (ObjectBlockModel request : requestObjectBlock) {
			if (request.getClassName().equals(name)) {
				return request;
			}
		}
		return null;
	}

	public String calcFullName(String name, List<String> parameters) {

		String fullName = name + "[";
		for (String param : parameters) {
			param = param.substring(0, param.indexOf(" "));
			if (param.equals("double")) {
				param = "int";
			}
			fullName += "@" + MethodAnalyzer.convertBlockConnectorType(param);
		}
		fullName += "]";

		return fullName;
	}

	public void makeIndent(PrintStream out, int number) {
		for (int i = 0; i < number; i++) {
			out.print("\t");
		}
	}
}

