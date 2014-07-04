/*
 * JavaEnv.java
 * Created on 2007/09/22 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.helpers;

import java.io.File;

/**
 * JavaEnv
 */
public class JavaEnv {
	public File dir;
	public String source;
	public String runnable;

	public JavaEnv(File dir, String source, String runnable) {
		this.dir = dir;
		this.source = source;
		this.runnable = runnable;
	}
}
