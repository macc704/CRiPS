package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import bc.classblockfilewriters.model.ConvertBlockModel;
import bc.classblockfilewriters.model.ObjectArrayBlockModel;
import bc.classblockfilewriters.model.ObjectBlockModel;
import bc.classblockfilewriters.model.ParameterBlockModel;
import bc.utils.ASTParserWrapper;
import bc.utils.DomParserWrapper;

public class LangDefFilesRewriter {

	private File langDefGenusesFile;

	private List<ObjectBlockModel> requestObjectBlock = new LinkedList<ObjectBlockModel>();
	private List<ConvertBlockModel> requestConvertBlockModel = new LinkedList<ConvertBlockModel>();
	private List<ParameterBlockModel> requestParameterBlockModel = new LinkedList<ParameterBlockModel>();

	private FileInputStream ldfReader;
	private String javaFileName;
	private Map<String, String> addedMethods = new HashMap<String, String>();
	private Map<String, String> addedMethodsJavaType = new HashMap<String, String>();
	private List<String> addedClasses = new ArrayList<String>();
	private Map<String, List<String>> libaryMethod = new HashMap<>();
	
	private String enc;
	private String[] classPaths;

	public LangDefFilesRewriter(File file, String javaFileName, String enc, String[] classPaths) {
		this.langDefGenusesFile = file;
		this.javaFileName = javaFileName.substring(0, javaFileName.indexOf(".java"));
		this.enc = enc;
		this.classPaths = classPaths;
	}
	
	public LangDefFilesRewriter(File file, String javaFileName, String enc, String[] classPaths, String libraryXMLPath) {
		this(file, javaFileName, enc,classPaths);
		
		//ライブラリリストを読み込んで、ライブラリクラスとそのブロック名のマップを作成
		Document doc = DomParserWrapper.parse(libraryXMLPath);
		createLibraryMethodsMap(doc.getFirstChild());
	}
	
	public void createLibraryMethodsMap(Node node){
		//LibClassノードで行う処理の定義
		Consumer<Node> parseLibNode = new Consumer<Node>() {
			@Override
			public void accept(Node node) {
				String className = DomParserWrapper.getAttribute(node, "name");
				libaryMethod.put(className, new ArrayList<>());
				//CategoryNameタグの全ノードで行う処理の定義
				Consumer<Node> parseCategory = new Consumer<Node>() {
					@Override
					public void accept(Node t) {
						Consumer<Node> c = new Consumer<Node>() {
							@Override
							public void accept(Node t) {
								libaryMethod.get(className).add(t.getTextContent());
							}
						};
						
						if("add".equals(DomParserWrapper.getAttribute(t, "command"))){
							DomParserWrapper.doAnythingToNodeList(t, "MethodName", c);							
						}else if("copy".equals(DomParserWrapper.getAttribute(t, "command"))){
							List<String> methods = libaryMethod.get(DomParserWrapper.getAttribute(t, "name"));
							for(String method : methods){
								libaryMethod.get(className).add(method);
							}
						}
					}
				};
				
				DomParserWrapper.doAnythingToNodeList(node, "CategoryName", parseCategory);
			}
		};
		DomParserWrapper.doAnythingToNodeList(node, "LibraryClass", parseLibNode);		
	}
	
	public void setSelDefClassModel(List<ObjectBlockModel> models) {
		for (ObjectBlockModel model : models) {
			requestObjectBlock.add(model);
		}
	}

	public void setLocalVariableBlockModel(String fileName, Map<String, List<PublicMethodInfo>> methods, String superClassName) {
		createLocalVariableModel(superClassName, fileName, methods);

//		createLocalVariableArrayModel(superClassName, fileName, methods);
	}
	public void createLocalVariableModel(String superClassName, String fileName, Map<String, List<PublicMethodInfo>> methods){
		ObjectBlockModel classObjectModel = new ObjectBlockModel("local-var-object-" + fileName, "local-variable", "initname", fileName + "型の変数をつくり", "と名付ける", "230 0 255");
		// 定義クラスブロックのプロパティをセットする
		classObjectModel.setMethods(methods);
		classObjectModel.setClassName(fileName);
		requestObjectBlock.add(classObjectModel);
		classObjectModel.createMethodsMenu(libaryMethod);
		classObjectModel.setSuperClassName(superClassName);
	}

