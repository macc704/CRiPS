/*
 * PPDefectFixingTaskMetaModel.java
 * Created on 2012/06/19
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.controller;

import ppv.app.taskdesigner.PPTaskDesignController;
import ppv.app.taskdesigner.model.PPDefectFixingTask;
import clib.common.time.CTime;
import clib.view.table.model.ICTableModelDescripter;
import cswing.table.model.CAbstractEditableFieldProvider;
import cswing.table.model.CStringEditableField;
import cswing.table.model.ICEditableFieldProvider;
import cswing.table.model.ICTableElementEditorDescripter;

/**
 * @author macchan
 * 
 */
public class PPDefectFixingTaskEditorDescripter implements
		ICTableElementEditorDescripter<PPDefectFixingTask> {
	private PPTaskDesignController controller;

	public PPDefectFixingTaskEditorDescripter(PPTaskDesignController controller) {
		this.controller = controller;
	}

	public String getName() {
		return "欠陥修正記録";
	}

	public PPDefectFixingTask newInstance() {
		return controller.createDefectFixingTask();
	}

	private ICTableModelDescripter<PPDefectFixingTask> descripter = new ICTableModelDescripter<PPDefectFixingTask>() {
		public java.lang.Class<?> getValiableClass(int index) {
			return new Class[] { CTime.class, CTime.class, String.class, }[index];
		}

		public Object getVariableAt(PPDefectFixingTask model, int index) {
			return new Object[] { model.getStart(), model.getEnd(),
					model.getName() }[index];
		}

		public int getVariableCount() {
			return 3;
		}

		public String getVariableName(int index) {
			return new String[] { "開始", "終了", "内容" }[index];
		}
	};

	public ICTableModelDescripter<PPDefectFixingTask> getDescripter() {
		return descripter;
	}

	public ICEditableFieldProvider<PPDefectFixingTask> createEditableFieldProvider(
			PPDefectFixingTask model) {
		return new CAbstractEditableFieldProvider<PPDefectFixingTask>(model) {
			protected void initialize() {
				CStringEditableField nameeditor = new CStringEditableField(
						"内容", getField("name"), getModel());
				add(nameeditor);
			}
		};
	}
}
