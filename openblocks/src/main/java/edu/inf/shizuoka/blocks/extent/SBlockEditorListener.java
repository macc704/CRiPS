package edu.inf.shizuoka.blocks.extent;

import java.io.File;

public interface SBlockEditorListener {

	public void blockConverted(File file);

	public void blockCompile();

	public void blockRun();

	public void blockDebugRun();
	
	public void chengeInheritance();
	
	public void toggleTraceLines(String state);
}
