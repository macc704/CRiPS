/*
 * PPActualTaskProvider.java
 * Created on 2012/06/19
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.timeline;

import java.util.List;

import ppv.app.taskdesigner.PPTaskDesignController;
import ppv.app.taskdesigner.model.PPActualTask;
import clib.common.model.ICModelChangeListener;
import cswing.table.view.ICElementEditableTableListener;

/**
 * @author macchan
 * 
 */
public class PPActualTaskProvider implements IPPTaskProvider<PPActualTask> {

	private PPTaskDesignController controller;

	/**
	 * 
	 */
	public PPActualTaskProvider(PPTaskDesignController controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.app.taskdesigner.timeline.IPPTaskProvider#doAdd()
	 */
	@Override
	public void doAdd() {
		controller.doAddActualTask();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.app.taskdesigner.timeline.IPPTaskProvider#getTasks()
	 */
	@Override
	public List<PPActualTask> getTasks() {
		return controller.getActualTasks();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ppv.app.taskdesigner.timeline.IPPTaskProvider#doRemove(ppv.app.taskdesigner
	 * .model.PPRangeTask)
	 */
	@Override
	public void doRemove(PPActualTask task) {
		controller.getActualTaskTable().doRemove(task);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ppv.app.taskdesigner.timeline.IPPTaskProvider#doModify(ppv.app.taskdesigner
	 * .model.PPRangeTask)
	 */
	@Override
	public void doModify(PPActualTask task) {
		controller.getActualTaskTable().modify(task);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ppv.app.taskdesigner.timeline.IPPTaskProvider#addModelChangeListener(
	 * clib.common.model.ICModelChangeListener)
	 */
	@Override
	public void addModelChangeListener(
			final ICModelChangeListener modelChangeListener) {
		controller.getActualTaskTable().addElementEditableTableListener(
				new ICElementEditableTableListener<PPActualTask>() {

					@Override
					public void elementRemoved(PPActualTask object) {
						modelChangeListener.modelUpdated();
					}

					@Override
					public void elementAdded(PPActualTask object) {
						modelChangeListener.modelUpdated();
					}
				});
	}
}
