/*
 * PPresVisualizer.java
 * Created on 2011/07/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 * 
 * Moniter非表示版PPDataManager
 * @author Motoki Hirao
 * 
 * 要リファクタリング
 */

package createcocodata.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import ppv.app.datamanager.IPPVLoader;
import ppv.app.datamanager.PPMissingFileManager;
import ppv.app.datamanager.PPProjectSet;
import ppv.view.frames.PPProjectSetViewerFrame;
import pres.loader.model.PLProject;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CPath;
import clib.common.utils.CNullProgressMonitor;
import clib.view.dialogs.CErrorDialog;

/**
 * @author macchan
 */
public class PPDataManager2 {

	private static final String DATA_DIR = "ppv.data/data";
	private static final String WORK_DIR = "ppv.data/cash";
	private static final String TMP_DIR = "ppv.data/tmp";
	private static final String WORKLIB_DIR = "ppv.lib";

	public static final String BASE_DIR_IN_PROJECT_NAME = "project";

	private static PPDataManager2 current;

	public static PPDataManager2 getCurrent() {
		return current;
	}

	private CDirectory baseDir;
	private CDirectory libDir;

	public PPDataManager2(CDirectory baseDir) {
		this.baseDir = baseDir;
		current = this;
		this.libDir = getBaseDir().findOrCreateDirectory(WORKLIB_DIR);
	}

	/**
	 * @return the baseDir
	 */
	public CDirectory getBaseDir() {
		return baseDir;
	}

	/*********************************************************
	 * ディレクトリ取得関係
	 *********************************************************/

	public CDirectory getDataDir() {
		return getBaseDir().findOrCreateDirectory(DATA_DIR);
	}

	public CDirectory getWorkDir() {
		return getBaseDir().findOrCreateDirectory(WORK_DIR);
	}

	public CDirectory getTMPDir() {
		return getBaseDir().findOrCreateDirectory(TMP_DIR);
	}

	public void setLibDir(CDirectory libDir) {
		this.libDir = libDir;
	}

	public CDirectory getLibDir() {
		return this.libDir;
	}

	public static CDirectory getProjectDir(CDirectory dir) {
		CDirectory projectDir = dir.findDirectory(BASE_DIR_IN_PROJECT_NAME);
		try {
			PLProject.checkAppropreateDir(projectDir);
		} catch (Exception ex) {
			return null;
		}
		return projectDir;
	}

	/*********************************************************
	 * ProjectSetを開く処理
	 *********************************************************/

	public PPProjectSet openProjectSet(String projectSetName, boolean load,
			boolean compile, boolean coco) {
		if (getDataDir().findDirectory(projectSetName) == null) {
			throw new RuntimeException(
					"openProjectSet そのような名前のProjectSetはありません．projectSetName = "
							+ projectSetName);
		}
		CDirectory projectSetDir = getDataDir().findDirectory(projectSetName);
		return openProjectSet2(projectSetDir, load, compile, coco);
	}

	@Deprecated
	public PPProjectSet openProjectSet(final CDirectory projectSetDir,
			boolean load) {
		return openProjectSet2(projectSetDir, load, false, false);
	}

	private PPProjectSet openProjectSet2(final CDirectory projectSetDir,
			final boolean load, final boolean compile, final boolean coco) {
		final PPProjectSet projectSet = new PPProjectSet(projectSetDir);
		try {
			openProjectSetInternal(projectSet, load, compile, coco);
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(null, "", ex);
		}
		return projectSet;
	}

