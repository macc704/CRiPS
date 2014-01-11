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

public class CHFileSystem {

	public static final int SERVER = 0;
	public static final int CLIENT = 1;

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

	public static List<String> getRequestFilePaths(
			List<CFileListDifference> differences, CDirectory copy) {

		List<String> requestFilePaths = new ArrayList<String>();
		for (CFileListDifference aDifference : differences) {
			switch (aDifference.getKind()) {
			case CREATED:
			case UPDATED:
				requestFilePaths.add(aDifference.getPath());
				break;
			case REMOVED:
				copy.findChild(new CPath(aDifference.getPath())).delete();
				break;
			default:
				throw new RuntimeException();
			}
		}

		return requestFilePaths;
	}

	// for server
	public static List<CHFile> getCHFiles(List<String> requestFilePaths,
			String user, int port) {

		List<CHFile> chFiles = new ArrayList<CHFile>();
		for (String path : requestFilePaths) {
			CFile file;
			if (user.equals("") && port == 0) {
				file = getFinalProjectDir().findFile(path);
			} else {
				file = getUserDirForServer(user, port).findFile(path);
			}
			byte[] byteArray = file.loadAsByte();
			chFiles.add(new CHFile(path, byteArray));
		}

		return chFiles;
	}

	// for client
	public static List<CHFile> getCHFiles(List<String> requestFilePaths) {
		return getCHFiles(requestFilePaths, "", 0);
	}

	// for server
	public static void saveFiles(List<CHFile> files, String user, int port) {

		CDirectory cDir;
		if (user.equals("") && port == 0) {
			cDir = getFinalProjectDir();
		} else {
			cDir = getUserDirForServer(user, port);
		}

		for (CHFile aFile : files) {
			CFile file = cDir.findOrCreateFile(aFile.getPath());
			file.saveAsByte(aFile.getBytes());
		}
	}

	// for client
	public static void saveFiles(List<CHFile> files) {
		saveFiles(files, "", 0);
	}
}
