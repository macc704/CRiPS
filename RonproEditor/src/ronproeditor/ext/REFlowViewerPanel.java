/*
 * FlowViewerPanel2.java
 * Created on 2012/01/16
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ronproeditor.ext;

import bc.j2b.model.ClassModel;
import bc.j2b.model.CompilationUnitModel;
import bc.j2b.model.ElementModel;
import bc.j2b.model.StAbstractionBlockModel;
import bc.j2b.model.StBlockModel;
import bc.j2b.model.StIfElseModel;
import bc.j2b.model.StMethodDeclarationModel;
import bc.j2b.model.StWhileModel;
import fcfw.FCFlowchartPanel;
import fcfw.components.FCCommandPanel;
import fcfw.components.FCCompositePanel;
import fcfw.components.FCIfElsePanel;
import fcfw.components.FCModulePanel;
import fcfw.components.FCWhilePanel;

/**
 * @author macchan
 */
public class REFlowViewerPanel extends FCFlowchartPanel {

	private static final long serialVersionUID = 1L;

	private CompilationUnitModel root;

	public REFlowViewerPanel(CompilationUnitModel root) {
		this.root = root;
		initialize();
	}

	private void initialize() {
		for (ClassModel clazz : root.getClasses()) {
			buildClass(clazz);
		}
		refresh();
	}

	private void buildClass(ClassModel clazz) {
		for (StMethodDeclarationModel method : clazz.getMethods()) {
			buildMethod(method, getRootPanel());
		}
	}

	private void buildMethod(StMethodDeclarationModel model,
			FCModulePanel parentPanel) {
		StBlockModel block = model.getBody();
		buildBlock(block, parentPanel.getBody());
	}

	private void buildBlock(StBlockModel model, FCCompositePanel parentPanel) {
		for (ElementModel child : model.getChildren()) {
			buildStatement(child, parentPanel);
		}
	}

	// TODO 上と同じになるはず．Modelの段階でまとめられるはず．（松）
	private void buildAbstractionBlock(StAbstractionBlockModel model,
			FCCompositePanel parentPanel) {
		FCCompositePanel panel = new FCCompositePanel();
		panel.setComment(model.getComment());
		for (ElementModel child : model.getChildren()) {
			buildStatement(child, panel);
		}
		parentPanel.add(panel);
	}

	private void buildStatement(ElementModel model, FCCompositePanel parentPanel) {
		if (model instanceof StBlockModel) {
			buildBlock((StBlockModel) model, parentPanel);
		} else if (model instanceof StAbstractionBlockModel) {
			buildAbstractionBlock((StAbstractionBlockModel) model, parentPanel);
		} else if (model instanceof StIfElseModel) {
			buildIfStatement((StIfElseModel) model, parentPanel);
		} else if (model instanceof StWhileModel) {
			buildWhileStatement((StWhileModel) model, parentPanel);
		} else {
			buildNormalStatement(model, parentPanel);
		}
	}

	private void buildNormalStatement(ElementModel model,
			FCCompositePanel parentPanel) {
		FCCommandPanel panel = new FCCommandPanel(model.getLabel());
		parentPanel.add(panel);
	}

	private void buildIfStatement(StIfElseModel model,
			FCCompositePanel parentPanel) {
		FCIfElsePanel panel = new FCIfElsePanel(model.getTestClause()
				.getLabel());
		if (model.getThenClause() != null) {
			buildBlock(model.getThenClause(), panel.getThenClause());
		}
		if (model.getElseClause() != null) {
			buildBlock(model.getElseClause(), panel.getElseClause());
		}
		// 2012.09.25 #matsuzawa
		// StatementModel elseClause = model.getElseClause();
		// if (elseClause != null) {
		// if (elseClause instanceof StBlockModel) {
		// buildBlock((StBlockModel) elseClause, panel.getElseClause());
		// } else if (elseClause instanceof StIfElseModel) {
		// buildIfStatement((StIfElseModel) elseClause, parentPanel);
		// } else {
		// throw new RuntimeException("unknown elseClause");
		// }
		// }
		parentPanel.add(panel);

	}

	private void buildWhileStatement(StWhileModel model,
			FCCompositePanel parentPanel) {
		FCWhilePanel panel = new FCWhilePanel(model.getTestClause().getLabel());
		buildBlock(model.getBodyClause(), panel.getDoClause());
		parentPanel.add(panel);
	}

}