	private void openProjectSetInternal(PPProjectSet projectSet, boolean load,
			boolean compile, boolean coco) throws IOException {
		// create a projectset
		loadProjectSet(projectSet, load, compile);

		// open view
		final PPProjectSetViewerFrame viewer = new PPProjectSetViewerFrame(
				projectSet);
		if (!coco) {
			viewer.setBounds(100, 100, 500, 500);
			viewer.setVisible(true);
		} else {
			viewer.doPrintCompileErrorCSV(baseDir);
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				viewer.fitScale();
			}
		});
	}

	public void loadProjectSet(PPProjectSet projectSet, boolean load,
			boolean compile) {
		List<CDirectory> dirs = projectSet.getDir().getDirectoryChildren();
		for (CDirectory dir : dirs) {
			try {
				PLProject project = createProject(dir,
						projectSet.getSrcDirName());
				if (project == null) {
					System.err.println("Project is null in "
							+ dir.getNameByString());
					continue;
				}
				if (project != null) {
					projectSet.add(project);
				}
			} catch (Exception ex) {
				System.err.println("Project initializing error occured in "
						+ dir.getNameByString());
				ex.printStackTrace();
			}
		}

		// load and prone
		if (load) {
			loadAllProjects(projectSet);
			if (compile) {
				createCompileCash(projectSet);
			}
		}
	}

	private PLProject createProject(CDirectory dir, String srcDirName) {
		CDirectory projectDir = dir.findDirectory(BASE_DIR_IN_PROJECT_NAME);
		try {
			PLProject.checkAppropreateDir(projectDir);
		} catch (Exception ex) {
			return null;
		}
		PLProject project = new PLProject(dir.getNameByString(), projectDir,
				new CPath(srcDirName));
		return project;
	}

	/*********************************************************
	 * データ入出力関係
	 *********************************************************/

	public void loadAllProjects(PPProjectSet projectSet) {
		List<PLProject> projects = projectSet.getProjects();
		for (PLProject project : projects) {
			project.load();
		}
		projectSet.prune(false);
	}

	public void loadDir(final CDirectory dirFrom, final IPPVLoader loader) {
		CDirectory dirTo = getDataDir().findOrCreateDirectory(
				dirFrom.getNameByString());
		List<CFile> children = dirFrom.getFileChildren();
		for (CFile file : children) {
			try {
				loadZip(file, dirTo, loader);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// post processs
		loader.postProcess(dirTo);
		// 読み込み後，　ファイル補完．
		PPProjectSet set = new PPProjectSet(dirTo);
		PPMissingFileManager.doProcess(set, new CNullProgressMonitor());
	}

	public void loadOneFile(CFile file, CDirectory dirTo, IPPVLoader loader) {
		if ("zip".equals(file.getName().getExtension())) {
			loadZip(file, dirTo, loader);

			// bug fix#7
			// post process
			loader.postProcess(dirTo);
			// 読み込み後，　ファイル補完．
			PPProjectSet set = new PPProjectSet(dirTo);
			PPMissingFileManager.doProcess(set, new CNullProgressMonitor());
		}
	}

	private void loadZip(CFile file, CDirectory dirTo, IPPVLoader loader) {
		if ("zip".equals(file.getName().getExtension())) {
			loader.load(file, dirTo);
		}
	}

	public void deleteProjectSet(CDirectory dir) {
		dir.delete();
	}

	public void createCompileCash(PPProjectSet projectSet) {
		CDirectory workDir = getWorkDir();
		CDirectory libDir = getLibDir();
		CDirectory dir = workDir.findOrCreateDirectory(projectSet.getName());
		projectSet.compileAllProjects(dir, libDir, new CNullProgressMonitor());
		projectSet.prune(true);
	}

	public void clearCompileCash() {
		try {
			clearCashInternal();
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(null, "", ex);
		}
	}

	private void clearCashInternal() {
		CDirectory workDir = getWorkDir();
		final List<CDirectory> projects = new ArrayList<CDirectory>();
		for (CDirectory projectSet : workDir.getDirectoryChildren()) {
			for (CDirectory project : projectSet.getDirectoryChildren()) {
				projects.add(project);
			}
		}
		for (CDirectory project : projects) {
			project.delete();
		}
		workDir.delete();
	}
}
