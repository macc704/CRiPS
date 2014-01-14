package ch.library;

import java.util.ArrayList;
import java.util.List;

import ch.conn.framework.CHFile;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;
import clib.common.filesystem.sync.CFileList;
import clib.common.filesystem.sync.CFileListDifference;
import clib.common.filesystem.sync.CFileListUtils;

public class CHFileSystem {

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

	// processFilelistRequest and Response server
	public static CFileList getServerFileList(String user, int port) {
		return new CFileList(getUserDirForServer(user, port));
	}

	// processFilelistRequest client
	public static CFileList getFinalProjectFileList() {
		return new CFileList(getFinalProjectDir());
	}

	// processFileListResponse client
	public static CFileList getClientFileList(String user) {
		return new CFileList(getUserDirForClient(user));
	}

	public static List<String> getRequestFilePaths(CFileList master,
			CDirectory copyDir) {

		CFileList copy = new CFileList(copyDir);

		List<CFileListDifference> differences = CFileListUtils.compare(master,
				copy);

		List<String> requestFilePaths = new ArrayList<String>();
		for (CFileListDifference aDifference : differences) {
			switch (aDifference.getKind()) {
			case CREATED:
			case UPDATED:
				requestFilePaths.add(aDifference.getPath());
				break;
			case REMOVED:
				copyDir.findChild(new CPath(aDifference.getPath())).delete();
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

	public static CFile getEntryUserList(int port) {
		return CFileSystem.getExecuteDirectory().findOrCreateFile(
				"CH/" + port + "/EntryUserList.csv");
	}
}
