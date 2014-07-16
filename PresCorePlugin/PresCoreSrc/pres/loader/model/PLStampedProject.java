/*
 * PLStampedProject.java
 * Created on 2011/06/21 by macchan
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import clib.common.compiler.CCompileResult;
import clib.common.compiler.CJavaCompiler;
import clib.common.compiler.CJavaCompilerFactory;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CPath;
import clib.common.system.CEncoding;
import clib.common.time.CTime;
import clib.common.time.ICTimeOrderable;

/**
 * PLStampedProject
 * 
 * XXXXProject <-- workDirForProject
 * 		20000000000  <-- workDir
 * 			ProjectBase  <-- projectBaseDir
 * 			CompileResult.txt <-- result file
 * 		30000000000  <-- workDir
 * 			ProjectBase  <-- projectBaseDir
 * 			CompileResult.txt <-- result file
 */
public class PLStampedProject implements ICTimeOrderable {

	private static final String RESULT_FILE = "CompileResult.dat";
	private static final String PROJECTBASE_DIR_NAME = "ProjectBase";

	private PLProject project;
	private CTime time;
	private CDirectory libDir;
	private CEncoding encoding;

	private CDirectory workDir;

	/**
	 * @param workDirBase
	 * @param libDir
	 */
	public PLStampedProject(PLProject project, CTime time,
			CDirectory workDirForProject, CDirectory libDir, CEncoding encoding) {
		this.project = project;
		this.time = time;
		this.libDir = libDir;
		this.encoding = encoding;

		this.workDir = workDirForProject.findOrCreateDirectory(new CPath(Long
				.toString(time.getAsLong())));
	}

	/* (non-Javadoc)
	 * @see clib.common.time.ICTimeOrderable#getTime()
	 */
	public CTime getTime() {
		return time;
	}

	public CDirectory getLibDir() {
		return libDir;
	}

	public CEncoding getEncoding() {
		return encoding;
	}

	public void initialize(CPath compileFilePath,
			PLStampedProject previousProject) {
		if (!alreadyInitialized()) {
			createAndCopyProjectBaseDirectory(previousProject);
			CCompileResult result = compile(compileFilePath);
			saveCompileResult(result);
		}
	}

	private boolean alreadyInitialized() {
		return workDir != null && workDir.findFile(RESULT_FILE) != null;
	}

	public CCompileResult getCompileResult() {
		return loadCompileResult();
	}

	private CCompileResult loadCompileResult() {
		try {
			CFile file = workDir.findOrCreateFile(RESULT_FILE);
			ObjectInputStream ois = new ObjectInputStream(
					file.openInputStream());
			CCompileResult result = (CCompileResult) ois.readObject();
			ois.close();
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void saveCompileResult(CCompileResult result) {
		try {
			CFile file = workDir.findOrCreateFile(RESULT_FILE);
			ObjectOutputStream oos = new ObjectOutputStream(
					file.openOutputStream());
			oos.writeObject(result);
			oos.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private CCompileResult compile(CPath compileFilePath) {
		CDirectory projectBase = getProjectBaseDirectory();
		CJavaCompiler jc = CJavaCompilerFactory.createCompiler(projectBase);
		CPath srcPath = project.getSrcPath();
		projectBase.findOrCreateDirectory(srcPath);//無ければ作る　bug#03 応急処置
		jc.setSourcepath(srcPath);
		jc.setDestpath(srcPath);
		jc.addClasspathDir(libDir.getAbsolutePath());
		jc.setEncoding(encoding);

		//bug06
		{
			CPath compilePath = compileFilePath.getRelativePath(srcPath);
			jc.addSource(compilePath);
			if (nextRunPath != null) {//bugfix06 次に実行するものもコンパイル
				CPath runPath = nextRunPath.getRelativePath(srcPath);
				if (!runPath.equals(compilePath)) {
					jc.addSource(runPath);
				}
			}
		}
		//全部加える版
		//		{
		//			jc.clearSource();//clearすると，wild card扱いになる!
		//		}

		return jc.doCompile();
	}

	public CDirectory getProjectBaseDirectory() {
		return workDir.findOrCreateDirectory(PROJECTBASE_DIR_NAME);
	}

	private void createAndCopyProjectBaseDirectory(
			PLStampedProject previousProject) {
		CDirectory srcDir = workDir.findOrCreateDirectory(PROJECTBASE_DIR_NAME);
		srcDir.delete();
		srcDir = workDir.findOrCreateDirectory(PROJECTBASE_DIR_NAME);
		copyProjectAtThatTime(srcDir, time);
		// #bug06 class ファイルをコピーすると，コンパイル対象以外のソースが変更されてもリコンパイルされない問題が起きるため
		//		if (previousProject != null) {
		//			copyClassFiles(previousProject);
		//		}
	}

	private boolean isLastStamp = false;
	private CPath nextRunPath = null;

	/**
	 * @param isLastStamp the isLastStamp to set
	 */
	public void setLastStamp(boolean isLastStamp) {
		this.isLastStamp = isLastStamp;
	}

	/**
	 * @param nextRunPath the nextRunPath to set
	 */
	public void setNextRunPath(CPath nextRunPath) {
		this.nextRunPath = nextRunPath;
	}

	private void copyProjectAtThatTime(CDirectory toDir, CTime time) {
		//bug#6 @TODO tmporary lastLogは別扱いで，ログではなく，srcからコピーする
		if (isLastStamp) {
			CDirectory from = project.getProjectBaseDir()
					.findOrCreateDirectory(project.getSrcPath());
			from.copyTo(toDir);
			return;
		}

		for (PLFile file : project.getRootPackage().getFilesRecursively()) {
			if (file.hasStamp(time)) {
				IPLFileStamp stamp = file.getStamp(time);
				CPath parentPath = file.getPath().getParentPath();
				CDirectory parentDir;
				if (parentPath != null) {
					parentDir = toDir.findOrCreateDirectory(parentPath);
				} else {
					parentDir = toDir;
				}
				stamp.getFile()
						.copyTo(parentDir, file.getFileName().toString());
			}
		}
	}

	//	private void copyClassFiles(PLStampedProject previousProject) {
	//		CDirectory pBase = previousProject.getProjectBaseDirectory();
	//		List<CPath> paths = getClassFiles(pBase, pBase);
	//		for (CPath path : paths) {
	//			CFile src = pBase.findFile(path);
	//			src.copyTo(getProjectBaseDirectory(), path);
	//		}
	//	}
	//
	//	private List<CPath> getClassFiles(CDirectory base, CDirectory dir) {
	//		List<CPath> paths = new ArrayList<CPath>();
	//		for (CDirectory child : dir.getDirectoryChildren()) {
	//			paths.addAll(getClassFiles(base, child));
	//		}
	//		for (CFile file : dir.getFileChildren()) {
	//			if ("class".equals(file.getName().getExtension())) {
	//				paths.add(file.getRelativePath(base));
	//			}
	//		}
	//		return paths;
	//	}
}
