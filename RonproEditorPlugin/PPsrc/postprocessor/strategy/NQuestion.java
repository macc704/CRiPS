/*
 * NKadai.java
 * Created on 2013/02/09 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package postprocessor.strategy;

/**
 * @author macchan NKadai
 */
public class NQuestion {

	public enum LanguageRequirement {
		JAVA, BLOCK, ANY
	};

	private String lecture;
	private String number;
	private String filename;
	private boolean mandatory = true;
	private LanguageRequirement langReq = LanguageRequirement.ANY;

	public NQuestion(String lecture, String number, String filename,
			boolean mandatory, LanguageRequirement langReq) {
		super();
		this.lecture = lecture;
		this.number = number;
		this.filename = filename;
		this.mandatory = mandatory;
		this.langReq = langReq;
	}

	/**
	 * @return the lecture
	 */
	public String getLecture() {
		return lecture;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the langReq
	 */
	public LanguageRequirement getLangReq() {
		return langReq;
	}

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 */
	public String getId() {
		return "Q" + getLecture() + "-" + getNumber();
	}
}
