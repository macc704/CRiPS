/*
 * PPTaskProvider.java
 * Created on 2012/06/19
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.timeline;

import java.util.List;

import ppv.app.taskdesigner.model.PPRangeTask;
import clib.common.model.ICModelChangeListener;

/**
 * @author macchan
 * 
 */
public interface IPPTaskProvider<T extends PPRangeTask> {

	public List<T> getTasks();

	public void doAdd();

	public void doRemove(T task);

	public void doModify(T task);

	public void addModelChangeListener(ICModelChangeListener modelChangeListener);

}
