/*
 * BlockModel.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author macchan
 * 
 */
public class StBlockModel extends StatementModel {

	protected List<ElementModel> children = new ArrayList<ElementModel>();

	public void addElement(ElementModel child) {
		// TODO Blockの中にBlockを入れるとIdがずれるので突貫工事
		// if (child instanceof BlockStatementModel) {
		// for (ElementModel each : ((BlockStatementModel) child).children) {
		// addElement(each);
		// }
		// return;
		// }
		child.setParent(this);
		children.add(child);
	}

	public List<ElementModel> getChildren() {
		return children;
	}

	public ElementModel getChild(int index) {
		if (index > children.size()) {
			return null;
		}
		return children.get(index);
	}

	public ElementModel getLastChild() {
		return children.get(children.size() - 1);
	}

	public int getChildrenSize() {
		return children.size();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return getParent().getId();

		// if (children.size() > 0) {
		// return children.get(0).getId();
		// } else {
		// return -1;
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see j2b.model.ElementModel#setConnectorId(int)
	 */
	@Override
	public void setConnectorId(int connectorId) {
		if (children.size() > 0) {
			children.get(0).setConnectorId(connectorId);
		} else {
			super.setConnectorId(connectorId);
		}
	}

	@Override
	public void print(PrintStream out, int indent) {
		for (int i = 0; i < children.size(); i++) {
			ElementModel child = children.get(i);
			child.print(out, indent);
		}
	}
}
