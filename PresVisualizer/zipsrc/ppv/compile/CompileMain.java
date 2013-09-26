/*
 * CompileMain.java
 * Created on 2011/06/03
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.compile;


/**
 * @author macchan
 * 
 */
public class CompileMain {

	// public static void main(String[] args) {
	// new CompileMain().main();
	// }
	//
	// void main() {
	// // simple();
	// // complex();
	// CJavaCompiler jc = new CJavaCompiler(CFileSystem.getExecuteDirectory());
	// jc.setDestpath(new CPath("bb"));
	// jc.setSourcepath(new CPath("aa"));
	// // jc.addSource("Main.java");
	// jc.addSource("CompileMain.java");
	// CCompileResult result = jc.doCompile();
	// System.out.println(result.isSuccess());
	// System.out.println(result.getDiagnostics());
	// }
	//
	// public void complex() {
	// // �R���p�C���̎擾
	// // JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	// JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	// System.out.println(compiler);
	// System.out.println(compiler.getSourceVersions());
	//
	// DiagnosticCollector<JavaFileObject> collector = new
	// DiagnosticCollector<JavaFileObject>();
	//
	// StandardJavaFileManager fileManager = compiler.getStandardFileManager(
	// collector, null, null);
	// System.out.println(fileManager);
	//
	// Iterable<? extends JavaFileObject> fileobjs = fileManager
	// .getJavaFileObjects("a/CompileMain.java");
	//
	// // �R���p�C��
	// JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
	// collector, Arrays.asList(new String[] { "-d", "aa" }), null,
	// fileobjs);
	//
	// boolean result = task.call();
	//
	// // �R���p�C�����ʂ̏o��
	// if (result) {
	// System.out.println("Success");
	// } else {
	// System.out.println("Fail");
	// for (Diagnostic<? extends JavaFileObject> diag : collector
	// .getDiagnostics()) {
	// System.out.println(diag);
	// }
	// }
	// }
	//
	// public void simple() {
	// CPath srcPath = new CPath("src");
	// CPath destPath = new CPath("src");
	//
	// JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	// System.out.println(compiler);
	//
	// // �R���p�C��
	// int result = compiler.run(null, null, null, "-d", destPath.toString(),
	// "-cp", destPath.toString(), "-sourcepath", srcPath.toString(),
	// "ppv/compile/*.java");
	//
	// // �R���p�C�����ʂ̏o��
	// if (result == 0) {
	// System.out.println("Success");
	// } else {
	// System.out.println("Fail");
	// }
	// }
}
