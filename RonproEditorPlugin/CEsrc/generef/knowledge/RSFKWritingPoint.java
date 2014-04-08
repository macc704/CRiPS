package generef.knowledge;

import java.util.Date;
import java.util.List;

public class RSFKWritingPoint {

	private Date date = new Date();
	private List<RSFailureKnowledge> knowledgeList;

	public RSFKWritingPoint(List<RSFailureKnowledge> knowledgeList) {
		this.knowledgeList = knowledgeList;

		for (RSFailureKnowledge knowledge : knowledgeList) {
			if (knowledge.getWritingPointTime() == 0) {
				knowledge.setWritingPointTime(date.getTime());
			}
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(long millis) {
		date = new Date(millis);
	}

	public List<RSFailureKnowledge> getKnowledgeList() {
		return knowledgeList;
	}

	public int getFKCount() {
		return knowledgeList.size();
	}

	@Override
	public String toString() {
		return getFKCount() + "å¬ÇÃÉGÉâÅ[@" + date.toString();
	}
}
