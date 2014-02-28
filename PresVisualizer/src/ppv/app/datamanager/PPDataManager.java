/*
 * PPresVisualizer.java
 * Created on 2011/07/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.datamanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import ppv.view.frames.PPProjectSetViewerFrame;
import pres.loader.model.PLProject;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CPath;
import clib.common.thread.ICTask;
import clib.common.utils.ICProgressMonitor;
import clib.view.dialogs.CErrorDialog;
import clib.view.progress.CPanelProcessingMonitor;

/**
 * @author macchan
 */
public class PPDataManager {

	private static final String DATA_DIR = "ppv.data/data";
	private static final String WORK_DIR = "ppv.data/cash";
	private static final String TMP_DIR = "ppv.data/tmp";
	private static final String WORKLIB_DIR = "ppv.lib";

	public static final String BASE_DIR_IN_PROJECT_NAME = "project";

	private static PPDataManager current;

	public static PPDataManager getCurrent() {
		return current;
	}

	private CPanelProcessingMonitor monitor = new CPanelProcessingMonitor();
	private CDirectory baseDir;
	private CDirectory libDir;

	public PPDataManager(CDirectory baseDir) {
		this.baseDir = baseDir;
		current = this;
		this.libDir = getBaseDir().findOrCreateDirectory(WORKLIB_DIR);
	}

	/**
	 * @param monitor
	 *            the monitor to set
	 */
	public void setMonitor(CPanelProcessingMonitor monitor) {
		this.monitor = monitor;
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
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					openProjectSetInternal(monitor, projectSet, load, compile,
							coco);
				} catch (Exception ex) {
					ex.printStackTrace();
					CErrorDialog.show(null, "", ex);
				}
			}
		});
		return projectSet;
	}

	private void openProjectSetInternal(CPanelProcessingMonitor monitor,
			PPProjectSet projectSet, boolean load, boolean compile, boolean coco)
			throws IOException {
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
		monitor.setWorkTitle("Loading Projects");
		monitor.setMax(dirs.size());
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
			monitor.progress(1);
		}

		// load and prone
		if (load) {
			loadAllProjects(projectSet, monitor);
			if (compile) {
				createCompileCash(projectSet, monitor);
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

	public void loadAllProjects(PPProjectSet projectSet,
			ICProgressMonitor monitor) {
		monitor.setWorkTitle("Load Projects...");
		List<PLProject> projects = projectSet.getProjects();
		monitor.setMax(projects.size());
		for (PLProject project : projects) {
			project.load();
			monitor.progress(1);
		}
		projectSet.prune(false);
	}

	public void loadDir(final CDirectory dirFrom, final IPPVLoader loader) {
		monitor.setWorkTitle("Now Loading...");
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				CDirectory dirTo = getDataDir().findOrCreateDirectory(
						dirFrom.getNameByString());
				List<CFile> children = dirFrom.getFileChildren();
				monitor.setMax(children.size());
				for (CFile file : children) {
					try {
						loadZip(file, dirTo, loader);
						monitor.progress(1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				// post processs
				loader.postProcess(dirTo);
				// 読み込み後，　ファイル補完．
				PPProjectSet set = new PPProjectSet(dirTo);
				PPMissingFileManager.doProcess(set, monitor);
			}
		});
	}

	public void loadOneFile(CFile file, CDirectory dirTo, IPPVLoader loader) {
		if ("zip".equals(file.getName().getExtension())) {
			loadZip(file, dirTo, loader);

			// bug fix#7
			// post process
			loader.postProcess(dirTo);
			// 読み込み後，　ファイル補完．
			PPProjectSet set = new PPProjectSet(dirTo);
			PPMissingFileManager.doProcess(set, monitor);
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

	public void createCompileCash(PPProjectSet projectSet,
			ICProgressMonitor monitor) {
		CDirectory workDir = getWorkDir();
		CDirectory libDir = getLibDir();
		CDirectory dir = workDir.findOrCreateDirectory(projectSet.getName());
		projectSet.compileAllProjects(dir, libDir, monitor);
		projectSet.prune(true);
	}

	public void clearCompileCash() {
		monitor.setWorkTitle("Deleting...");
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					clearCashInternal();
				} catch (Exception ex) {
					ex.printStackTrace();
					CErrorDialog.show(null, "", ex);
				}
			}
		});
	}

	private void clearCashInternal() {
		CDirectory workDir = getWorkDir();
		final List<CDirectory> projects = new ArrayList<CDirectory>();
		for (CDirectory projectSet : workDir.getDirectoryChildren()) {
			for (CDirectory project : projectSet.getDirectoryChildren()) {
				projects.add(project);
			}
		}
		monitor.setMax(projects.size());
		for (CDirectory project : projects) {
			project.delete();
			monitor.progress(1);
		}
		workDir.delete();
	}
}
