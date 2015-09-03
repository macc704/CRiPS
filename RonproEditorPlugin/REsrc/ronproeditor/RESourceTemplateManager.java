/*
 * RESourceTemplateManager.java
 * Created on 2007/09/21 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * RESourceTemplateManager
 */
public class RESourceTemplateManager {

	private File dir;

	public RESourceTemplateManager(String dirname) {
		File dir = new File(dirname);
		if (!dir.exists()) {
			dir.mkdir();
		}
		this.dir = dir;
	}

	public List<RESourceTemplate> getTemplates() {
		ArrayList<RESourceTemplate> templates = new ArrayList<RESourceTemplate>();
		templates.add(new DefaultTemplate());
		for (File file : dir.listFiles()) {
			if (file.isFile() && !file.getName().startsWith(".")) {
				templates.add(new FileTemplate(file));
			}
		}
		return templates;
	}

}
