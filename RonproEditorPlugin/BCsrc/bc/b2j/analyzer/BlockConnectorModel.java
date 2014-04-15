/*
 * BlockConnectorModel.java
 * Created on 2012/11/24 by macchan
 * Copyright(c) 2012 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package bc.b2j.analyzer;

import bc.b2j.model.BlockModel;

/**
 * @author macchan BlockConnectorModel
 */
public class BlockConnectorModel {

	private int id = BlockModel.NULL;
	private String type;

	public BlockConnectorModel() {
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
