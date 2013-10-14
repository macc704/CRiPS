/*
 * PPPackage.java
 * Created on 2011/06/05
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pres.loader.logmodel.PLFileLog;
import pres.loader.logmodel.PLLog;
import pres.loader.utils.PLLogSelecters;
import pres.loader.utils.PLTimeListUtils;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;
import clib.common.time.CTime;

/**
 * @author macchan
 */
public class PLPackage extends PLAbstractUnit {

	private List<PLPackage> subPackages = new ArrayList<PLPackage>();
	//private List<PLFile> files = new ArrayList<PLFile>();
	private Map<CPath, PLFile> filesMap = new LinkedHashMap<CPath, PLFile>();
	private boolean loaded = false;

	public PLPackage(PLProject project, CDirectory dir, CPath path) {
		super(project, dir, path);
	}

	public void load() {
		if (loaded) {
			return;
		}
		List<CDirectory> children = getDir().getDirectoryChildren();
		for (CDirectory child : children) {
			if (child.getName().getExtension().equals("java")) {// file
				addFile(new PLFile(getProject(), child, getPath().appendedPath(
						child.getNameByString())));
			} else {// package
				PLPackage pack = new PLPackage(getProject(), child, getPath()
						.appendedPath(child.getNameByString()));
				pack.load();
				subPackages.add(pack);
			}
		}
		loaded = true;
	}

	public void unload() {
		if (!loaded) {
			return;
		}
		super.clearLog();
		subPackages = new ArrayList<PLPackage>();
		filesMap = new LinkedHashMap<CPath, PLFile>();
		loaded = false;
	}

	private void addFile(PLFile file) {
		filesMap.put(file.getPath(), file);
	}

	public String getName() {
		if (getPath().toPathStrings().size() == 0) {
			return getProject().getName() + "(Root)";
		}
		return super.getName();
	}

	protected void putLog(PLLog log) {
		super.putLog(log);
		if (log instanceof PLFileLog) {
			CPath logPath = ((PLFileLog) log).getPath();
			for (PLPackage subPackage : subPackages) {
				if (logPath.toString().startsWith(
						subPackage.getPath().toString())) {
					subPackage.putLog(log);
				}
			}

			PLFile file = filesMap.get(logPath);
			if (file != null) {
				file.putLog(log);
			} else {//bug#10 応急処置
				for (PLFile otherCaseFile : getFiles()) {
					if (otherCaseFile.getPath().toString().toLowerCase()
							.equals(logPath.toString().toLowerCase())) {
						PLFile newFile = new PLFile(getProject(),
								otherCaseFile.getDir(), logPath);
						addFile(newFile);
					}
				}
			}

			//bug#10 応急処置
			//			for (PLFile file : getFiles()) {
			//				CPath filePath = file.getPath();
			//				if (logPath.equals(filePath)) {
			//					file.putLog(log);
			//				}
			//			}
		}
	}

	public List<IPLUnit> getChildren() {
		List<IPLUnit> children = new ArrayList<IPLUnit>();
		children.addAll(getSubPackages());
		children.addAll(getFiles());
		return children;
	}

	public List<PLPackage> getSubPackages() {
		return new ArrayList<PLPackage>(subPackages);
	}

	public List<PLFile> getFiles() {
		//return new ArrayList<PLFile>(files);
		return new ArrayList<PLFile>(filesMap.values());
	}

	public List<PLFile> getFilesRecursively() {
		List<PLFile> files = getFiles();
		for (PLPackage pack : subPackages) {
			files.addAll(pack.getFilesRecursively());
		}
		return files;
	}

	/* (non-Javadoc)
	* @see pres.loader.model.IPLFileProvider#getFile(clib.common.time.CTime)
	*/
	public PLFile getFile(CTime time) {
		if (getChildren().size() <= 0) {
			return null;
		}
		if (getChildren().size() == 1) {//short cut
			return getChildren().get(0).getFile(time);
		}

		//PLLog log = searchLog(time, PLLogSelecters.ALL);
		PLLog log = searchLog(time, PLLogSelecters.FILELOG);//2012/06/18 bug#2 ProjectLogが来るとエラー
		if (log == null) {
			return null;
		}

		//2012/06/18 bug#2 ProjectLogが来るとエラー
		if (!(log instanceof PLFileLog)) {
			return null;
		}

		CPath path = ((PLFileLog) log).getPath();
		PLFile file = getProject().getFile(path);
		if (file == null) {
			//throw new RuntimeException();//@TODO bug#01 これが起こるデータがある
			return null;
		}
		return file;
	}

	//	public String getSource(CTime time) {
	//		if (getChildren().size() <= 0) {
	//			return null;
	//		}
	//		if (getChildren().size() == 1) {//short cut
	//			return getChildren().get(0).getFile(time);
	//		}
	//
	//		//PLLog log = searchLog(time, PLLogSelecters.SAVE);
	//		PLLog log = searchLog(time, PLLogSelecters.ALL);
	//		if (log == null) {
	//			return "";
	//		}
	//		CPath path = ((PLFileLog) log).getPath();
	//		PLFile file = getProject().getFile(path);
	//		if (file == null) {
	//			throw new RuntimeException();
	//		}
	//		return file.getFile(time);
	//	}

	public List<CTime> getSavePoints() {
		List<CTime> points = new ArrayList<CTime>();
		for (IPLUnit unit : getChildren()) {
			points = PLTimeListUtils.marge(points, unit.getSavePoints());
		}
		return points;
		//   List<CTime> points = new ArrayList<CTime>();
		//		for (PLLog log : logs.getElements()) {
		//			if ("SAVE".equals(log.getSubType())) {
		//				points.add(log.getTime());
		//			}
		//		}
		// return points;
	}

	public int getMaxLineCount() {
		int max = 0;
		for (IPLUnit unit : getChildren()) {
			max += unit.getMaxLineCount();
		}
		return max;
	}

	public int getLineCount(CTime time) {
		int count = 0;
		for (IPLUnit unit : getChildren()) {
			count += unit.getLineCount(time);
		}
		return count;
	}

//	/* (non-Javadoc)
//	 * @see pres.loader.model.IPLUnit#hasSource(java.lang.String)
//	 */
//	public boolean hasSource(String sourceName) {
//		for (IPLUnit unit : getChildren()) {
//			if (unit.hasSource(sourceName)) {
//				return true;
//			}
//		}
//		return false;
//	}

}
