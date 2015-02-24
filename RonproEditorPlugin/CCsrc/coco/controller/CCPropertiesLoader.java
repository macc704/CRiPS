package coco.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class CCPropertiesLoader {
	private ArrayList<Properties> propertieslist = new ArrayList<Properties>();
	
	public CCPropertiesLoader() {
		
	}

	public CCPropertiesLoader(String filename) {
		load(filename);
	}
	
	public void load(String filename) {
		Properties p = new Properties();
		try {
			InputStream is = new FileInputStream(filename);
			p.loadFromXML(is);
			propertieslist.add(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Properties> getPropertieslist() {
		return propertieslist;
	}
	
	public Properties getProperties(String lang) {
		for(Properties p : propertieslist) {
			if(p.getProperty("language").equals(lang)) {
				return p;
			}
		}
		
		return null;
	}
}
