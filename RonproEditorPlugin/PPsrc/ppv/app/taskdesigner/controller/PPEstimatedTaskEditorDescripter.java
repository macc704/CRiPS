/*
 * AMetaModel.java
 * Created on 2012/05/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.controller;

import ppv.app.taskdesigner.model.PPEstimatedTask;
import clib.view.table.model.ICTableModelDescripter;
import cswing.table.model.CAbstractEditableFieldProvider;
import cswing.table.model.CNumberEditableField;
import cswing.table.model.CStringEditableField;
import cswing.table.model.ICEditableFieldProvider;
import cswing.table.model.ICTableElementEditorDescripter;

/**
 * @author macchan
 * 
 */
public class PPEstimatedTaskEditorDescripter implements ICTableElementEditorDescripter<PPEstimatedTask> {
	public PPEstimatedTaskEditorDescripter() {
	}

	public String getName() {
		return "見積もり要素";
	}

	public PPEstimatedTask newInstance() {
		return new PPEstimatedTask("");
	}

	private ICTableModelDescripter<PPEstimatedTask> descripter = new ICTableModelDescripter<PPEstimatedTask>() {
		public java.lang.Class<?> getValiableClass(int index) {
			return new Class[] { String.class, Integer.class }[index];
		}

		public Object getVariableAt(PPEstimatedTask model, int index) {
			return new Object[] { model.getName(), model.getEstimation() }[index];
		}

		public int getVariableCount() {
			return 2;
		}

		public String getVariableName(int index) {
			return new String[] { "作業内容", "見積（分）" }[index];
		}
	};

	public ICTableModelDescripter<PPEstimatedTask> getDescripter() {
		return descripter;
	}

	public ICEditableFieldProvider<PPEstimatedTask> createEditableFieldProvider(
			PPEstimatedTask model) {
		return new CAbstractEditableFieldProvider<PPEstimatedTask>(model) {
			protected void initialize() {
				add(new CStringEditableField("作業内容", getField("name"),
						getModel()));
				add(new CNumberEditableField("見積（分）",
						getField("estimation"), getModel()));
			}
		};
	}
}
