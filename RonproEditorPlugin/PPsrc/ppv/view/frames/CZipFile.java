/*
 * CZipFile.java
 * Created on 2012/03/01
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.frames;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFrame;
import javax.swing.JPanel;

import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.utils.CFrameTester;
import clib.view.dnd.CFileDropInDataTransferHandler;
import clib.view.dnd.ICFileDroppedListener;

/**
 * @author macchan
 * 
 */
public class CZipFile {

	private CFile file;

	/**
	 * Constructor
	 */
	public CZipFile(CFile file) {
		this.file = file;
	}

	public CZDirectory getRoot() {
		CZDirectory root = new CZDirectory(null);
		try {
			ZipFile zipfile = new ZipFile(file.toJavaFile());
			Enumeration<? extends ZipEntry> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				System.out.println(entry.getName());
			}
			zipfile.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return root;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = CFrameTester.open(new JPanel());
		CFileDropInDataTransferHandler.set(frame, new AFileDroppedListener());
	}

	static class AFileDroppedListener implements ICFileDroppedListener {
		@Override
		public void fileDropped(List<File> files) {
			for (File file : files) {
				new CZipFile(CFileSystem.findFile(file.getAbsolutePath()));
			}
		}
	}
}
