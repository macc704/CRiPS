/*
 * AMetaModel.java
 * Created on 2012/05/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.controller;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import ppv.app.taskdesigner.PPTaskDesignController;
import ppv.app.taskdesigner.model.PPActualTask;
import ppv.app.taskdesigner.model.PPEstimatedTask;
import clib.common.time.CTime;
import clib.view.table.model.ICTableModelDescripter;
import cswing.table.model.CAbstractEditableFieldProvider;
import cswing.table.model.CObjectEditableField;
import cswing.table.model.CStringEditableField;
import cswing.table.model.ICEditableFieldProvider;
import cswing.table.model.ICTableElementEditorDescripter;

/**
 * @author macchan
 * 
 */
public class PPActualTaskEditorDescripter implements
		ICTableElementEditorDescripter<PPActualTask> {

	private PPTaskDesignController controller;
	private List<PPEstimatedTask> ceTasks;

	public PPActualTaskEditorDescripter(PPTaskDesignController controller,
			List<PPEstimatedTask> ceTasks) {
		this.controller = controller;
		this.ceTasks = ceTasks;
	}

	public String getName() {
		return "作業内容";
	}

	public PPActualTask newInstance() {
		return controller.createTask();
	}

	private ICTableModelDescripter<PPActualTask> descripter = new ICTableModelDescripter<PPActualTask>() {
		public java.lang.Class<?> getValiableClass(int index) {
			return new Class[] { CTime.class, CTime.class, String.class,
					PPEstimatedTask.class }[index];
		}

		public Object getVariableAt(PPActualTask model, int index) {
			return new Object[] { model.getStart(), model.getEnd(),
					model.getName(), model.getCeTask() }[index];
		}

		public int getVariableCount() {
			return 4;
		}

		public String getVariableName(int index) {
			return new String[] { "開始", "終了", "作業内容", "対応見積" }[index];
		}
	};

	public ICTableModelDescripter<PPActualTask> getDescripter() {
		return descripter;
	}

	public ICEditableFieldProvider<PPActualTask> createEditableFieldProvider(
			PPActualTask model) {
		return new CAbstractEditableFieldProvider<PPActualTask>(model) {
			protected void initialize() {
				CStringEditableField nameeditor = new CStringEditableField(
						"作業内容", getField("name"), getModel());
				add(nameeditor);
				CObjectEditableField ceeditor = new CObjectEditableField(
						"対応見積", getField("ceTask"), getModel(), ceTasks);
				add(ceeditor);

				final JTextField namefield = (JTextField) nameeditor
						.getEditor();
				final JComboBox<?> cefield = (JComboBox<?>) ceeditor
						.getEditor();
				cefield.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						String text = namefield.getText();
						if (!isEditing(text)) {
							namefield.setText(cefield.getSelectedItem()
									.toString());
						}
					}

					boolean isEditing(String text) {
						if (text == null) {
							return false;
						}
						if (text.length() <= 0) {
							return false;
						}
						int len = cefield.getItemCount();
						for (int i = 0; i < len; i++) {
							Object o = cefield.getItemAt(i);
							if (text.equals(o.toString())) {
								return false;
							}
						}
						return true;
					}
				});
			}
		};
	}
}
