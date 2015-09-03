package coco.model;

public class CCAchivementData {
	private int property;
	private int threshold;
	private String hirotitle;
	private String explanation;

	public CCAchivementData() {

	}

	public void set(int property, int threshold, String hirotitle,
			String explanation) {
		this.property = property;
		this.threshold = threshold;
		this.hirotitle = hirotitle;
		this.explanation = explanation;
	}

	public int getProperty() {
		return property;
	}

	public int getThreshold() {
		return threshold;
	}

	public String getHirotitle() {
		return hirotitle;
	}

	public String getExplanation() {
		return explanation;
	}
}