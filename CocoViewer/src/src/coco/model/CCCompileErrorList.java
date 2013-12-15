package src.coco.model;

import java.util.ArrayList;

public class CCCompileErrorList {
	private String message = "null message";
	private int rare = 0;
	private ArrayList<CCCompileError> errors = new ArrayList<CCCompileError>();

	public CCCompileErrorList() {

	}

	public void setMessageData(int rare, String message) {
		this.rare = rare;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public int getRare() {
		return rare;
	}

	public void addError(CCCompileError error) {
		errors.add(error);
	}

	public ArrayList<CCCompileError> getErrors() {
		return errors;
	}
}