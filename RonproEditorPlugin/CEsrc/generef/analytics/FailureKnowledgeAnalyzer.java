package generef.analytics;

import generef.knowledge.RSFailureKnowledge;

import java.util.ArrayList;
import java.util.List;

public class FailureKnowledgeAnalyzer {

	private List<RSFailureKnowledge> knowledges;

	public FailureKnowledgeAnalyzer(List<RSFailureKnowledge> knowledges) {
		this.knowledges = knowledges;
	}

	public List<RSFailureKnowledge> getKnowledges() {
		return knowledges;
	}

	public long getWritingTime() {
		long writingTime = 0;
		for (RSFailureKnowledge knowledge : getWritingPointKnowledges()) {
			writingTime += knowledge.getWritingTime();
		}
		return writingTime;
	}

	public long getWrintingTimeWithoutIsWorking() {
		long writingTime = 0;
		for (RSFailureKnowledge knowledge : getWritingPointKnowledges()) {
			if (knowledge.getWritingTime() < 5 * 60 * 1000) { // isWorkingのものだけ取り出す
				writingTime += knowledge.getWritingTime();
			}
		}
		return writingTime;
	}

	/**
	 * 各WritingPointから1つずつ失敗知識を取り出したリストを返します
	 * 
	 * @return
	 */
	public List<RSFailureKnowledge> getWritingPointKnowledges() {
		List<RSFailureKnowledge> knowledges = new ArrayList<RSFailureKnowledge>();

		long writingPoint = 0;
		for (RSFailureKnowledge knowledge : this.knowledges) {
			if (writingPoint != knowledge.getWritingPointTime()) {
				knowledges.add(knowledge);
				writingPoint = knowledge.getWritingPointTime();
			}
		}

		return knowledges;
	}

}
