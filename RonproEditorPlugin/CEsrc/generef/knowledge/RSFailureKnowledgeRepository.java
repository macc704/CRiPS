package generef.knowledge;

import java.util.ArrayList;
import java.util.List;

public class RSFailureKnowledgeRepository {

	private List<RSFKWritingPoint> points = new ArrayList<RSFKWritingPoint>();

	public void addAll(List<RSFailureKnowledge> failureKnowledges) {
		points.add(new RSFKWritingPoint(failureKnowledges));
	}

	public List<RSFailureKnowledge> getFailureKnowledges() {
		List<RSFailureKnowledge> failureKnowledges = new ArrayList<RSFailureKnowledge>();
		for (RSFKWritingPoint point : points) {
			failureKnowledges.addAll(point.getKnowledgeList());
		}
		return failureKnowledges;
	}

	public List<RSFKWritingPoint> getPoints() {
		return points;
	}

	/**
	 * messageで指定されたエラーメッセージの失敗知識をリストから全て取り出します
	 * 
	 * @param message
	 *            エラーメッセージ
	 * @return
	 */
	public List<RSFailureKnowledge> getFailureKnowledges(String message) {
		ArrayList<RSFailureKnowledge> list = new ArrayList<RSFailureKnowledge>();

		for (RSFailureKnowledge knowledge : getFailureKnowledges()) {
			if (knowledge.getCompileError().getMessageParser()
					.getAbstractionMessage().equals(message)) {
				list.add(knowledge);
			}
		}

		return list;
	}

	/**
	 * 失敗知識リストからエラーメッセージの種類を取り出します
	 * 
	 * @return String型のエラーメッセージリスト
	 */
	public List<String> getFailureKnowledgeKinds() {
		ArrayList<String> list = new ArrayList<String>();

		for (RSFailureKnowledge knowledge : getFailureKnowledges()) {
			String message = knowledge.getCompileError().getMessageParser()
					.getErrorMessage();
			if (!list.contains(message)) {
				list.add(message);
			}
		}

		return list;
	}
}
