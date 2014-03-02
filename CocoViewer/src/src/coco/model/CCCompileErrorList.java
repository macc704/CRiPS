package src.coco.model;

import java.util.ArrayList;

public class CCCompileErrorList {
	private String message = "null message";
	private int rare = 0;
	private ArrayList<CCCompileError> errors = new ArrayList<CCCompileError>();

	public CCCompileErrorList(int rare, String message) {
		this.rare = rare;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public int getRare() {
		return rare;
	}

	public void addError(CCCompileError error) {
		// CompileError.csv のコンパイルエラーの発生日時の順序が正しくないことがあるので
		// リスト格納時に正しい発生日時順になるよう処理
		// errors.add(error);

		if (errors.size() == 0) {
			errors.add(error);
		} else {
			for (int i = errors.size(); i > 0; i--) {
				if (errors.get(i - 1).getBeginTime() < error.getBeginTime()) {
					errors.add(i, error);
					break;
				}

				if (i == 1) {
					errors.add(0, error);
				}
			}
		}
	}

	public ArrayList<CCCompileError> getErrors() {
		return errors;
	}
}