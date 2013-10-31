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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bc.utils.DomParserWrapper;

public class OutputSelDefClassPageModel {

	private File file;
	private File menuFile;
	private String fileName;
	// private String[] classpaths;

	private List<SelDefClassModel> requestClass = new ArrayList<SelDefClassModel>();
	private BufferedReader br;

	public OutputSelDefClassPageModel(File file, File menuFile, String fileName) {
		this.file = file;
		this.menuFile = menuFile;
		this.fileName = fileName.substring(0, fileName.indexOf('.'));
		setLocalSelDefClass();
		setGlobalSelDefClass();
		// this.classpaths = classpaths;
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

	public void printMenu(File menuFile) {
		try {
			ByteArrayOutputStream menuByteArray = new ByteArrayOutputStream();
			PrintStream menuPs = new PrintStream(menuByteArray);
			int lineNum = 0;

			menuPs.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			menuPs.println("<BlockDrawerSet name=\"factory\" type=\"stack\" location=\"southwest\" window-per-drawer=\"no\" drawer-draggable=\"no\">");

			makeIndent(menuPs, ++lineNum);
			menuPs.println("<BlockDrawer name=\"self-def-class\" type=\"factory\" button-color=\"247 0 0\">");

			// drawerprint
			lineNum++;
			for (SelDefClassModel selDefClass : requestClass) {
				selDefClass.printMenuItem(menuPs, lineNum);
			}
			if (menuFile.exists()) {
				printExistingMenuItem(menuPs, menuFile.getPath(), lineNum);
			}

			makeIndent(menuPs, --lineNum);
			menuPs.println("</BlockDrawer>");
			makeIndent(menuPs, --lineNum);
			menuPs.println("</BlockDrawerSet>");

			String menuString = menuByteArray.toString();
			BufferedWriter menuBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(menuFile), "UTF-8"));

			menuBw.write(menuString);
			menuBw.flush();
			menuBw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void printExistingMenuItem(PrintStream menuPs, String path,
			int lineNum) {
		// 既存のプロジェクトメニューファイルを読み込み書き出す 重複項は書き出さない
		Document document = DomParserWrapper.parse(path);
		Element root = document.getDocumentElement();
		NodeList lists = root.getChildNodes();
		for (int i = 0; i < lists.getLength(); i++) {
			Node list = lists.item(i);
			if (list.getNodeName().equals("BlockDrawerSet")) {
				NodeList factry = list.getChildNodes();
				for (int j = 0; j < factry.getLength(); j++) {
					Node drawer = factry.item(j);
					if (drawer.getNodeName().equals("BlockDrawer")) {
						for (Node child = drawer.getFirstChild(); child != null; child = child
								.getNextSibling()) {
							if (child.getNodeType() != Node.TEXT_NODE) {
								// 名前を確認する
								Boolean nameCheck = true;
								for (SelDefClassModel request : requestClass) {
									if (request.getName().equals(
											child.getTextContent())) {
										nameCheck = false;
									}
								}
								if (nameCheck) {
									makeIndent(menuPs, lineNum);
									menuPs.println("<BlockGenusMember>"
											+ child.getTextContent()
											+ "</BlockGenusMember>");
								}
							}
						}
					}
				}
			}

		}
	}

	public void printLangDefFile(File file) {
		// lang_def_project.xmlの書き出し
		printXMLFile(file);
		// lang_def.dtdの書き出し
		printDtdFile(file);
	}

	private void printXMLFile(File file) {
		// lang_def_file.xmlの書き出し　既存のものからコピーして、プロジェクトだけ書き換える
		try {
			FileInputStream ldfReader = new FileInputStream(
					"ext/block/lang_def_turtle.xml");

			// FileReader ldfReader = new FileReader(
			// "ext/block/lang_def_menu_turtle.xml");

			InputStreamReader ldfISR = new InputStreamReader(ldfReader, "SJIS");
			br = new BufferedReader(ldfISR);

			// File ldf = new File("/ext/block/lang_def.dtd");

			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream turtlePs = new PrintStream(turtleByteArray);
			// すべての行をコピーする
			String line;
			while ((line = br.readLine()) != null) {
				// 一行書き込み >>lang_def.xml
				if (line.contains("lang_def_menu")) {
					// メニューの書き換え
					turtlePs.println("\t\t&lang_def_menu_project;");
				} else {
					turtlePs.println(line);
				}
			}
			// menu情報のコピー
			// psに書きだしたものをすべて文字列に変換する
			String ldfString = turtleByteArray.toString();

			FileOutputStream ldfOS = new FileOutputStream(file.getParentFile()
					.getPath() + "/lang_def_project.xml");
			// FileReader ldfReader = new FileReader(
			// "ext/block/lang_def_menu_turtle.xml");
			OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS, "SJIS");
			BufferedWriter ldfWriter = new BufferedWriter(ldfFOS);

			ldfWriter.write(ldfString);
			ldfWriter.flush();
			ldfWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printDtdFile(File file) {
		// lang_def_file.xmlの書き出し　既存のものからコピーして、プロジェクトだけ書き換える
		try {
			FileInputStream ldfReader = new FileInputStream(
					"ext/block/lang_def.dtd");

			// FileReader ldfReader = new FileReader(
			// "ext/block/lang_def_menu_turtle.xml");

			InputStreamReader ldfISR = new InputStreamReader(ldfReader, "SJIS");
			br = new BufferedReader(ldfISR);

			// File ldf = new File("/ext/block/lang_def.dtd");

			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream turtlePs = new PrintStream(turtleByteArray);
			// すべての行をコピーする
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.contains("<!ENTITY lang_def_")) {
					turtlePs.println(line);
				}
			}
			turtlePs.println("<!ENTITY lang_def_menu_project SYSTEM \"lang_def_menu_project.xml\">");
			turtlePs.println("<!ENTITY lang_def_project SYSTEM \"lang_def_genuses_project.xml\">");
			// menu情報のコピー
			// psに書きだしたものをすべて文字列に変換する
			String ldfString = turtleByteArray.toString();

			FileOutputStream ldfOS = new FileOutputStream(file.getParentFile()
					.getPath() + "/lang_def.dtd");
			// FileReader ldfReader = new FileReader(
			// "ext/block/lang_def_menu_turtle.xml");
			OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS, "SJIS");
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
