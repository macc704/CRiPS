/*
 * PPConvert.java
 * Created on 2011/06/20
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.datamanager;

import java.util.ArrayList;
import java.util.List;

import pres.core.PRRecordingProject;
import pres.loader.model.IPLFileStamp;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CPath;
import clib.common.time.CTime;
import clib.common.utils.ICProgressMonitor;

/*
 * @author macchan 
 * 最終的なファイルと，履歴ファイルの差分を調べます 
 * 最終的なファイルがあるのに，履歴ファイルがない場合は補完します．
 */
public class PPMissingFileManager {

	private static final boolean DEBUG = true;

	private static final String PRES2 = PRRecordingProject.RECORDING_DIRNAME_DEFAULT;
	private static final String EXTENSION = "java";

	public static void doProcess(PPProjectSet projectSet,
			ICProgressMonitor monitor) {
		new PPMissingFileManager().doProcessInternal(projectSet, monitor);
	}

	private void doProcessInternal(PPProjectSet projectSet,
			ICProgressMonitor monitor) {
		CDirectory projectSetDir = projectSet.getDir();
		List<CDirectory> children = projectSetDir.getDirectoryChildren();
		monitor.setWorkTitle("managing MissingFiles..");
		monitor.setMax(children.size());
		for (CDirectory child : children) {
			try {
				manageLatestForProject(child, projectSet.getSrcDirName());
			} catch (Exception ex) {
				System.err.println("error happened in " + child.getName());
				ex.printStackTrace();
			} finally {
				monitor.progress(1);
			}
		}
	}

	private void manageLatestForProject(CDirectory projectDir, String srcDirName) {
		CDirectory proDir = projectDir
				.findDirectory(PPDataManager.BASE_DIR_IN_PROJECT_NAME);
		PLProject pj = new PLProject(projectDir.getNameByString(), proDir,
				new CPath(srcDirName));
		List<CPath> paths = findJavaSrcBase(proDir);
		// printBoth(pj, paths);
		manageFiles(proDir, pj, paths);
	}

	private void manageFiles(CDirectory proDir, PLProject pj, List<CPath> paths) {
		if (DEBUG) {
			System.out.println("------------------------------------");
			System.out.println(pj.getName());
			if (!pj.isValid()) {
				System.out.println("Project " + pj.getName() + " is not valid");
				return;
			}
		}
		for (CPath path : paths) {
			PLFile presFile = pj.getFile(path);
			if (presFile != null) {
				IPLFileStamp lastStamp = presFile.getStamps().getLast();
				int presLen = lastStamp.getFile().loadText().length();
				CFile file = proDir.findFile(path);
				int orgLen = file.loadText().length();
				int diff = presLen - orgLen;
				if (diff == 0 && DEBUG) {
					System.out.println(path + " was checked OK");
				}
				if (diff != 0) {
					System.out.println(path + " Difference->" + diff);
				}
				if (diff != 0) {
					CTime time = new CTime(lastStamp.getFile()
							.getLastModified() + 1);
					copy(proDir, path, time);
				}
			} else {
				CTime time = pj.getRootPackage().getStart();
				copy(proDir, path, time);
			}
		}
	}

	private void copy(CDirectory proDir, CPath path, CTime time) {
		CDirectory presDir = proDir.findDirectory(PRES2);
		CFile file = proDir.findFile(path);
		CDirectory fileDir = presDir.findOrCreateDirectory(path);
		file.copyTo(fileDir, Long.toString(time.getAsLong()) + ".java");
		if (DEBUG) {
			System.out.println(path + " was Copied");
		}
	}

	private List<CPath> findJavaSrcBase(CDirectory base) {
		List<CPath> paths = findJavaSrcDir(base, base);
		List<CDirectory> children = base.getDirectoryChildren();
		for (CDirectory child : children) {
			if (!child.getNameByString().equals(PRES2)) {
				paths.addAll(findJavaSrcDirRec(child, base));
			}
		}
		return paths;
	}

	private List<CPath> findJavaSrcDirRec(CDirectory dir, CDirectory base) {
		List<CPath> paths = findJavaSrcDir(dir, base);
		List<CDirectory> children = dir.getDirectoryChildren();
		for (CDirectory child : children) {
			paths.addAll(findJavaSrcDirRec(child, base));
		}
		return paths;
	}

	private List<CPath> findJavaSrcDir(CDirectory dir, CDirectory base) {
		List<CPath> paths = new ArrayList<CPath>();
		List<CFile> children = dir.getFileChildren();
		for (CFile child : children) {
			if (child.getName().getExtension().equals(EXTENSION)) {
				paths.add(child.getRelativePath(base));
			}
		}
		return paths;
	}

}

