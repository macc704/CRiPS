package generef.analytics;

import generef.knowledge.RSFailureKnowledge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import util.StringUtil;
import clib.common.time.CTime;

public class FailureKnowledgeListFile {

	private static final String CAMMA = ",";
	private static final String QUOTE = "\"";

	private static final String FILE_PATH = "./";
	private static final String FKLISTFILE_NAME = FILE_PATH
			+ "FailureKnowledgeList.csv";
	private static final String WRITING_POINT_FILE_NAME = FILE_PATH
			+ "FKWritingPoint.csv";
	private static final String FILENAMELIST_NAME = FILE_PATH
			+ "FileNameList.txt";

	private List<String[]> fileNameList;

	public void output(List<FailureKnowledgeRepositoryAnalyzer> analyzers) {

		try {
			File fkListFile = new File(FKLISTFILE_NAME);
			File writingPointListFile = new File(WRITING_POINT_FILE_NAME);

			createFile(fkListFile, writingPointListFile);
			createFileNameList();

			PrintWriter pw = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(fkListFile),
							"sjis")));
			PrintWriter pw2 = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(
							writingPointListFile), "sjis")));

			// header
			printFKListHeader(pw);
			printWritingPointHeader(pw2);

			for (FailureKnowledgeRepositoryAnalyzer analyzer : analyzers) {
				if (analyzer.existRepositoryFile()) {
					printOneRepository(analyzer, pw, pw2);
				}
			}

			pw.close();
			pw2.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createFile(File fkListFile, File writingPointListFile)
			throws IOException {
		if (!fkListFile.exists()) {
			fkListFile.createNewFile();
		}

		if (!writingPointListFile.exists()) {
			writingPointListFile.createNewFile();
		}
	}

	private void createFileNameList() throws IOException {
		fileNameList = new ArrayList<String[]>();

		File file = new File(FILENAMELIST_NAME);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			fileNameList.add(lines);
		}
		reader.close();
	}

	private void printFKListHeader(PrintWriter pw) {
		StringBuffer buf = new StringBuffer();
		buf.append("StudentNumber");
		buf.append(CAMMA);
		buf.append("Lecture");
		buf.append(CAMMA);
		buf.append("FileName");
		buf.append(CAMMA);
		buf.append("ErrorMessage");
		buf.append(CAMMA);
		buf.append("Cause");
		buf.append(CAMMA);
		buf.append("Handle");
		buf.append(CAMMA);
		buf.append("WritingStart");
		buf.append(CAMMA);
		buf.append("WritingFinish");
		buf.append(CAMMA);
		buf.append("WritingTime(sec)");
		buf.append(CAMMA);
		buf.append("Threshold");
		pw.println(buf.toString());
		pw.flush();
	}

	private void printWritingPointHeader(PrintWriter pw) {
		StringBuffer buf = new StringBuffer();
		buf.append("StudentName");
		buf.append(CAMMA);
		buf.append("Lecture");
		buf.append(CAMMA);
		buf.append("FileName");
		buf.append(CAMMA);
		buf.append("WritingTime");
		pw.println(buf.toString());
	}

	private void printOneRepository(
			FailureKnowledgeRepositoryAnalyzer repositoryAnalyzer,
			PrintWriter pw, PrintWriter pw2) {

		String[] line = repositoryAnalyzer.getProject().getName().split("-");
		String student = line[0] + "-" + line[1];

		printFailureKnowledgeList(repositoryAnalyzer, pw, student);
		printWritingPointList(repositoryAnalyzer, pw2, student);
	}

	private void printFailureKnowledgeList(
			FailureKnowledgeRepositoryAnalyzer repositoryAnalyzer,
			PrintWriter pw, String student) {
		for (RSFailureKnowledge knowledge : repositoryAnalyzer.getRepository()
				.getFailureKnowledges()) {
			printOneFailureKnowledge(knowledge, student, pw);
		}
		pw.flush();
	}

	private void printWritingPointList(
			FailureKnowledgeRepositoryAnalyzer repositoryAnalyzer,
			PrintWriter pw, String student) {
		for (FailureKnowledgeAnalyzer analyzer : repositoryAnalyzer
				.getFkAnalyzers()) {
			for (RSFailureKnowledge knowledge : analyzer
					.getWritingPointKnowledges()) {
				printOneWritingPoint(knowledge, student, pw);
			}
		}
		pw.flush();
	}

	private void printOneFailureKnowledge(RSFailureKnowledge knowledge,
			String student, PrintWriter pw) {
		StringBuffer buf = new StringBuffer();
		buf.append(student);
		buf.append(CAMMA);
		buf.append(getReportNumber(knowledge.getCompileError()
				.getNoPathSourceName()));
		buf.append(CAMMA);
		buf.append(knowledge.getCompileError().getNoPathSourceName());
		buf.append(CAMMA);
		buf.append(QUOTE);
		buf.append(knowledge.getCompileError().getMessageParser()
				.getAbstractionMessage());
		buf.append(QUOTE);
		buf.append(CAMMA);
		buf.append(QUOTE);
		buf.append(knowledge.getCause());
		buf.append(QUOTE);
		buf.append(CAMMA);
		buf.append(QUOTE);
		buf.append(knowledge.getHandle());
		buf.append(QUOTE);
		buf.append(CAMMA);
		buf.append(new CTime(knowledge.getWindowOpenTime()));
		buf.append(CAMMA);
		buf.append(new CTime(knowledge.getWindowCloseTime()));
		buf.append(CAMMA);
		buf.append((double) knowledge.getWritingTime() / 1000);
		buf.append(CAMMA);
		buf.append(knowledge.getThreshold());
		pw.println(buf.toString());
	}

	private void printOneWritingPoint(RSFailureKnowledge knowledge,
			String studentNumber, PrintWriter pw) {
		StringBuffer buf = new StringBuffer();
		buf.append(studentNumber);
		buf.append(CAMMA);
		buf.append(getReportNumber(knowledge.getCompileError()
				.getNoPathSourceName()));
		buf.append(CAMMA);
		buf.append(knowledge.getCompileError().getNoPathSourceName());
		buf.append(CAMMA);
		buf.append((double) knowledge.getWritingTime() / 1000);
		pw.println(buf.toString());
	}

	private String getReportNumber(String fileName) {
		fileName = StringUtil.convertToHalfSize(fileName);
		for (int i = 0; i < fileNameList.size(); i++) {
			String[] names = fileNameList.get(i);
			for (int j = 0; j < names.length; j++) {
				if (names[j].toUpperCase().equals(fileName.toUpperCase())) {
					return Integer.toString(i + 2);
				}
			}
		}
		return "";
	}
}
