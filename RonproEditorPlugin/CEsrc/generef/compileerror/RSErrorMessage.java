package generef.compileerror;

import java.util.ArrayList;
import java.util.List;

import clib.common.compiler.CCompileResult;
import clib.common.compiler.CDiagnostic;

public class RSErrorMessage {

	private List<String> compileFileNames = new ArrayList<String>();
	private CCompileResult result;
	private String message;
	private boolean path;

	public RSErrorMessage(String message) {
		this.message = parseMessage(message);
		this.compileFileNames = setCompileFileNames(message);
		this.path = check2Path(message);
		this.result = setCompileResult();
	}

	private String parseMessage(String message) {
		StringBuffer buf = new StringBuffer();

		String[] line = message.split("\n");

		for (int i = 0; i < line.length; i++) {
			if (!line[i].startsWith("[")) {
				buf.append(line[i] + "\n");
			}
		}

		return buf.toString();
	}

	private List<String> setCompileFileNames(String message) {
		List<String> list = new ArrayList<String>();

		String[] line = message.split("\n");

		for (int i = 0; i < line.length; i++) {
			if (line[i].contains("構文解析開始")) {
				int beginIndex = line[i].lastIndexOf("[") + 1;
				int endIndex = line[i].indexOf("]");
				String fileName = line[i].substring(beginIndex, endIndex);
				int pathIndex = fileName.lastIndexOf("\\") + 1;
				if (pathIndex == -1) {
					list.add(fileName);
				} else {
					list.add(fileName.substring(pathIndex));
				}

			}
		}

		return list;
	}

	private boolean check2Path(String message) {

		String[] line = message.split("\n");

		for (int i = 0; i < line.length; i++) {

			if (line[i].endsWith("構文解析完了]")) {
				if (line[i + 1].startsWith("[合計")) {
					return false;
				} else {
					return true;
				}
			}

		}

		return true;
	}

	private CCompileResult setCompileResult() {
		List<CDiagnostic> compileErrors = createDiagnostics();
		boolean success = compileErrors.size() <= 0;

		return new CCompileResult(success, compileErrors);
	}

	private List<CDiagnostic> createDiagnostics() {

		List<CDiagnostic> compileErrors = new ArrayList<CDiagnostic>();
		String[] line = message.split("\n");
		StringBuffer buf = new StringBuffer();

		for (int i = 1; i < line.length; i++) {

			if (line[i].contains(".java:") || i == line.length - 1) {
				buf.append(line[i - 1]);
				compileErrors.add(new CDiagnostic(buf.toString()));
				buf.delete(0, buf.length()); // bufの中身をリセット
			} else {
				buf.append(line[i - 1] + "\n");
			}

		}

		return compileErrors;
	}

	public boolean is2Path() {
		return path;
	}

	public String getErrorMessage() {
		return message;
	}

	public CCompileResult getCompileResult() {
		return result;
	}

	public List<String> getCompileFileNames() {
		return compileFileNames;
	}

}
