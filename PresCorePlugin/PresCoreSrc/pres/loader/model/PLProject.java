/*
 * PresModel.java
 * Created on Jul 6, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.model;

import java.util.List;

import pres.core.PRRecordingProject;
import pres.loader.logmodel.PLFileLog;
import pres.loader.logmodel.PLLog;
import pres.loader.logmodel.PLLogReader;
import pres.loader.utils.PLLogSelecters;
import clib.common.compiler.CCompileResult;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CPath;
import clib.common.system.CEncoding;
import clib.common.system.CEncodingDetector;
import clib.common.time.CTime;
import clib.common.time.CTimeOrderedList;
import clib.common.time.ICTimeOrderable;

/**
 * class PPProject
 * 
 * @author macchan
 */
public class PLProject {

	public static void checkAppropreateDir(CDirectory dir) {
		if (dir == null) {
			throw new RuntimeException("dir is null");
		}

		if (dir.findDirectory(PRRecordingProject.RECORDING_DIRNAME_DEFAULT) == null) {
			throw new RuntimeException(".pres2 dir is null");
		}

		if (dir.findDirectory(PRRecordingProject.RECORDING_DIRNAME_DEFAULT)
				.findFile(PRRecordingProject.RECORDING_FILENAME_DEFAULT) == null) {
			throw new RuntimeException("pres2.log is null");
		}
	}

	private String name;
	private CDirectory projectBaseDir;
	private CPath srcPath;

	private PLPackage rootPackage;

	private CTimeOrderedList<PLStampedProject> stampedProjects = new CTimeOrderedList<PLStampedProject>();

	//private CTimeOrderedList<PLCompileResult> compileResults = new CTimeOrderedList<PLCompileResult>();

	private boolean compiled = false;
	private boolean loaded = false;

	public PLProject(String name, CDirectory projectBaseDir, CPath srcPath) {
		checkAppropreateDir(projectBaseDir);
		this.name = name;
		this.projectBaseDir = projectBaseDir;
		this.srcPath = srcPath;
		initialize();
	}

	private void initialize() {
		CDirectory presBaseDir = projectBaseDir
				.findOrCreateDirectory(PRRecordingProject.RECORDING_DIRNAME_DEFAULT);
		this.rootPackage = new PLPackage(this, presBaseDir, new CPath(""));
	}

	public void load() {
		if (loaded) {
			return;
		}
		rootPackage.load();
		CDirectory presBaseDir = projectBaseDir
				.findOrCreateDirectory(PRRecordingProject.RECORDING_DIRNAME_DEFAULT);
		List<PLLog> logs = PLLogReader.readPresLogFile(presBaseDir.findFile(
				PRRecordingProject.RECORDING_FILENAME_DEFAULT).toJavaFile());
		for (PLLog log : logs) {
			rootPackage.putLog(log);
		}
		loaded = true;
	}

	//メモリ対策
	public void unLoad() {
		if (!loaded) {
			return;
		}
		rootPackage.unload();
		loaded = false;
	}

	/**
	 * @return the loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}

	public String getName() {
		return name;
	}

	public CPath getSrcPath() {
		return srcPath;
	}

	public CDirectory getProjectBaseDir() {
		return projectBaseDir;
	}

	public PLPackage getRootPackage() {
		return rootPackage;
	}

	public List<PLFile> getFiles() {
		return rootPackage.getFilesRecursively();
	}

	public PLFile getFile(CPath path) {
		for (PLFile file : getFiles()) {
			if (file.getPath().equals(path)) {
				return file;
			}
		}
		//System.err.println("error:" + path + " : ");
		return null;
	}

	public void compileAllTime(CDirectory workDirForProject, CDirectory libDir) {
		PLStampedProject previousProject = null;

		CTimeOrderedList<PLLog> logs = rootPackage.getOrderedLogs().select(
				PLLogSelecters.COMPILE);

		PLLog lastLog = null;
		if (logs.size() > 1) {
			lastLog = logs.get(logs.size() - 1);
		}

		for (PLLog log : logs) {
			CTime time = log.getTime();
			PLStampedProject stampedProject = new PLStampedProject(this, time,
					workDirForProject, libDir, getFileEncoding());
			if (log == lastLog) { //bug#6 @TODO tmporary lastLogは別扱いで，ログではなく，srcからコピーする
				stampedProject.setLastStamp(true);
			}
			PLLog runLog = rootPackage.getOrderedLogs().searchElementAfter(
					log.getTime(), PLLogSelecters.RUN);
			if (runLog != null) {
				stampedProject.setNextRunPath(((PLFileLog) runLog).getPath());
			}
			stampedProject.initialize(((PLFileLog) log).getPath(),
					previousProject);//compile 
			stampedProjects.add(stampedProject);
			previousProject = stampedProject;
		}

		//		for (PLLog log : rootPackage.getLogs()) {
		//			if (log instanceof PLFileLog
		//					&& PRCommandLog.SubType.COMPILE.toString().equals(
		//							log.getSubType())) {
		//				CTime time = log.getTime();
		//				PLStampedProject stampedProject = new PLStampedProject(this,
		//						time, workDirForProject, libDir, getFileEncoding());
		//				stampedProject.initialize(((PLFileLog) log).getPath(),
		//						previousProject);//compile 
		//				stampedProjects.add(stampedProject);
		//				previousProject = stampedProject;
		//			}
		//		}
		this.compiled = true;
	}

	/**
	 * @return the compiled
	 */
	public boolean isCompiled() {
		return compiled;
	}

	public boolean isReady(boolean requireCompile) {
		try {
			return isValid() && (isCompiled() || !requireCompile);
		} catch (Exception ex) {
			return false;
		}
	}

	public boolean isValid() {
		try {
			return getRootPackage().hasRange();
		} catch (Exception ex) {
			return false;
		}
	}

	private CEncoding fileEncoding = null;

	public CEncoding getFileEncoding() {
		if (fileEncoding == null) {
			fileEncoding = createFileEncoding();
		}
		return fileEncoding;
	}

	private CEncoding createFileEncoding() {
		int utfCount = 0;
		List<PLFile> files = getRootPackage().getFilesRecursively();
		for (PLFile file : files) {
			try {
				CFile cFile = file.getStamps().getLast().getFile();
				if (CEncoding.UTF8 == CEncodingDetector.detect(cFile
						.toJavaFile())) {
					utfCount++;
				}
			} catch (Exception ex) {
				continue;
			}
		}
		if (utfCount > 0) {//TODO とりあえず
			return CEncoding.UTF8;
		}
		return CEncoding.JISAutoDetect;
	}

	public CCompileResult getCompileResult(CTime time) {
		PLStampedProject stampedProject = stampedProjects.searchElement(time);
		if (stampedProject == null) {
			return null;
		}
		CCompileResult result = stampedProject.getCompileResult();
		return result;
	}

	public PLStampedProject getStampedProject(CTime time) {
		return stampedProjects.searchElementBefore(time);
	}

}

class PLCompileResult implements ICTimeOrderable {

	private CTime time;
	private CCompileResult result;

	/**
	 * @param time
	 * @param result
	 */
	public PLCompileResult(CTime time, CCompileResult result) {
		super();
		this.time = time;
		this.result = result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.utils.IPLTimeScaled#getTime()
	 */
	public CTime getTime() {
		return time;
	}

	/**
	 * @return the result
	 */
	public CCompileResult getResult() {
		return result;
	}
}
