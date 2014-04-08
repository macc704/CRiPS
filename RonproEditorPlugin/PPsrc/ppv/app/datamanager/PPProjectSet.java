/*
 * PLProjectManager.java
 * Created on 2011/06/08 by macchan
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University
 */
package ppv.app.datamanager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pres.loader.model.PLPackage;
import pres.loader.model.PLProject;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.time.CTime;
import clib.common.time.CTimeRange;
import clib.common.utils.ICProgressMonitor;

/**
 * PLProjectManager
 */
public class PPProjectSet {

	private static final String SRCDIR_TXT = "srcdir.txt";

	private static String getSrcDirName(CDirectory dir) {
		CFile file = dir.findOrCreateFile(SRCDIR_TXT);
		return file.loadText();
	}

	private static void setSrcDirName(CDirectory dir, String name) {
		CFile file = dir.findOrCreateFile(SRCDIR_TXT);
		file.saveText(name);
	}

	private CDirectory dir;
	private String name = "";
	private String src = "";

	private List<PLProject> projects = new ArrayList<PLProject>();

	/**
	 * Constructor
	 */
	public PPProjectSet(CDirectory dir) {
		this.dir = dir;
		this.name = dir.getNameByString();
		this.src = getSrcDirName(dir);
	}

	public void add(PLProject project) {
		if (project == null) {
			throw new RuntimeException("project is null.");
		}
		projects.add(project);
	}

	public List<PLProject> getProjects() {
		return projects;
	}

	public CTimeRange getRange() {
		CTime start = null;
		CTime end = null;
		for (PLProject project : projects) {
			PLPackage pack = project.getRootPackage();
			if (pack.hasRange()) {
				if (start == null || pack.getRange().getStart().before(start)) {
					start = pack.getRange().getStart();
				}
				if (end == null || pack.getRange().getEnd().after(end)) {
					end = pack.getRange().getEnd();
				}
			}
		}
		if (start == null || end == null) {
			// throw new RuntimeException();
			return new CTimeRange(0, 1000);
		}
		return new CTimeRange(start, end);
	}

	/**
	 * @param manager
	 * @param workDir
	 * @param libDir
	 */
	public void compileAllProjects(CDirectory workDirBase, CDirectory libDir,
			ICProgressMonitor monitor) {
		monitor.setWorkTitle("Collecting Compile Result...");
		monitor.setMax(projects.size());
		for (PLProject project : projects) {
			try {
				CDirectory workDirForProject = workDirBase
						.findOrCreateDirectory(project.getName());
				project.compileAllTime(workDirForProject, libDir);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				monitor.progress(1);
			}
		}
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getSrcDirName() {
		return src;
	}

	public void setSrcDirName(String name) {
		setSrcDirName(dir, name);
	}

	/**
	 * @return the dir
	 */
	public CDirectory getDir() {
		return dir;
	}

	public void prune(boolean requireCompile) {
		for (Iterator<PLProject> i = projects.iterator(); i.hasNext();) {
			PLProject pj = i.next();
			if (!pj.isReady(requireCompile)) {
				System.err
						.println("prune() " + pj.getName() + " is not valid.");
				i.remove();
			}
		}
	}
}
