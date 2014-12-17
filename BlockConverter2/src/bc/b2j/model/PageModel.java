package bc.b2j.model;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import bc.BCSystem;
import bc.apps.OutputSourceModel;

public class PageModel extends BlockModel {

	private ArrayList<ProcedureBlockModel> procedures = new ArrayList<ProcedureBlockModel>();
	private ArrayList<ConstructorBlockModel> constructors = new ArrayList<ConstructorBlockModel>();
	private ArrayList<PrivateVariableBlockModel> privateVariableBlocks = new ArrayList<PrivateVariableBlockModel>();
	private String superClass = null;

	public PageModel(String name, String superClass) {
		setName(name);
		this.superClass = superClass;
	}

	/**
	 * 
	 * @param procedures
	 */
	public void addProcedure(ProcedureBlockModel procedure) {
		procedures.add(procedure);
	}

	public void addProcedure(ConstructorBlockModel procedure) {
		constructors.add(procedure);
	}

	public void addPrivateVariableBlock(
			PrivateVariableBlockModel privateVariableBlock) {
		privateVariableBlocks.add(privateVariableBlock);
	}

	public void addConstructor(ConstructorBlockModel constructorBlock) {
		constructors.add(constructorBlock);
	}

	/**
	 * 
	 * @param superClass
	 */
	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	/**
	 * 
	 * @return
	 */
	public String getSuperClass() {
		return superClass;
	}

	@Override
	public void checkError() {
		for (BlockModel topBlock : procedures) {
			topBlock.checkError();
		}
	}

	// TODO
	// BlockEditorからブロックでプログラムを組み立て、Javaのソースコードにするときはclass名をBlockEditorの初期設定のページ名にして、mainメソッドを記述してあげる必要がある。
	@Override
	public void print(PrintStream out, int indent) {
		for (BlockModel topBlock : procedures) {
			topBlock.print(out, indent + 1);
		}
	}

	// TODO JavaからBlockEditorのブロックに変換されたときはオリジナルのJavaソースコードとメソッドの部分だけ置き換える
	public void print2(OutputSourceModel out) {
		out.setSuperClassName(superClass);
		for (PrivateVariableBlockModel privateVariableBlock : privateVariableBlocks) {// #ohata
																						// added
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(byteArray);
			privateVariableBlock.print(ps, 0);
			String blockString = byteArray.toString();
			String name = privateVariableBlock.getLabel();
			out.replacePrivateValue(name, blockString);// private変数は個別に登録しておく
		}

		for (ConstructorBlockModel constructor : constructors) {// すべての手続きブロックをプリントする//#ohata
																// added
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(byteArray);
			constructor.print(ps, 0);
			String blockString = byteArray.toString();
			BCSystem.out.println("blockString:" + blockString);
			String name = constructor.getKey();
			BCSystem.out.println("name:" + name);
			out.replace(name, blockString);
		}

		for (ProcedureBlockModel procedure : procedures) {// すべての手続きブロックをプリントする
			BCSystem.out.println("procedure block model print");
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(byteArray);
			procedure.print(ps, 0);
			String blockString = byteArray.toString();
			BCSystem.out.println("blockString:" + blockString);
			out.replace(procedure.getKey(), blockString);
		}
	}
	/*
	 * //#ohata added private String getBlockStringValue(String blockString){
	 * int index = blockString.indexOf("="); if(index == -1){ return null; }
	 * return blockString.substring(index+2,blockString.length()-2); }
	 */
}