// public class PPConverter {
// //
// // void run() {
// // PPDataManager manager = new PPDataManager(
// // CFileSystem.getExecuteDirectory());
// // CDirectory datadir = manager.getDataDir();
// // for (CDirectory child : datadir.getDirectoryChildren()) {
// // doProcess(child);
// // }
// // }
// //
// // private void doProcess(CDirectory datadir) {
// // renameDirs(datadir);
// // manageLatestVersion(datadir);
// // }
// //
// // void renameDirs(CDirectory parent) {
// // List<CDirectory> children = parent.getDirectoryChildren();
// // for (CDirectory child : children) {
// // try {
// // String original = child.getNameByString();
// // String[] names = original.split("_");
// // if (names.length == 2) {
// // int number = Integer.parseInt(names[1]);
// // if (number < 1000) {
// // child.renameTo(formatter.format(number) + "_"
// // + original);
// // }
// // }
// // // else if (names.length == 3) {
// // // int number = Integer.parseInt(names[0]);
// // // String newName = formatter.format(number) + "_" + names[1];
// // // child.renameTo(newName);
// // // }
// // } catch (Exception ex) {
// // continue;
// // }
// // }
// // }
// //
// // void manageLatestVersion(CDirectory parent) {
// // List<CDirectory> children = parent.getDirectoryChildren();
// // for (CDirectory child : children) {
// // try {
// // manageLatestForProject(child);
// // } catch (Exception ex) {
// // // ex.printStackTrace();
// // }
// // }
// // }
// //
// // void manageLatestForProject(CDirectory base) {
// // CDirectory proDir = base
// // .findDirectory(PPDataManager.BASE_DIR_IN_PROJECT_NAME);
// // PLProject pj = new PLProject(base.getNameByString(), proDir, new CPath(
// // SRC_DIR));
// // List<CPath> paths = findJavaSrcBase(proDir);
// // // printBoth(pj, paths);
// // manageFiles(proDir, pj, paths);
// // }
// //
// // private void manageFiles(CDirectory proDir, PLProject pj, List<CPath>
// // paths) {
// // System.out.println("------------------------------------");
// // System.out.println(pj.getName());
// // for (CPath path : paths) {
// // PLFile presFile = pj.getFile(path);
// // if (presFile != null) {
// // IPLFileStamp lastStamp = presFile.getStamps().getLast();
// // int presLen = lastStamp.getFile().loadText().length();
// // CFile file = proDir.findFile(path);
// // int orgLen = file.loadText().length();
// // int diff = presLen - orgLen;
// // System.out.println(path + " Difference->" + diff);
// // if (diff != 0) {
// // CTime time = new CTime(lastStamp.getFile()
// // .getLastModified() + 1);
// // copy(proDir, path, time);
// // }
// // } else {
// // CTime time = pj.getRootPackage().getStart();
// // copy(proDir, path, time);
// // }
// // }
// // }
// //
// // private void copy(CDirectory proDir, CPath path, CTime time) {
// // CDirectory presDir = proDir.findDirectory(PRES2);
// // CFile file = proDir.findFile(path);
// // CDirectory fileDir = presDir.findOrCreateDirectory(path);
// // file.copyTo(fileDir, Long.toString(time.getAsLong()) + ".java");
// // System.out.println(path + " was Copied");
// // }
// //
// // @SuppressWarnings("unused")
// // private void printBoth(PLProject pj, List<CPath> paths) {
// // System.out.println("------------------------------------");
// // System.out.println(pj.getName());
// // for (PLFile file : pj.getFiles()) {
// // System.out.println(file.getPath());
// // }
// //
// // System.out.println("------");
// // for (CPath path : paths) {
// // System.out.println(path);
// // }
// // }
// //
// // private List<CPath> findJavaSrcBase(CDirectory base) {
// // List<CPath> paths = findJavaSrcDir(base, base);
// // List<CDirectory> children = base.getDirectoryChildren();
// // for (CDirectory child : children) {
// // if (!child.getNameByString().equals(PRES2)) {
// // paths.addAll(findJavaSrcDirRec(child, base));
// // }
// // }
// // return paths;
// // }
// //
// // private List<CPath> findJavaSrcDirRec(CDirectory dir, CDirectory base) {
// // List<CPath> paths = findJavaSrcDir(dir, base);
// // List<CDirectory> children = dir.getDirectoryChildren();
// // for (CDirectory child : children) {
// // paths.addAll(findJavaSrcDirRec(child, base));
// // }
// // return paths;
// // }
// //
// // private List<CPath> findJavaSrcDir(CDirectory dir, CDirectory base) {
// // List<CPath> paths = new ArrayList<CPath>();
// // List<CFile> children = dir.getFileChildren();
// // for (CFile child : children) {
// // if (child.getName().getExtension().equals("java")) {
// // paths.add(child.getRelativePath(base));
// // }
// // }
// // return paths;
// // }
// }