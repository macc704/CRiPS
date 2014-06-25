package ch.library;

import java.util.ArrayList;
import java.util.List;

import ch.conn.framework.CHFile;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.filesystem.sync.CFileHashList;
import clib.common.filesystem.sync.CFileListDifference;
import clib.common.filesystem.sync.CFileListUtils;
import clib.common.table.CCSVFileIO;

public class CHFileSystem {
	
	public static String PROJECTPATH = "runtime-EclipseApplication/final";
	public static String MEMBERDIRPATH = "runtime-EclipseApplication/.ch";
	public static String PREFPATH = "runtime-EclipseApplication/.ch/.pref";

	private static CDirectory getBaseDir(int port) {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				"CH/" + port);
	}

	public static CDirectory getUserDirForServer(String user, int port) {
		return getBaseDir(port).findOrCreateDirectory(user);
	}

	public static CDirectory getFinalProjectDir() {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				"MyProjects/final");
	}

	public static CDirectory getUserDirForClient(String user) {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				"MyProjects/.CH/" + user + "/final");
	}
	
	// for plug-in
	public static CDirectory getEclipseProjectDir() {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(PROJECTPATH);
	}
	
	// for plug-in
	public static CDirectory getEclipseMemberDir() {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(MEMBERDIRPATH);
	}
	
	public static CFile getPrefFile() {
		return CFileSystem.getExecuteDirectory().findOrCreateFile(PREFPATH);
	}
	
	public static CFileHashList getEclipseFinalProjectFileList(){
		return createFileList(getEclipseProjectDir());
	}

	// processFilelistRequest and Response server
	public static CFileHashList getServerFileList(String user, int port) {
		return createFileList(getUserDirForServer(user, port));
	}

	// processFilelistRequest client
	public static CFileHashList getFinalProjectFileList() {
		return createFileList(getFinalProjectDir());
	}
	
	// for plug-in
	public static CFileHashList getEclipseProjectFileList() {
		return createFileList(getEclipseProjectDir());
	}

	// processFileListResponse client
	public static CFileHashList getClientFileList(String user) {
		return createFileList(getUserDirForClient(user));
	}

	/* 単体テスト対象！ */
	public static void sync(CDirectory from, CDirectory to) {
		List<String> requestFilePaths = CHFileSystem.getRequestFilePaths(
				createFileList(from), to);
		List<CHFile> files = CHFileSystem.getCHFiles(requestFilePaths, from);
		CHFileSystem.saveFiles(files, to);
	}

	public static void pull(CDirectory from, CDirectory to, CFileFilter filter) {
		List<String> requestFilePaths = CHFileSystem.getRequestFilePaths1(
				createFileList(from, filter), to);
		List<CHFile> files = CHFileSystem.getCHFiles(requestFilePaths, from);
		CHFileSystem.saveFiles(files, to);
	}

	public static CFileHashList createFileList(CDirectory dir) {
		return new CFileHashList(dir, CFileFilter.IGNORE_BY_NAME_FILTER(/*".*",*/
				".class", ".xml"));
	}

	public static CFileHashList createFileList(CDirectory dir,
			CFileFilter filter) {
		return new CFileHashList(dir, filter);
	}

	/*
	 * TODO 2つの仕事してない？ TODO CFileListを返した方が良い？
	 */
	public static List<String> getRequestFilePaths(CFileHashList fromList,
			CDirectory to) {

		CFileHashList toList = createFileList(to);

		List<CFileListDifference> differences = CFileListUtils.compare(
				fromList, toList);

		List<String> requestFilePaths = new ArrayList<String>();
		for (CFileListDifference aDifference : differences) {
			switch (aDifference.getKind()) {
			case CREATED:
			case UPDATED:
				requestFilePaths.add(aDifference.getPath());
				break;
			case REMOVED:
				to.findChild(new CPath(aDifference.getPath())).delete();
				break;
			default:
				throw new RuntimeException();
			}
		}

		return requestFilePaths;
	}

	// 一時繋ぎ
	public static List<String> getRequestFilePaths1(CFileHashList fromList,
			CDirectory to) {

		CFileHashList toList = createFileList(to);

		List<CFileListDifference> differences = CFileListUtils.compare(
				fromList, toList);

		List<String> requestFilePaths = new ArrayList<String>();
		for (CFileListDifference aDifference : differences) {
			switch (aDifference.getKind()) {
			case CREATED:
			case UPDATED:
				requestFilePaths.add(aDifference.getPath());
				break;
			case REMOVED:
				break;
			default:
				throw new RuntimeException();
			}
		}

		return requestFilePaths;
	}

	public static List<CHFile> getCHFiles(List<String> requestFilePaths,
			CDirectory dir) {
		List<CHFile> chFiles = new ArrayList<CHFile>();
		for (String path : requestFilePaths) {
			CFile file = dir.findFile(path);
			byte[] byteArray = file.loadAsByte();
			chFiles.add(new CHFile(path, byteArray));
		}
		return chFiles;
	}

	public static int getFileSize(List<CHFile> files) {
		int fileSize = 0;
		for (CHFile aFile : files) {
			fileSize += aFile.getBytes().length;
		}
		return fileSize;
	}

	public static void saveFiles(List<CHFile> files, CDirectory dir) {
		for (CHFile aFile : files) {
			CFile file = dir.findOrCreateFile(aFile.getPath());
			file.saveAsByte(aFile.getBytes());
		}
	}

	public static void copyUserDirToMyProjects(String user) {
		CDirectory masterDir = CHFileSystem.getUserDirForClient(user);
		CDirectory copyDir = CFileSystem.getExecuteDirectory()
				.findOrCreateDirectory("MyProjects/" + user);
		List<String> requestFilePaths = CHFileSystem.getRequestFilePaths(
				createFileList(masterDir), copyDir);

		List<CHFile> files = CHFileSystem.getCHFiles(requestFilePaths,
				masterDir);
		CHFileSystem.saveFiles(files, copyDir);
	}

	public static CFile getEntryUserList(int port) {
		if (getServerDir(port).findFile("EntryUserList.csv") == null) {
			CFile file = getServerDir(port).findOrCreateFile(
					"EntryUserList.csv");
			initEntryUserList(file);
		}
		return CFileSystem.getExecuteDirectory().findOrCreateFile(
				"CH/" + port + "/EntryUserList.csv");
	}

	public static CDirectory getServerDir(int port) {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				"CH/" + port);
	}

	public static void initEntryUserList(CFile file) {
		List<List<String>> table = new ArrayList<List<String>>();
		List<String> header = new ArrayList<String>();
		header.add("User");
		header.add("Password");
		table.add(header);
		CCSVFileIO.saveByListList(table, file);
	}
}
