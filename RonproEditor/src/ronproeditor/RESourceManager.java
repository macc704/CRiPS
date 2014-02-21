/*
 * RESourceManager.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */
package ronproeditor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Date;
import java.util.List;

import ronproeditor.helpers.FileSystemUtil;
import ronproeditor.helpers.MatcherUtil;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;

/**
 * Class RESourceManager
 * 
 * @author macchan
 * @version $Id: RESourceManager.java,v 1.2 2007/12/14 15:40:45 macchan Exp $
 */
public class RESourceManager implements ICFwResourceRepository {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	private File rootDirectory = new File(".");
	private File projectDirectory = null;
	private File currentFile = null;

	// private File prevCompileFile = null;

	public RESourceManager() {
	}

	/***************************
	 * Root操作
	 ***************************/

	public File getRootDirectory() {
		return rootDirectory;
	}

	protected void setRootDirectory(File rootDirectory) {
		if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
			throw new RuntimeException("root directory missed.");
		}
		this.rootDirectory = rootDirectory;
		fireRefreshedEvent();
	}

	protected void fireRefreshedEvent() {
		propertyChangeSupport.firePropertyChange(MODEL_REFRESHED, null, null);
	}

	/***************************
	 * Project操作
	 ***************************/

	public File getProjectDirectory() {
		return projectDirectory;
	}

	protected void setProjectDirectory(File projectDirectory) {
		// if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
		// throw new RuntimeException("project directory missed.");
		// }

		this.projectDirectory = projectDirectory;
		propertyChangeSupport.firePropertyChange(PROJECT_REFRESHED, null, null);
	}

	public boolean canCreateProject(String name) {
		return rootDirectory.exists()
				&& !new File(rootDirectory, name).exists();
	}

	protected void createProject(String name) {
		if (!canCreateProject(name)) {
			throw new RuntimeException();
		}

		try {
			File newProject = new File(rootDirectory, name);
			newProject.mkdir();
			propertyChangeSupport.firePropertyChange(MODEL_REFRESHED, null,
					null);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/***************************
	 * ファイル操作
	 ***************************/

	public File getCurrentFile() {
		return this.currentFile;
	}

	// public File getPrevCompileFile() {
	// return prevCompileFile;
	// }

	// public void updatePrevCompileFile() {
	// CFile currentFile = getCCurrentFile();
	// CPath ParentPath = currentFile.getAbsolutePath().getParentPath();
	// String prevCompileFileName = currentFile.getLastModified() + ".java";
	// CPath prevCompileFilePath = ParentPath.appendedPath("/.pres2/"
	// + currentFile.getName() + "/" + prevCompileFileName);
	// this.prevCompileFile = prevCompileFilePath.toJavaFile();
	// }

	public boolean hasCurrentFile() {
		return this.currentFile != null;
	}

	protected void open(File file) {
		if (currentFile != null) {
			throw new RuntimeException();
		}

		propertyChangeSupport.firePropertyChange(PREPARE_DOCUMENT_OPEN, null,
				null);
		this.currentFile = file;
		setProjectDirectory(file.getParentFile());
		propertyChangeSupport.firePropertyChange(DOCUMENT_OPENED, null,
				this.currentFile);
	}

	protected void close() {
		if (currentFile == null) {
			throw new RuntimeException();
		}

		propertyChangeSupport.firePropertyChange(PREPARE_DOCUMENT_CLOSE, null,
				null);
		File oldFile = this.currentFile;
		this.currentFile = null;
		propertyChangeSupport
				.firePropertyChange(DOCUMENT_CLOSED, oldFile, null);
	}

	public boolean canCreateFile(String name) {
		return canCreateFile(new CPath(this.projectDirectory.getPath()), name);
	}

	public boolean canCreateFile(CPath dir, String name) {
		return dir != null
				&& dir.exists()
				&& !new File(dir.toJavaFile(), name + "."
						+ REApplication.FILE_EXTENSION).exists();
	}

	protected void createFile(String name, RESourceTemplate template) {
		if (!canCreateFile(name)) {
			throw new RuntimeException();
		}

		template.setClName(name);
		File file = new File(projectDirectory, name + "."
				+ REApplication.FILE_EXTENSION);
		FileSystemUtil.save(file, template.getSource());
		fireRefreshedEvent();
		open(file);
	}

	/*************************************
	 * Refactor関連
	 *************************************/

	public void refactorProjectName(String inputtedName) {
		projectDirectory.renameTo(new File(projectDirectory.getParentFile(),
				inputtedName));
		fireRefreshedEvent();
	}

	public void copyFile(File source, CPath project, String inputtedName) {
		if (!project.exists()) {
			throw new IllegalArgumentException();
		}

		// new filename
		File newFile = new File(project.toJavaFile(), inputtedName + "."
				+ REApplication.FILE_EXTENSION);
		FileSystemUtil.copyFile(source, newFile);

		replaceClassName(source, newFile, inputtedName);

		// refresh
		fireRefreshedEvent();
		open(newFile);
	}

	public void refactorFileName(File target, String inputtedName) {
		// change filename
		File newFile = new File(target.getParentFile(), inputtedName + "."
				+ REApplication.FILE_EXTENSION);
		boolean result = target.renameTo(newFile);
		if (!result) {
			throw new RuntimeException("Changing filename has been failed.");
		}

		replaceClassName(target, newFile, inputtedName);

		// refresh
		fireRefreshedEvent();
		open(newFile);
	}

	private void replaceClassName(File source, File target, String newClassName) {
		try {
			String oldClassName = FileSystemUtil.cutExtension(source);
			String oldText = FileSystemUtil.load(target,
					REApplication.SRC_ENCODING);
			String newText = MatcherUtil.replaceAll(oldText, oldClassName,
					newClassName);
			FileSystemUtil.save(target, newText);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*************************************
	 * Trash関連
	 *************************************/

	protected File makeTrashFolder() {
		String name = new Date().toString();
		name = name.replace(" ", "");
		name = name.replace(":", "");
		File file = new File(getTrashRoot(), name);
		file.mkdir();
		return file;
	}

	private File getTrashRoot() {
		File dir = new File(new File("."), REApplication.TRASH_FOLDER);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	/*************************************
	 * イベント関連
	 *************************************/

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/***************************
	 * つなぎ
	 ***************************/

	private CFileFilter fileFilter = CFileFilter.ALL_ACCEPT_FILTER();
	private CFileFilter dirFilter = CFileFilter.ALL_ACCEPT_FILTER();

	public CFile getCCurrentFile() {
		return (CFile) toCFile(getCurrentFile());
	}

	public CDirectory getCCurrentProject() {
		return (CDirectory) toCFile(getProjectDirectory());
	}

	public CDirectory getCRootDirectory() {
		return (CDirectory) toCFile(getRootDirectory());
	}

	public List<CDirectory> getAllProjects() {
		return getCRootDirectory().getDirectoryChildren(
				CFileFilter.IGNORE_BY_NAME_FILTER(".*"));
	}

	public CFileFilter getDirFilter() {
		return dirFilter;
	}

	public CFileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(CFileFilter fileFilter) {
		this.fileFilter = fileFilter;
		this.fireRefreshedEvent();
	}

	public void setDirFilter(CFileFilter dirFilter) {
		this.dirFilter = dirFilter;
		this.fireRefreshedEvent();
	}

	private CFileElement toCFile(File file) {
		return CFileSystem.convertToCFile(file);
	}

}