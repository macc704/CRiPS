/*
 * PPDefectFixingTaskProvider.java
 * Created on 2012/06/19
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.timeline;

import java.util.List;

import ppv.app.taskdesigner.PPTaskDesignController;
import ppv.app.taskdesigner.model.PPDefectFixingTask;
import clib.common.model.ICModelChangeListener;
import cswing.table.view.ICElementEditableTableListener;

/**
 * @author macchan
 * 
 */
public class PPDefectFixingTaskProvider implements
		IPPTaskProvider<PPDefectFixingTask> {

	private PPTaskDesignController controller;

	/**
	 * 
	 */
	public PPDefectFixingTaskProvider(PPTaskDesignController controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.app.taskdesigner.timeline.IPPTaskProvider#doAdd()
	 */
	@Override
	public void doAdd() {
		controller.doAddDefectFixingTask();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.app.taskdesigner.timeline.IPPTaskProvider#getTasks()
	 */
	@Override
	public List<PPDefectFixingTask> getTasks() {
		return controller.getDefectFixingTasks();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ppv.app.taskdesigner.timeline.IPPTaskProvider#doRemove(ppv.app.taskdesigner
	 * .model.PPRangeTask)
	 */
	@Override
	public void doRemove(PPDefectFixingTask task) {
		controller.doRemoveDefectFixingTask(task);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ppv.app.taskdesigner.timeline.IPPTaskProvider#doModify(ppv.app.taskdesigner
	 * .model.PPRangeTask)
	 */
	@Override
	public void doModify(PPDefectFixingTask task) {
		controller.getDefectFixingTaskTable().modify(task);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ppv.app.taskdesigner.timeline.IPPTaskProvider#addModelChangeListener(
	 * ppv.app.taskdesigner.timeline.IPPModelChangeListener)
	 */
	@Override
	public void addModelChangeListener(
			final ICModelChangeListener modelChangeListener) {
		controller.getDefectFixingTaskTable().addElementEditableTableListener(
				new ICElementEditableTableListener<PPDefectFixingTask>() {

					@Override
					public void elementRemoved(PPDefectFixingTask object) {
						modelChangeListener.modelUpdated();
					}

					@Override
					public void elementAdded(PPDefectFixingTask object) {
						modelChangeListener.modelUpdated();
					}
				});
	}

}
