package generef.analytics;

import generef.knowledge.RSFailureKnowledge;
import generef.knowledge.RSFailureKnowledgeRepository;
import generef.knowledge.RSFailureKnowledgeRepositoryDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pres.loader.model.PLProject;

public class FailureKnowledgeRepositoryAnalyzer {

	private static final String REPOSITORY_FILE_NAME = "repository.dat";

	private PLProject project;

	private RSFailureKnowledgeRepository fkRepository;
	private HashMap<String, FailureKnowledgeAnalyzer> fkAnalyzers = new HashMap<String, FailureKnowledgeAnalyzer>();
	private List<FailureKnowledgeAnalyzer> fkAnalyzers2 = new ArrayList<FailureKnowledgeAnalyzer>();

	public FailureKnowledgeRepositoryAnalyzer(PLProject project) {
		this.project = project;
		String path = project.getProjectBaseDir()
				.getRelativePathFromExecuteDirectory().toString();
		File repositoryFile = new File(path + "/" + REPOSITORY_FILE_NAME);

		this.fkRepository = loadRepository(repositoryFile);
		if (fkRepository != null) {
			analyze();
		}
	}

	private RSFailureKnowledgeRepository loadRepository(File file) {
		if (!file.exists()) {
			return null;
		} else {
			return new RSFailureKnowledgeRepositoryDAO().load(file);
		}
	}

	private void analyze() {
		for (String name : getFileNames()) {
			FailureKnowledgeAnalyzer analyzer = createFailureKnowledgeAnalyzer(name);
			fkAnalyzers.put(name, analyzer);
			fkAnalyzers2.add(analyzer);
		}
	}

	public List<FailureKnowledgeAnalyzer> getFkAnalyzers() {
		return fkAnalyzers2;
	}

	private List<String> getFileNames() {
		List<String> names = new ArrayList<String>();
		for (RSFailureKnowledge knowledge : fkRepository.getFailureKnowledges()) {
			String name = knowledge.getCompileError().getSourceName();
			name = name.substring(name.lastIndexOf("\\") + 1);
			if (!names.contains(name)) {
				names.add(name);
			}
		}
		return names;
	}

	private FailureKnowledgeAnalyzer createFailureKnowledgeAnalyzer(
			String sourceName) {
		List<RSFailureKnowledge> knowledges = new ArrayList<RSFailureKnowledge>();
		for (RSFailureKnowledge knowledge : fkRepository.getFailureKnowledges()) {
			String name = knowledge.getCompileError().getSourceName();
			name = name.substring(name.lastIndexOf("\\") + 1);
			if (sourceName.equals(name)) {
				knowledges.add(knowledge);
			}
		}
		return new FailureKnowledgeAnalyzer(knowledges);
	}

	public boolean existRepositoryFile() {
		if (fkRepository == null) {
			return false;
		} else {
			return true;
		}
	}

	public RSFailureKnowledgeRepository getRepository() {
		return fkRepository;
	}

	public PLProject getProject() {
		return project;
	}

	public FailureKnowledgeAnalyzer getFailureKnowledgeAnalyzer(
			String sourceName) {
		return fkAnalyzers.get(sourceName);
	}
}
