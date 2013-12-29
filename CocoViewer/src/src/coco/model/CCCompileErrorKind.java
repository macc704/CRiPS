package src.coco.model;

import java.util.ArrayList;

public class CCCompileErrorKind {

	private String message = "null message";
	private int rare = 0;
	private ArrayList<CCCompileError> errors = new ArrayList<CCCompileError>();

	public CCCompileErrorKind(int rare, String message) {
		this.rare = rare;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public int getRare() {
		return rare;
	}

	protected void addError(CCCompileError error) {
		// CompileError.csv のコンパイルエラーの発生日時の順序が正しくないことがあるので
		// リスト格納時に正しい発生日時順になるよう処理

		if (errors.size() == 0) {
			errors.add(error);
		} else {
			for (int i = errors.size(); i > 0; i--) {
				if (errors.get(i - 1).getBeginTime() < error.getBeginTime()) {
					errors.add(i, error);
					break;
				}
			}
		}
	}

	public ArrayList<CCCompileError> getErrors() {
		return errors;
	}
}