	public void createLocalVariableArrayModel(String superClassName, String fileName, Map<String, List<PublicMethodInfo>> methods){
		// // 配列の追加
		ObjectBlockModel classObjectArrayModel = new ObjectArrayBlockModel("local-var-object-" + fileName + "-arrayobject", "local-variable", "initname", fileName + "[]" + "型の変数をつくり", "と名付ける", "230 0 255");
		classObjectArrayModel.setSuperClassName(superClassName);
		// 定義クラスブロックのプロパティをセットする
		classObjectArrayModel.setMethods(methods);
		classObjectArrayModel.setClassName(fileName + "[]");
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

	public void printGenusesForUni() throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(byteArray);

		ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		for (ObjectBlockModel selDefClass : requestObjectBlock) {
			selDefClass.printForUni(ps, 0);
		}

		for (ConvertBlockModel model : requestConvertBlockModel) {
			model.printForUni(ps, 0);
		}

		for (ParameterBlockModel model : requestParameterBlockModel) {
			model.printForUni(ps, 0);
		}

		String blockString = byteArray.toString();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(langDefGenusesFile), "UTF-8"));
		bw.write(blockString);
		bw.flush();
		bw.close();
		ps.close();
	}

	public void printGenuses() throws Exception {
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
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(langDefGenusesFile), "UTF-8"));
		bw.write(blockString);
		bw.flush();
		bw.close();
		ps.close();
	}

	public void printMenu(File menuFile, File originFile, boolean forAD) {
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

			if(forAD){
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
			}

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

	public void setProjectClassInfo() {
		List<String> addedMethodsCash = new ArrayList<String>();
		for (ObjectBlockModel selDefClass : requestObjectBlock) {
			setMethodInfo(selDefClass, addedMethodsCash);
		}
	}

	public void setMethodInfo(ObjectBlockModel selDefClass, List<String> addedMethodsCash) {
		for (String key : selDefClass.getMethods().keySet()) {
			for (PublicMethodInfo method : selDefClass.getMethods().get(key)) {
				if (addedMethodsCash.indexOf(method.getGenusName()) == -1) {
					String paramSize = Integer.toString(method.getParameters().size());
					if (paramSize.equals("0")) {
						paramSize = "";
					}
					String addedMethodName = method.getName() + "(" + paramSize + ")";
					this.addedMethods.put(addedMethodName, method.getReturnType());
					this.addedMethodsJavaType.put(method.getGenusName(), method.getJavaType());
				}
			}
		}
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
					if (addedMethods.get(method.getGenusName()) == null && !key.equals(javaFileName)) {
						PublicMethodCommandWriter writer = new PublicMethodCommandWriter();
						writer.setMethods(method);
						writer.printMenuItem(ps, lineNum);
						addedMethods.put(method.getGenusName(), method);

						// superの追加
						if (method.getGenusName().startsWith("new-") && key.equals(request.getSuperClassName())) {
							// モデルを追加
							PublicMethodInfo superConstructorCaller = method;
							superConstructorCaller.setColor("\"255 0 0\"");

							superConstructorCaller.setGenusName("super");
							superConstructorCaller.setgenusName(calcFullName("super", method.getParameters()));
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

	public void printMenu(File projectMenuFile, String baseDir) throws IOException {
		FileReader reader = new FileReader(new File(langDefGenusesFile.getParent() + "/" + javaFileName  + ".java"));
		BufferedReader br = new BufferedReader(reader);
		String str;
		// 親クラスがタートルならメニューをコピー
		while ((str = br.readLine()) != null) {
			if (str.contains(" extends Turtle")) {
				File turtleMenu = new File(System.getProperty("user.dir"), baseDir + "lang_def_menu_turtle.xml");
				printMenu(projectMenuFile, turtleMenu, false);
				br.close();
				return;
			}
		}
		File cuiMenu = new File(System.getProperty("user.dir"), "ext/block/lang_def_menu_cui.xml");
		printMenu(projectMenuFile, cuiMenu, false);
		br.close();
		reader.close();
	}

	/*
	 * ディレクトリを解析して追加するブロックモデルを
	 */
	public void parseDirectry(String enc, String[] classpaths) throws IOException {
		for (String name : langDefGenusesFile.getParentFile().list()) {
			if (name.endsWith(".java")) {
				// javaファイル生成
				File javaFile = new File(langDefGenusesFile.getParentFile().getPath() + "/" + name);
				name = name.substring(0, name.indexOf(".java"));
				// javaファイルを解析
				Map<String, List<PublicMethodInfo>> methods = analyzeJavaFile(name, javaFile, name);
				// 親クラス名を取得し，各モデルに追加する
				String superClassName = getSuperClassName(javaFile);
				// ローカル変数ブロックのモデルを追加
				setLocalVariableBlockModel(name, methods, superClassName);// メソッドリストを引数に追加
				// // インスタンス変数ブロックのモデルを追加
//				setInstanceVariableBlockMode(name, methods, superClassName);
				// 型変換ブロックモデルの追加
//				setConvertBlockModel(name);
				// 引数ブロックモデルの追加
//				setParameterBlockModel(name, methods);
				// //配列ブロックモデルの追加
//				setArrayParameterBlockModel(name, methods);

				// キャッシュに登録済みクラスを追加する
				addedClasses.add(name);
			}
		}
	}

	public List<String> getAddedClasses(){
		return this.addedClasses;
	}

	private Map<String, List<PublicMethodInfo>> analyzeJavaFile(String name, File file, String childName) throws IOException {
		// javaファイル解析して、クラス名とメソッドのセットを取得する
		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classPaths);
		MethodAnalyzer visitor = new MethodAnalyzer();
		unit.accept(visitor);

		Map<String, List<PublicMethodInfo>> methods = new HashMap<String, List<PublicMethodInfo>>();
		String superClassName = visitor.getSuperClassName();

		// 親クラスのクラス名と，メソッド情報を取得し，先に登録する
		if (superClassName != null && existCurrentDirectry(superClassName + ".java")) {
			methods = analyzeJavaFile(superClassName, new File(file.getParentFile().getPath() + "/" + superClassName + ".java"), childName);
		}else{
			methods.put(superClassName, new ArrayList<>());
		}
		// 最後に，自クラス名とメソッド情報を登録して返す
		methods.put(name, visitor.getMethods());
		
		return methods;
	}

	private Boolean existCurrentDirectry(String fileName) {
		for (String name : langDefGenusesFile.getParentFile().list()) {
			if (name.equals(fileName)) {
				return true;
			}
		}
		return false;
	}

	public String getSuperClassName(File file) {
		// javaファイル解析
		CompilationUnit unit = ASTParserWrapper.parse(file, enc, classPaths);
		MethodAnalyzer visitor = new MethodAnalyzer();

		// 継承チェック
		unit.accept(visitor);
		return visitor.getSuperClassName();
	}

	/*
	 * return addedMethods(key: )
	 */
	public Map<String, String> getAddedMethodsType() {
		return this.addedMethods;
	}

	/*
	 * return addedMethodJavaType(key:methodfullname, value:java)
	 */
	public Map<String, String> getAddedJavaMethodsJavaType() {
		return this.addedMethodsJavaType;
	}

	public void copyLangDefFiles(String copyFilesBaseDir) throws IOException {
		// // 継承関係にあるブロック達をファミリーに出力
		LangDefFamiliesCopier langDefFamilies = new LangDefFamiliesCopier(copyFilesBaseDir);
		langDefFamilies.print(langDefGenusesFile);

		// langDefファイルを作成する
		Copier langDefXml = new LangDefFileCopier(copyFilesBaseDir);
		langDefXml.print(langDefGenusesFile);

		Copier langDefDtd = new LangDefFileDtdCopier(copyFilesBaseDir);
		langDefDtd.print(langDefGenusesFile);

		// genuseファイルを作成する　その際にprojectファイルの場所を追記する
		Copier genusCopier = new LangDefGenusesCopier(copyFilesBaseDir);
		genusCopier.print(langDefGenusesFile);
	}

	public void copyAdditionalFileForUni(String baseDir) throws IOException{
		Copier copier = new Copier(baseDir);
		copier.copyFile("method_lang_def.xml", baseDir + "method_lang_def.xml");
		copier.copyFile("method_lang_def.dtd", baseDir+ "method_lang_def.dtd");
		copier.copyFile("lang_def_genuses_turtle.xml",baseDir + "lang_def_genuses_turtle.xml");
	}
}
