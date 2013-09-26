/*
 * PRRecordingFile.java
 * Created on 2010/02/11 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CPath;

/**
 * PRRecordingFile
 * FileRecordingのルールに責任を持つ
 */
public class PRRecordingFile {

	private PRRecordingProject project;
	private CPath path;
	private CFile _file; //キャッシュ

	/**
	 * Constructor
	 * @param project プロジェクト
	 * @param path 相対パス
	 */
	public PRRecordingFile(PRRecordingProject project, CPath path) {
		this.project = project;
		this.path = path;
	}

	public CPath getPath() {
		return this.path;
	}

	public boolean deleted() {
		return getFile().deleted();
	}

	private CFile getFile() {
		if (_file == null) {
			_file = project.getBaseDirectory().findFile(path);
		}
		return _file;
	}

	/*****************************************************
	 * FileRecordingのルール
	 * input:
	 * basedir:base
	 * path:a/b/C.java
	 * recordingdir:record
	 * 
	 * output:
	 * record/a/b/C/java/XXXXXXXXX(lastmodifiled).java
	 *****************************************************/

	public boolean hasUpdated() {
		if (deleted()) {
			return false;
		}

		return getRecordingFileDir().findFile(new CPath(createFilename())) != null;
	}

	public void stamp() {
		if (hasUpdated()) {
			project.recordDebug("already stamped.: " + path.toString());
			return;
		}
		getFile().copyTo(getRecordingFileDir(), createFilename());
	}

	private CDirectory getRecordingFileDir() {
		CDirectory recordBase = project.getRecordingDirectory();
		CDirectory recordFileDir = recordBase.findOrCreateDirectory(path);
		return recordFileDir;
	}

	private String createFilename() {
		return Long.toString(getFile().getLastModified()) + "."
				+ getFile().getName().getExtension();
	}

}
