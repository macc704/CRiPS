/*
 * ExpressionModel.java
 * Created on 2011/10/02
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.j2b.model;

/**
 * @author macchan
 * 
 */
public abstract class ExpressionModel extends ElementModel {

	public static final String VOID = "void";

	private String type = "poly";

	public String getType() {
		return type;
	}

	public final void setType(String type) {
		this.type = type;
	}
}
