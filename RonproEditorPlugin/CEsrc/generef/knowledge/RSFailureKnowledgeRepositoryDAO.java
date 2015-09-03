package generef.knowledge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import util.StringUtil;
import clib.common.compiler.CDiagnostic;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.table.CCSVFileIO;

public class RSFailureKnowledgeRepositoryDAO {

	public RSFailureKnowledgeRepositoryDAO() {
	}

	/***************************************************************************
	 * Save
	 **************************************************************************/

	public void save(RSFailureKnowledgeRepository repository, File file) {
		try {
			saveAllLines(repository, file);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void saveAllLines(RSFailureKnowledgeRepository repository, File file) {

		CFile cFile = (CFile) CFileSystem.convertToCFile(file);
		String[][] values = changeOutputFormat(repository);
		CCSVFileIO.save(values, cFile);

	}

	private String[][] changeOutputFormat(
			RSFailureKnowledgeRepository repository) {
		int size = repository.getFailureKnowledges().size();
		String[][] tokens = new String[size][8];

		for (int i = 0; i < size; i++) {
			RSFailureKnowledge knowledge = repository.getFailureKnowledges()
					.get(i);
			tokens[i] = saveOneLine(knowledge);
		}

		return tokens;
	}

	private String[] saveOneLine(RSFailureKnowledge knowledge) {
		String[] lines = new String[9];

		// Point Date
		lines[0] = Long.toString(knowledge.getWritingPointTime());

		// Time
		lines[1] = Long.toString(knowledge.getWindowOpenTime());
		lines[2] = Long.toString(knowledge.getWindowCloseTime());

		// Error Data
		CDiagnostic error = knowledge.getCompileError();
		lines[3] = StringUtil.convertToSign(error.getMessage());

		// knowledge
		lines[4] = StringUtil.convertToSign(knowledge.getCause());
		lines[5] = StringUtil.convertToSign(knowledge.getHandle());

		// file
		lines[6] = knowledge.getUnFixedFilePath();
		lines[7] = knowledge.getFixedFilePath();

		// Threshold
		lines[8] = String.valueOf(knowledge.getThreshold());

		return lines;
	}

	/***************************************************************************
	 * Load
	 **************************************************************************/

	public RSFailureKnowledgeRepository load(File file) {
		try {
			if (!file.exists()) {
				return new RSFailureKnowledgeRepository();
			}
			return loadAllLines(file);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private RSFailureKnowledgeRepository loadAllLines(File file)
			throws Exception {

		CFile cFile = (CFile) CFileSystem.convertToCFile(file);
		String[][] values = CCSVFileIO.load(cFile);
		RSFailureKnowledgeRepository repository = changeInputFormat(values);
		return repository;

	}

	private RSFailureKnowledgeRepository changeInputFormat(String[][] values) {
		RSFailureKnowledgeRepository repository = new RSFailureKnowledgeRepository();
		List<RSFailureKnowledge> knowledges = new ArrayList<RSFailureKnowledge>();

		for (int i = 0; i < values.length; i++) {
			RSFailureKnowledge knowledge = loadOneLine(values[i]);
			if (knowledges.size() == 0) {
				knowledges.add(knowledge);
			} else {
				if (knowledges.get(0).getWritingPointTime() == knowledge
						.getWritingPointTime()) {
					knowledges.add(knowledge);
				} else {
					repository.addAll(knowledges);
					knowledges = new ArrayList<RSFailureKnowledge>();
					knowledges.add(knowledge);
				}
			}
		}

		repository.addAll(knowledges);

		return repository;
	}

	private RSFailureKnowledge loadOneLine(String[] lines) {
		// Writing Point Date
		long writingPointTime = Long.parseLong(lines[0]);

		// Time
		long openWindowTimeMillis = Long.parseLong(lines[1]);
		long closeWindowTimeMillis = Long.parseLong(lines[2]);

		// Error Data
		String message = StringUtil.convertToNonSign(lines[3]);
		CDiagnostic error = new CDiagnostic(message);

		// Knowledge
		String cause = StringUtil.convertToNonSign(lines[4]);
		String handle = StringUtil.convertToNonSign(lines[5]);

		// File
		String unFixedFilePath = lines[6];
		String fixedFilePath = lines[7];

		// Threshold
		int threshold = 0;
		if (lines.length > 8) {
			threshold = Integer.parseInt(lines[8]);
		}

		return new RSFailureKnowledge(error, cause, handle, unFixedFilePath,
				fixedFilePath, openWindowTimeMillis, closeWindowTimeMillis,
				writingPointTime, threshold);
	}

}
