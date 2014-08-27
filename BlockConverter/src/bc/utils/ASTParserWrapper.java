/*
 * ASTParserWrapper.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.utils;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author macchan
 * 
 */
public class ASTParserWrapper {

	// public static CompilationUnit parse(File file) {
	// return parse(file, "JISAutoDetect");
	// }

	public static CompilationUnit parse(File file, String enc,
			String[] classpaths) {
		try {
			// IProject project = AProject.create(file.getParentFile());
			// IJavaProject javaProject = JavaCore.create(project);
			// IClasspathEntry cpe = JavaCore.newLibraryEntry(new
			// Path(classpaths[0]), null, null);
			// javaProject.setRawClasspath(new IClasspathEntry[] { cpe }, null);

			String source = FileReader.readFile(file, enc);

			@SuppressWarnings("deprecation")
			ASTParser astParser = ASTParser.newParser(AST.JLS3);
			astParser.setKind(ASTParser.K_COMPILATION_UNIT);
			astParser.setEnvironment(classpaths, new String[] {},
					new String[] {}, false);
			// astParser.setProject(javaProject);
			astParser.setBindingsRecovery(true);
			astParser.setResolveBindings(true);
			astParser.setSource(source.toCharArray());
			CompilationUnit unit = (CompilationUnit) astParser
					.createAST(new NullProgressMonitor());
			return unit;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
