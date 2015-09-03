package bc.j2b.analyzer;

public class IdCounter {

	private int idCounter;

	public IdCounter(int firstId) {
		idCounter = firstId;
	}

	public void addIdCounter(int count) {
		idCounter += count;
	}

	public int getNextId() {
		return idCounter++;
	}

}
