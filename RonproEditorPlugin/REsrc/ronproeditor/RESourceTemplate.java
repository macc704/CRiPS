/*
 * RESourceTemplate.java
 * Created on 2007/09/21 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor;

import java.io.File;
import java.util.Date;

import ronproeditor.helpers.FileSystemUtil;
import ronproeditor.helpers.MatcherUtil;

/**
 * RESourceTemplate
 */
public abstract class RESourceTemplate {
	public static final String CLASSNAME = "CLASSNAME";
	public static final String DATE = "日付";

	private String clName = "ClassName";

	public String getClName() {
		return clName;
	}

	public void setClName(String clName) {
		this.clName = clName;
	}

	public abstract String getName();

	public String getSource() {
		String source = getSourceSource();
		source = MatcherUtil.replaceAll(source, CLASSNAME, getClName());
		source = MatcherUtil.replaceAll(source, DATE, new Date().toString());
		return source;
	}

	protected abstract String getSourceSource();

	public String toString() {
		return getName();
	}
}

class DefaultTemplate extends RESourceTemplate {
	/*
	 * (non-Javadoc)
	 * 
	 * @see ronproeditor.RESourceTemplate#getName()
	 */
	@Override
	public String getName() {
		return "Default";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ronproeditor.RESourceTemplate#getSourceSource()
	 */
	@Override
	protected String getSourceSource() {
		return "public class " + CLASSNAME + " {" + "\r\n" + "}";
	}
}

class FileTemplate extends RESourceTemplate {
	private File file;

	FileTemplate(File file) {
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ronproeditor.RESourceTemplate#getName()
	 */
	@Override
	public String getName() {
		return file.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ronproeditor.RESourceTemplate#getSourceSource()
	 */
	@Override
	protected String getSourceSource() {
		return FileSystemUtil.load(file);
	}

}
