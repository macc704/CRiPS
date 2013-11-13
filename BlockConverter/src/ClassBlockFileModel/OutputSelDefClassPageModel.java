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
import java.util.List;

public class OutputSelDefClassPageModel {

	private File file;
	private String fileName;
	// private String[] classpaths;

	private List<SelDefClassModel> requestClass = new ArrayList<SelDefClassModel>();
	private FileInputStream ldfReader;

	public OutputSelDefClassPageModel(File file, File menuFile) {
		this.file = file;
	}

	public OutputSelDefClassPageModel(File file, File menuFile, String fileName) {
		this.file = file;
		this.fileName = fileName.substring(0, fileName.indexOf('.'));
		// オブジェクトブロック追加のリクエスト
		setLocalSelDefClass();
		setGlobalSelDefClass();
	}

	public void setSelDefClassModel(List<SelDefClassModel> models) {
		for (SelDefClassModel model : models) {
			requestClass.add(model);
		}
	}

	public void setLocalSelDefClass() {
		SelDefClassModel classModel = new SelDefClassModel("local-var-object-"
				+ fileName, "local-variable", "initname",
				fileName + "型の変数をつくり", "と名付ける", "230 0 255 ");
		// 定義クラスブロックのプロパティをセットする
		classModel.setClassName(fileName);
		requestClass.add(classModel);
	}

	public void setGlobalSelDefClass() {
		SelDefClassModel classModel = new SelDefClassModel("global-var-object-"
				+ fileName, "global-variable", "initname", fileName
				+ "型の変数をつくり", "と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classModel.setClassName(fileName);
		requestClass.add(classModel);
	}

	public void setLocalSelDefClass(String fileName) {
		SelDefClassModel classModel = new SelDefClassModel("local-var-object-"
				+ fileName, "local-variable", "initname",
				fileName + "型の変数をつくり", "と名付ける", "230 0 255 ");
		// 定義クラスブロックのプロパティをセットする
		classModel.setClassName(fileName);
		requestClass.add(classModel);
	}

	public void setGlobalSelDefClass(String fileName) {
		SelDefClassModel classModel = new SelDefClassModel("global-var-object-"
				+ fileName, "global-variable", "initname", fileName
				+ "型の変数をつくり", "と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classModel.setClassName(fileName);
		requestClass.add(classModel);
	}

	public void printGenus() throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(byteArray);

		ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		for (SelDefClassModel selDefClass : requestClass) {
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
			ldfReader = new FileInputStream(file);

			InputStreamReader ldfISR = new InputStreamReader(ldfReader, "UTF-8");
			br = new BufferedReader(ldfISR);

			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(turtleByteArray);
			// </BlockDrawerSet>までコピー
			String line;
			while (!(line = br.readLine()).equals("</BlockDrawerSet>")) {
				// 一行書き込み >>lang_def.xml
				System.out.println("line:" + line);
				ps.println(line);
			}

			makeIndent(ps, ++lineNum);
			ps.println("<BlockDrawer name=\"Project-Objects\" type=\"factory\" button-color=\"255 155 64\">");
			lineNum++;
			for (SelDefClassModel selDefClass : requestClass) {
				selDefClass.printMenuItem(ps, lineNum);
			}

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
