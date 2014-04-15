/*
 * ExpressionStatementModel.java
 * Created on 2011/10/02
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.j2b.model;

import java.io.PrintStream;

/**
 * @author macchan
 * 
 */
public class StExpressionModel extends StatementModel {

	private ExpressionModel model;

	public StExpressionModel(ExpressionModel model) {
		model.setParent(this);
		this.model = model;
	}

	public ExpressionModel getModel() {
		return model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see j2b.model.ElementModel#getId()
	 */
	@Override
	public int getId() {
		return model.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see j2b.model.ElementModel#getConnectorId()
	 */
	@Override
	public int getConnectorId() {
		return model.getConnectorId();
	}

	@Override
	public void print(PrintStream out, int indent) {
		model.print(out, indent);
	}

	public String getLabel() {
		return model.getLabel();
	}
}
