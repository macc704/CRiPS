/*
 * PRRecordingProjectFileManager.java
 * Created on Apr 7, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import pres.core.model.PRCommandLog;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CPath;
import clib.common.filesystem.CRecursiveExplorer;
import clib.common.filesystem.ICRecursiveExplorerHandler;

/**
 * PRRecordingProjectFileManager
 */
public class PRRecordingFileManager {

	private PRRecordingProject project;

	private Map<CPath, PRRecordingFile> targetFiles;

	/**
	 * Constructor
	 */
	public PRRecordingFileManager(PRRecordingProject project) {
		this.project = project;
		initializeTargetFiles();
	}

	protected void initializeTargetFiles() {
		targetFiles = new LinkedHashMap<CPath, PRRecordingFile>();
		refreshTargetFiles();
	}

	// 監視対象ファイルリストを更新する
	protected void refreshTargetFiles() {
		CRecursiveExplorer explorer = new CRecursiveExplorer(
				new ICRecursiveExplorerHandler() {
					public void processFile(CFile file) throws Exception {
						putTargetFile(file);
					}

					public void processDir(CDirectory dir) throws Exception {
					}
				});
		explorer.setFileFilter(project.getFileFilter());
		explorer.setDirFilter(project.getDirFilter());
		explorer.explore(project.getBaseDirectory());
	}

	// 監視対象ファイルをリストに追加する
	private void putTargetFile(CFile file) {
		CPath path = file.getRelativePath(project.getBaseDirectory());
		if (!targetFiles.containsKey(path)) {
			targetFiles.put(path, new PRRecordingFile(project, path));
		}
	}

	// 監視対象ファイルを取得する
	protected PRRecordingFile getRecordingFile(CPath path) {
		refreshTargetFiles();
		return this.targetFiles.get(path);
	}

	// 監視する（削除されていれば，監視対象から削除する）
	protected void checkTargetFilesAndUpdate() {
		refreshTargetFiles();

		Collection<PRRecordingFile> files = targetFiles.values();
		for (Iterator<PRRecordingFile> i = files.iterator(); i.hasNext();) {
			PRRecordingFile file = i.next();
			if (file.deleted()) {
				i.remove();
				continue;
			} else if (!file.hasUpdated()) {
				file.stamp();
				project.record(new PRCommandLog(PRCommandLog.SubType.UPDATED,
						file.getPath()));
			}
		}
	}
}
