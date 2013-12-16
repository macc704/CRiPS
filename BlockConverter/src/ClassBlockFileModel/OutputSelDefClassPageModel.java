package ClassBlockFileModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputSelDefClassPageModel {

	private File file;
	// private String[] classpaths;
	private List<ObjectBlockModel> requestObjectBlock = new ArrayList<ObjectBlockModel>();
	private FileInputStream ldfReader;
	private String javaFileName;

	public OutputSelDefClassPageModel(File file, String javaFileName) {
		this.file = file;
		this.javaFileName = javaFileName.substring(0,
				javaFileName.indexOf(".java"));
	}

	public void setSelDefClassModel(List<ObjectBlockModel> models) {
		for (ObjectBlockModel model : models) {
			requestObjectBlock.add(model);
		}
	}

	public void setLocalSelDefClass(String fileName,
			Map<String, List<PublicMethodInfo>> methods) {
		ObjectBlockModel classModel = new ObjectBlockModel("local-var-object-"
				+ fileName, "local-variable", "initname",
				fileName + "型の変数をつくり", "と名付ける", "230 0 255 ");
		// 定義クラスブロックのプロパティをセットする
		classModel.setMethods(methods);
		classModel.setClassName(fileName);
		requestObjectBlock.add(classModel);
	}

	public void setGlobalSelDefClass(String fileName,
			Map<String, List<PublicMethodInfo>> methods) {
		ObjectBlockModel classModel = new ObjectBlockModel(
				"private-var-object-" + fileName, "global-variable",
				"initname", fileName + "型の変数をつくり", "と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classModel.setMethods(methods);
		classModel.setClassName(fileName);
		requestObjectBlock.add(classModel);
	}

	public void printGenus() throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(byteArray);

		ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		for (ObjectBlockModel selDefClass : requestObjectBlock) {
			selDefClass.print(ps, 0);
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

			Map<String, PublicMethodInfo> addedMethods = new HashMap<String, PublicMethodInfo>();
			for (ObjectBlockModel selDefClass : requestObjectBlock) {
				selDefClass.printMenuItem(ps, lineNum);

				for (String key : selDefClass.getMethods().keySet()) {
					for (PublicMethodInfo method : selDefClass.getMethods()
							.get(key)) {
						if (addedMethods.get(Integer.toString((method
								.hashCode()))) == null) {
							PublicMethodCommandWriter writer = new PublicMethodCommandWriter();
							writer.setMethods(method);
							writer.printMenuItem(ps, lineNum);
							addedMethods
									.put(Integer.toString(method.hashCode()),
											method);
						}
					}
				}
			}

			makeIndent(ps, --lineNum);
			ps.println("</BlockDrawer>");

			ps.println("<BlockDrawer name=\"継承メソッド\" type=\"factory\" button-color=\"255 155 64\">");
			lineNum++;
			addedMethods.clear();
			for (ObjectBlockModel request : requestObjectBlock) {
				if (request.getClassName().equals(javaFileName)) {
					for (String key : request.getMethods().keySet()) {
						for (PublicMethodInfo method : request.getMethods()
								.get(key)) {
							if (addedMethods.get(Integer.toString((method
									.hashCode()))) == null
									&& !key.equals(javaFileName)) {
								PublicMethodCommandWriter writer = new PublicMethodCommandWriter();
								writer.setMethods(method);
								writer.printMenuItem(ps, lineNum);
								addedMethods.put(
										Integer.toString(method.hashCode()),
										method);
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

	public void makeIndent(PrintStream out, int number) {
		for (int i = 0; i < number; i++) {
			out.print("\t");
		}
	}
}
