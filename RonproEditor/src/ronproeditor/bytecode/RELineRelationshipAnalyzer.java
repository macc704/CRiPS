/*
 * RELineRelationshipAnalyzer.java
 * Created on 2007/12/14 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.bytecode;

import java.io.File;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import ronproeditor.REApplication;
import ronproeditor.helpers.FileSystemUtil;

/**
 * RELineRelationshipAnalyzer
 */
public class RELineRelationshipAnalyzer {

	private File source;
	private File jasmin;

	private Map<Integer, Integer> jasminAnalyzed = new LinkedHashMap<Integer, Integer>();
	private Map<Integer, Integer> lineRelationship = new LinkedHashMap<Integer, Integer>();
	private String jasminedSource;

	RELineRelationshipAnalyzer(File source, File jasmin) {
		this.source = source;
		this.jasmin = jasmin;
		analyze();
	}

	private void analyze() {
		analyzeJasmin();
		createJasminedSource();
	}

	private void analyzeJasmin() {
		Scanner scanner = new Scanner(FileSystemUtil.load(jasmin,
				REApplication.SRC_ENCODING));
		int linecount = 0;
		while (scanner.hasNext()) {
			linecount++;
			String line = scanner.nextLine();
			// if (line.startsWith(".line")) {
			if (line.indexOf(".line") != -1) {
				String[] tokens = line.split(" ");
				jasminAnalyzed.put(linecount,
						Integer.parseInt(tokens[tokens.length - 1]));
			}
		}
		scanner.close();
	}

	private int sLine = 1;
	private int jLine = 1;

	private void createJasminedSource() {
		Scanner scanner = new Scanner(FileSystemUtil.load(source,
				REApplication.SRC_ENCODING));
		StringWriter writer = new StringWriter();

		sLine = 1;
		jLine = 1;
		for (int jLineNext : jasminAnalyzed.keySet()) {
			int sLineNext = jasminAnalyzed.get(jLineNext);
			int dSLine = sLineNext - sLine;
			int dJLine = jLineNext - jLine;
			int dSJ = dJLine - dSLine;

			// Jasminのために空白行を作る分
			for (int i = 0; i < dSJ; i++) {
				writer.write(FileSystemUtil.CR);
				jLine++;
			}

			// ソース書いていない分
			for (int i = 0; i < dSLine; i++) {
				flushLine(scanner, writer);
			}

			// 当該行
			flushLine(scanner, writer);

			// sLine = sLineNext + 1;
			// jLine = jLineNext + 1;
		}

		while (scanner.hasNext()) {
			flushLine(scanner, writer);
		}

		jasminedSource = writer.toString();
	}

	private void flushLine(Scanner scanner, StringWriter writer) {
		lineRelationship.put(jLine, sLine);
		String line = scanner.nextLine();
		writer.write(line);
		writer.write(FileSystemUtil.CR);
		jLine++;
		sLine++;
	}

	public String getSource() {
		return jasminedSource;
	}

	public Map<Integer, Integer> getLineRelationShip() {
		return lineRelationship;
	}
}
