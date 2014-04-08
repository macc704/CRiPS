package generef.compileerror;

import java.util.ArrayList;
import java.util.List;

import tea.analytics.model.TCompileErrorHistory;
import clib.common.compiler.CCompileResult;
import clib.common.compiler.CDiagnostic;

public class RSCompileHistoryList {

	private List<RSCompileHistory> historys = new ArrayList<RSCompileHistory>();
	private List<String> sourceNames;

	public void addHistory(RSCompileHistory history) {
		historys.add(history);
	}

	public void addCompileResult(RSErrorMessage message) {
		CCompileResult result = message.getCompileResult();
		this.sourceNames = message.getCompileFileNames();
		if (result.isSuccess()) {
			// どのファイルがコンパイルされたのかをエラーメッセージから取得
			// コンパイルされたファイルに対してresultをaddする
			for (RSCompileHistory history : historys) {
				if (message.getCompileFileNames().contains(
						history.getSourceFileName())) {
					history.addCompileResult(result);
				}
			}

		} else {
			for (RSCompileHistory history : historys) {
				String sourceName = history.getSourceFileName();
				if (sourceNames.contains(sourceName)) {
					CCompileResult compileResult = createFileCompileResult(
							result, sourceName);
					history.addCompileResult(compileResult);
				}
			}
		}
	}

	/**
	 * 各ソースコードについてのコンパイル結果を作成する
	 * 
	 * @param result
	 * @param sourceName
	 * @return
	 */
	private CCompileResult createFileCompileResult(CCompileResult result,
			String sourceName) {
		List<CDiagnostic> errors = new ArrayList<CDiagnostic>();
		for (CDiagnostic error : result.getDiagnostics()) {
			if (sourceName.equals(error.getNoPathSourceName())) {
				errors.add(error);
			}
		}
		return new CCompileResult(errors.size() <= 0, errors);
	}

	public List<TCompileErrorHistory> getFixedCompileErrorHistory() {
		List<TCompileErrorHistory> fixedHistorys = new ArrayList<TCompileErrorHistory>();
		for (RSCompileHistory history : historys) {
			// 応急処置
			if (history.isFixed()
					&& sourceNames.contains(history.getSourceFileName())) {
				fixedHistorys.addAll(history.getFixedErrorHistorys());
			}
		}
		return fixedHistorys;
	}

	public RSCompileHistory getHistory(String sourceName) {
		for (RSCompileHistory history : historys) {
			if (sourceName.equals(history.getSourceFileName())) {
				return history;
			}
		}
		return null;
	}

	public List<RSCompileHistory> getHistorys(List<String> sourceNames) {
		List<RSCompileHistory> historys = new ArrayList<RSCompileHistory>();

		for (RSCompileHistory history : this.historys) {
			if (sourceNames.contains(history.getSourceFileName())) {
				historys.add(history);
			}
		}

		return historys;
	}

	public RSCompileHistory getRSHistory(TCompileErrorHistory tHistory) {
		for (RSCompileHistory history : historys) {
			if (history.getFixedErrorHistorys().contains(tHistory)) {
				return history;
			}
		}
		return null;
	}

	public void setCurrentCompileFiles(String projectPath,
			List<String> sourceNames) {

		for (RSCompileHistory history : historys) {
			if (sourceNames.contains(history.getSourceFileName())) {
				history.setCurrentCompileFile(projectPath);
			}
		}

	}

	public boolean isFixed() {
		if (getFixedCompileErrorHistory().size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 指定したファイル名のヒストリーが作られているか確認する
	 * 
	 * @param sourceName
	 * @return
	 */
	public boolean containsHistory(String sourceName) {
		for (RSCompileHistory history : historys) {
			if (sourceName.equals(history.getSourceFileName())) {
				return true;
			}
		}
		return false;
	}

	public void clearCompileSuccessHistorys(CCompileResult result,
			List<String> compileSourceNames) {
		if (result != null && result.isSuccess()) {
			for (RSCompileHistory history : historys) {
				if (compileSourceNames.contains(history.getSourceFileName())) {
					history.clear();
				}
			}
		}
	}

}
