/*
 * StatementModel.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.j2b.model;

import java.io.PrintStream;

/**
 * @author macchan
 * 
 */
public abstract class ElementModel {

	private int id = -1;
	private int connectorId = -1;
	private int next = -1;
	private int previous = -1;
	private ElementModel parent;

	private int posX = 50;
	private int posY = 20;
	private String comment = "any Comment";
	protected int blockHeight;

	private int lineNumber = -1;

	// protected OpenBlocksCodePrinter coder = new OpenBlocksCodePrinter();

	/**
	 * 
	 */
	public ElementModel() {
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
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(ElementModel parent) {
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public ElementModel getParent() {
		return parent;
	}

	/**
	 * @param connectorId
	 *            the connectorId to set
	 */
	public void setConnectorId(int connectorId) {
		this.connectorId = connectorId;
	}

	/**
	 * @return the connectorId
	 */
	public int getConnectorId() {
		return connectorId;
	}

	// /**
	// * @param next
	// * the next to set
	// */
	// public void setNext(ElementModel next) {
	// if (next != null) {
	// this.next = next.getId();
	// }
	// }

	private void setNext(int next) {
		this.next = next;
	}

	/**
	 * @return the next
	 */
	public int getNext() {
		return next;
	}

	// /**
	// * @param privious
	// * the previous
	// */
	// public void setPrevious(ElementModel previous) {
	// if (previous != null) {
	// this.previous = previous.getId();
	// }
	// }

	private void setPrevious(int previous) {
		this.previous = previous;
	}

	/**
	 * @return the previous
	 */
	public int getPrevious() {
		return previous;
	}

	/**
	 * @return the posX
	 */
	public int getPosX() {
		return this.posX;
	}

	/**
	 * @return the posY
	 */
	public int getPosY() {
		return this.posY;
	}

	public void setPosX(int x) {
		this.posX = x;
	}

	/**
	 * @param addPosY
	 *            the addPosY to add
	 */
	public void setPosY(int y) {
		this.posY = y;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String str) {
		this.comment = str;
	}

	protected void setBlockHeight(int blockHeight) {
		this.blockHeight = blockHeight;
	}

	public int getBlockHeight() {
		return blockHeight;
	}

	public void print(PrintStream out, int indent) {
	}

	public void makeIndent(int number) {
		for (int i = 0; i < number; i++) {
			System.out.print("\t");
		}
	}

	// TODO
	public static String convertJavaTypeToBlockGenusName(String type) {
		if (type == null) {
			return "void";
		} else if (type.equals("object") || type.equals("Object")) {
			return "object";
		} else if (type.equals("void")) {
			return "void";
		} else if (type.equals("number")) {
			return "number";
		} else if (type.equals("double-number")) {
			return "double-number";
		} else if (type.equals("int") || type.equals("int[]")) {
			// return "int-number";
			return "number";
		} else if (type.equals("double") || type.equals("float")
				|| type.equals("double[]") || type.equals("float[]")) {
			return "double-number";
			// return "number";
			// return "double";
		} else if (type.equals("string") || type.equals("String")
				|| type.equals("char") || type.equals("String[]")) {
			return "string";
		} else if (type.equals("boolean")) {
			return "boolean";
		} else if (type.equals("List") || type.equals("ArrayList")) {
			return type.toLowerCase() + "object";
		} else if (type.equals("BCanvas") || type.equals("BWindow")
				|| type.equals("BSound") || type.equals("Color")) {
			return "object-" + type.toLowerCase();
		} else {
			return "object";
		}
	}

	public static String getConnectorType(String type) {
		if (type == null) {
			return "void";
		} else if (type.equals("object") || type.equals("Object")) {
			return "object";
		} else if (type.equals("void")) {
			return "void";
		} else if (type.equals("number")) {
			return "number";
		} else if (type.equals("double-number")) {
			return "double-number";
		} else if (type.equals("int")) {
			// return "int-number";
			return "number";
		} else if (type.equals("double") || type.equals("float")) {
			return "double-number";
			// return "number";
			// return "double";
		} else if (type.equals("string") || type.equals("String")
				|| type.equals("char")) {
			return "string";
		} else if (type.equals("boolean")) {
			return "boolean";
		} else if (type.equals("List") || type.equals("listobject")
				|| type.equals("int[]") || type.equals("double[]")
				|| type.equals("String[]") || type.equals("Object[]")) {
			return "object";
		} else {
			return "object";
		}

	}

	public String getClassName() {
		ElementModel current = this;
		while (!(current instanceof ClassModel)) {
			current = current.getParent();
		}
		return ((ClassModel) current).getName();
	}

	protected void resolveBeforeAfterBlock(ElementModel parent) {
		if (parent instanceof StBlockModel) {
			int i = 0;
			while (((StBlockModel) parent).getChild(i) != null) {
				ElementModel child = ((StBlockModel) parent).getChild(i);

				if (child.getId() == getId()) {
					// BeforeBlockId‚ðÝ’è
					if (i == 0) {
						setPrevious(parent.getId());
					} else {
						setPrevious(((StBlockModel) parent).getChild(i - 1)
								.getId());
					}

					// AfterBlockId‚ðÝ’è
					if (i + 1 < ((StBlockModel) parent).getChildrenSize()) {
						setNext(((StBlockModel) parent).getChild(i + 1).getId());
					}
					return;
				}
				i++;
			}
		} else if (parent instanceof StAbstractionBlockModel) {
			int i = 0;
			while (((StAbstractionBlockModel) parent).getChild(i) != null) {
				ElementModel child = ((StAbstractionBlockModel) parent)
						.getChild(i);
				if (child.getId() == getId()) {
					// BeforeBlockId‚ðÝ’è
					if (i == 0) {
						setPrevious(parent.getId());
					} else {
						setPrevious(((StAbstractionBlockModel) parent)
								.getChild(i - 1).getId());
					}

					// AfterBlockId‚ðÝ’è
					if (i + 1 < ((StAbstractionBlockModel) parent)
							.getChildrenSize()) {
						setNext(((StAbstractionBlockModel) parent).getChild(
								i + 1).getId());
					}
					return;
				}
				i++;
			}
		}
	}

	public void makeIndent(PrintStream out, int number) {
		for (int i = 0; i < number; i++) {
			out.print("\t");
		}
	}

	public String getLabel() {
		return "default label";
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param lineNumber
	 *            the lineNumber to set
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public static String addEscapeSequence(String name) {
		String s = name;
		if (s.contains("<")) {
			s = s.replaceAll("<", "&lt;");
		}
		if (name.contains(">")) {
			s = s.replaceAll(">", "&gt;");
		}

		return s;
	}
}
