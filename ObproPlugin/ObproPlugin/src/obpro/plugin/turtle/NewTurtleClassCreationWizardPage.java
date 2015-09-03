/*
 * NewGUIClassCreationWizardPage.java
 * Created on 2007/04/30 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.plugin.turtle;

import obpro.plugin.common.NewObproClassWizardPage;

/**
 * NewGUIClassCreationWizardPage
 */
public class NewTurtleClassCreationWizardPage extends NewObproClassWizardPage {

	private static final String TEMPLATE_PATH = "template/turtle";

	/*
	 * @see obpro.plugin.common.NewObproClassWizardPage#getImportText()
	 */
	protected String getImportText() {
		return "import obpro.turtle.*;";
	}

	/*
	 * @see obpro.plugin.common.NewObproClassWizardPage#getTemplateURL()
	 */
	protected String getTemplatePath() {
		return TEMPLATE_PATH;
	}

	
	
//	public static final String MAIN_COMMENT = "//起動処理";
//	public static final String START_COMMENT = "//タートルを動かす処理";
//
//	/**
//	 * Fileコメントは吐かないようにオーバーライド
//	 */
//	protected String getFileComment(ICompilationUnit parentCU,
//			String lineDelimiter) throws CoreException {
//		return null;
//	}
//
//	/**
//	 * Typeコメントをオブプロ形式にオーバーライド
//	 */
//	protected String getTypeComment(ICompilationUnit parentCU,
//			String lineDelimiter) {
//		StringBuffer buf = new StringBuffer();
//
//		// 妥協処理import文の追加
//		buf.append("import obpro.turtle.*;\n");
//		buf.append("\n");// 空行
//		
//		// ヘッダコメントの追加
//		buf.append("/**\n");
//		buf.append("* プログラム名： \n");
//		buf.append("* 作成者： \n");
//		buf.append("* バージョン： 1.0 (日付) \n");
//		buf.append("*/");
//		return buf.toString();
//	}
//
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
//		// CreateComment
//		String comment = MAIN_COMMENT;
//		// String comment = CodeGeneration
//		// .getMethodComment(
//		// type.getCompilationUnit(),
//		// type.getTypeQualifiedName('.'),
//		// "main", new String[]{"args"}, new String[0],
//		// Signature.createTypeSignature("void", true), null, lineDelim);
//		// //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//		if (comment != null) {
//			buf.append(comment);
//			buf.append(lineDelim);
//		}
//
//		// Create Body
//		// final String content = CodeGeneration.getMethodBodyContent(type
//		// .getCompilationUnit(), type.getTypeQualifiedName('.'),
//		// "main", false, "", lineDelim); //$NON-NLS-1$ //$NON-NLS-2$
//		buf.append("public static void main("); //$NON-NLS-1$
//		buf.append(imports.addImport("java.lang.String")); //$NON-NLS-1$
//		buf.append("[] args) {"); //$NON-NLS-1$
//		buf.append(lineDelim);
//		String content = "Turtle.startTurtle(new " + type.getElementName()
//				+ "());";
//		if (content != null && content.length() != 0) {
//			buf.append(content);
//		}
//		buf.append(lineDelim);
//		buf.append("}"); //$NON-NLS-1$
//
//		type.createMethod(buf.toString(), null, false, null);
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
//		String comment = START_COMMENT;
//		if (comment != null) {
//			buf.append(comment);
//			buf.append(lineDelim);
//		}
//
//		// Create Body
//		buf.append("public void start()"); //$NON-NLS-1$
//		buf.append("{"); //$NON-NLS-1$
//		buf.append(lineDelim);
//		buf.append(lineDelim);
//		buf.append("}"); //$NON-NLS-1$
//
//		type.createMethod(buf.toString(), null, false, null);
//	}
}
