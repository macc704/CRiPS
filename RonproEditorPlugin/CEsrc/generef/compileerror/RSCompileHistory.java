package generef.compileerror;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tea.analytics.CompileErrorAnalyzer;
import tea.analytics.model.TCompileErrorHistory;
import tea.analytics.model.TCompilePoint;
import clib.common.compiler.CCompileResult;
import clib.common.time.CTime;

public class RSCompileHistory {

	private String projectName = "";
	private String sourceFileName = "";

	private File prevCompileFile = null;
	private File currentCompileFile = null;

	private List<TCompilePoint> compilePoints = new ArrayList<TCompilePoint>();
	private List<TCompileErrorHistory> historys = new ArrayList<TCompileErrorHistory>();
	private List<TCompileErrorHistory> fixedErrorHistorys = new ArrayList<TCompileErrorHistory>();

	public RSCompileHistory(String projectName, String sourceFileName) {
		this.projectName = projectName;
		this.sourceFileName = sourceFileName;
	}

	public void addCompileResult(CCompileResult compileResult) {
		compilePoints.add(createCompilePoint(compileResult));
		createHistorys();
		createFixedErrorHistorys();
		// removeFixedCompilePoint();
		// if (compileResult.isSuccess()) {
		// compilePoints.clear();
		// }
	}

	private TCompilePoint createCompilePoint(CCompileResult compileResult) {
		CTime time = new CTime(System.currentTimeMillis());
		TCompilePoint compilePoint = new TCompilePoint(time, compileResult);
		int size = compilePoints.size();
		if (size > 0) {
			compilePoint.setPrevious(compilePoints.get(size - 1));
		}
		return compilePoint;
	}

	private void createHistorys() {
		CompileErrorAnalyzer analyzer = new CompileErrorAnalyzer();
		analyzer.analyze(compilePoints);
		historys = analyzer.getHistories();
	}

	public void createFixedErrorHistorys() {

		List<TCompileErrorHistory> fixedErrorHistorys = new ArrayList<TCompileErrorHistory>();
		for (TCompileErrorHistory history : this.historys) {
			if (history.isFixed() && history.getEnd() == getLastCompilePoint()) {
				fixedErrorHistorys.add(history);
			}
		}
		this.fixedErrorHistorys = fixedErrorHistorys;
	}

	/**
	 * compilePointで発生したエラーが全て修正されていたらcompilePointsから削除します
	 */
	// private void removeFixedCompilePoint() {
	//
	// // 削除するコンパイルポイントを抽出
	// List<TCompilePoint> removeCompilePoints = new ArrayList<TCompilePoint>();
	// for (TCompilePoint compilePoint : this.compilePoints) {
	// if (checkFixedCompilePoint(compilePoint)) {
	// removeCompilePoints.add(compilePoint);
	// }
	// }
	// compilePoints.removeAll(removeCompilePoints);
	// }

	// private boolean checkFixedCompilePoint(TCompilePoint compilePoint) {
	//
	// for (CDiagnostic compileError : compilePoint.getCompileResult()
	// .getDiagnostics()) {
	// if (!checkContainsCompileError(compileError)) {
	// return false;
	// }
	// }
	//
	// return true;
	// }
	//
	// private boolean checkContainsCompileError(CDiagnostic compileError) {
	// for (TCompileErrorHistory history : this.fixedErrorHistorys) {
	// if (history.containsCompileError(compileError)) {
	// return true;
	// }
	// }
	// return false;
	// }

	public boolean isFixed() {
		if (fixedErrorHistorys.size() > 0) {
			return true;
		}
		return false;
	}

	public List<TCompileErrorHistory> getFixedErrorHistorys() {
		return fixedErrorHistorys;
	}

	public TCompilePoint getLastCompilePoint() {
		if (compilePoints.size() <= 0) {
			return null;
		}
		return compilePoints.get(compilePoints.size() - 1);
	}

	public String getProjectName() {
		return projectName;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public File getPrevCompileFile() {
		return prevCompileFile;
	}

	public File getCurrentCompileFile() {
		return currentCompileFile;
	}

	public void setCurrentCompileFile(String projectPath) {
		this.prevCompileFile = this.currentCompileFile;
		String path = projectPath + "/.pres2/" + sourceFileName + "/";
		File[] files = new File(path).listFiles();
		if (files.length > 0) {
			this.currentCompileFile = getLastModifiedFile(files);
		}
	}

	/**
	 * 最後に作成されたソースコードファイルを返します
	 * 
	 * @param files
	 * @return
	 */
	private File getLastModifiedFile(File[] files) {
		File lastModifiedFile = files[0];
		for (int i = 1; i < files.length; i++) {
			if (lastModifiedFile.lastModified() < files[i].lastModified()) {
				lastModifiedFile = files[i];
			}
		}
		return lastModifiedFile;
	}

	public void clear() {
		this.compilePoints.clear();
		this.historys.clear();
		this.fixedErrorHistorys.clear();
	}

}
