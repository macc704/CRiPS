package tea.analytics;

import generef.analytics.FailureKnowledgeAnalyzer;
import generef.analytics.FailureKnowledgeRepositoryAnalyzer;

import java.util.ArrayList;
import java.util.List;

import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import tea.analytics.model.TCompileErrorHistory;
import tea.analytics.model.TCompilePoint;

public class CompileErrorAnalyzerList {
	private PLProject project;

	private List<CompileErrorAnalyzer> children = new ArrayList<CompileErrorAnalyzer>();

	private FailureKnowledgeRepositoryAnalyzer fkRepositoryAnalyzer;

	/****************************************************
	 * Constructor
	 ****************************************************/

	public CompileErrorAnalyzerList(PLProject project) {
		this.project = project;
	}

	public void analyze() {
		// create fkRepositoryAnalyzer
		fkRepositoryAnalyzer = new FailureKnowledgeRepositoryAnalyzer(project);

		// create child
		for (PLFile file : project.getFiles()) {
			FailureKnowledgeAnalyzer fkAnalyzer = fkRepositoryAnalyzer
					.getFailureKnowledgeAnalyzer(file.getName());
			CompileErrorAnalyzer child = new CompileErrorAnalyzer(file,
					fkAnalyzer);
			child.analyze();
			children.add(child);
		}
	}

	/****************************************************
	 * getter
	 ****************************************************/

	public PLProject getProject() {
		return project;
	}

	public CompileErrorAnalyzer getChild(String fileName) {
		for (CompileErrorAnalyzer child : children) {
			if (fileName.equals(child.getFile().getName())) {
				return child;
			}
		}
		return null;
	}

	public List<TCompileErrorHistory> getHistories() {
		List<TCompileErrorHistory> histories = new ArrayList<TCompileErrorHistory>();
		for (CompileErrorAnalyzer child : children) {
			histories.addAll(child.getHistories());
		}
		return histories;
	}

	public List<TCompilePoint> getCompilePoints() {
		List<TCompilePoint> compilePoints = new ArrayList<TCompilePoint>();
		for (CompileErrorAnalyzer child : children) {
			compilePoints.addAll(child.getCompilePoints());
		}
		return compilePoints;
	}

}
