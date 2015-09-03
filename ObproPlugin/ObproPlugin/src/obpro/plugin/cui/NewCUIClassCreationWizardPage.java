/*
 * NewCUIClassCreationWizardPage.java
 * Created on 2007/04/30 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.plugin.cui;

import obpro.plugin.common.NewObproClassWizardPage;
import obpro.plugin.common.Template;

/**
 * NewCUIClassCreationWizardPage
 */
public class NewCUIClassCreationWizardPage extends NewObproClassWizardPage {

	private static final String TEMPLATE_PATH = "template/cui";
	private static final String[] IMPORT_CLASS_PATHS = new String[]{
			"obpro.common.BConverter", "obpro.common.BStopWatch",
			"obpro.cui.Input", "obpro.cui.Random", "obpro.file.BFile",
			"obpro.file.BFileReader", "obpro.file.BFileWriter"};

	/*
	 * @see obpro.plugin.common.NewObproClassWizardPage#getImportText()
	 */
	protected String getImportText() {
		return Template.createImportText(IMPORT_CLASS_PATHS);
	}

	/*
	 * @see obpro.plugin.common.NewObproClassWizardPage#getTemplatePath()
	 */
	protected String getTemplatePath() {
		return TEMPLATE_PATH;
	}

//	/**
//	 * Fileコメントは吐かないようにオーバーライド
//	 */
//	protected String getFileComment(ICompilationUnit parentCU,
//			String lineDelimiter) throws CoreException {
//		return null;
//	}

//	/**
//	 * Typeコメントをオブプロ形式にオーバーライド
//	 */
//	protected String getTypeComment(ICompilationUnit parentCU,
//			String lineDelimiter) {
//		StringBuffer buf = new StringBuffer();
//
//		// 妥協処理import文の追加
//		buf.append("import obpro.common.BConverter;\n");
//		buf.append("import obpro.common.BStopWatch;\n\n");
//
//		buf.append("import obpro.cui.Input;\n");
//		buf.append("import obpro.cui.Random;\n\n");
//
//		buf.append("import obpro.file.BFile;\n");
//		buf.append("import obpro.file.BFileReader;\n");
//		buf.append("import obpro.file.BFileWriter;\n");
//
//		buf.append("/**\n");
//		buf.append("* プログラム名： \n");
//		buf.append("* 作成者： \n");
//		buf.append("* バージョン： 1.0 (日付) \n");
//		buf.append("*/");
//		return buf.toString();
//	}

//	/**
//	 * メイン，start()メソッドを自動生成するようにオーバーライド
//	 */
//	protected void createTypeMembers(IType type, ImportsManager imports,
//			IProgressMonitor monitor) throws CoreException {
//
//		boolean createMain = isCreateMain();
//		boolean createConstructors = isCreateConstructors();
//		boolean createInheritedMethods = isCreateInherited();
//
//		createInheritedMethods(type, createConstructors,
//				createInheritedMethods, imports, new SubProgressMonitor(
//						monitor, 1));
//
//		if (createMain) {
//			createMainMethod(type, imports);
//		}
//
//		createStartMethod(type, imports);
//
//		if (monitor != null) {
//			monitor.done();
//		}
//	}
//
//	// メインメソッドを生成する
//	private void createMainMethod(IType type, ImportsManager imports)
//			throws CoreException, JavaModelException {
//		StringBuffer buf = new StringBuffer();
//		final String lineDelim = "\n"; // OK, since content is formatted
//		// afterwards //$NON-NLS-1$
//
//		// インポートを編成
//		for (int i = 0; i < IMPORT_CLASS_PATHS.length; i++) {
//			imports.addImport(IMPORT_CLASS_PATHS[i]);
//		}
//
//		// Create Body
//		String className = type.getElementName();
//		String variableName = toVariableName(className);
//		buf.append("public static void main("); //$NON-NLS-1$
//		buf.append(imports.addImport("java.lang.String")); //$NON-NLS-1$
//		buf.append("[] args) {"); //$NON-NLS-1$
//		buf.append(lineDelim);
//		buf.append(className + " " + variableName + " = new " + className
//				+ "();");
//		buf.append(lineDelim);
//		buf.append(variableName + ".main();");
//		buf.append("}"); //$NON-NLS-1$
//
//		type.createMethod(buf.toString(), null, false, null);
//	}
//
//	private String toVariableName(String className) {
//		return className.substring(0, 1).toLowerCase() + className.substring(1);
//	}
//
//	// Startメソッドを生成する
//	private void createStartMethod(IType type, ImportsManager imports)
//			throws CoreException, JavaModelException {
//		StringBuffer buf = new StringBuffer();
//		final String lineDelim = "\n"; // OK, since content is formatted
//		// afterwards //$NON-NLS-1$
//
//		// CreateComment
//		// String comment = START_COMMENT;
//		// if (comment != null) {
//		// buf.append(comment);
//		// buf.append(lineDelim);
//		// }
//
//		// Create Body
//		buf.append("private void main()"); //$NON-NLS-1$
//		buf.append("{"); //$NON-NLS-1$
//		buf.append(lineDelim);
//		buf.append(lineDelim);
//		buf.append("}"); //$NON-NLS-1$
//
//		type.createMethod(buf.toString(), null, false, null);
//	}
}
