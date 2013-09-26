/*
 * PLRangeDetectionStateMachine.java
 * Created on 2011/06/30
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author macchan
 * 
 */
public abstract class PLRangeDetectionStateMachine<TARGET, EVENT> {

	private List<EVENT> events;
	private TARGET startTag;

	public PLRangeDetectionStateMachine() {
	}

	public final List<EVENT> process(List<TARGET> targets) {
		this.events = new ArrayList<EVENT>();
		this.startTag = null;
		int len = targets.size();
		for (int i = 0; i < len; i++) {
			TARGET current = targets.get(i);
			TARGET next = null;
			if (i + 1 < len) {
				next = targets.get(i + 1);
			}
			processOne(current, next);
		}
		if (isActive()) {
			TARGET endTag = targets.get(targets.size() - 1);
			events.add(createEvent(startTag, endTag));
			this.startTag = null;
		}
		return events;
	}

	private final void processOne(TARGET current, TARGET next) {
		if (isActive()) {
			if (next != null && isEndTag(current, next)) {
				TARGET endTag = current;
				events.add(createEvent(startTag, endTag));
				this.startTag = null;
			} else {

			}
		} else {
			if (next != null && isStartTag(current, next)) {
				this.startTag = current;
			} else {

			}
		}
	}

	public boolean isActive() {
		return startTag != null;
	}

	protected abstract boolean isStartTag(TARGET current, TARGET next);

	protected abstract boolean isEndTag(TARGET current, TARGET next);

	protected abstract EVENT createEvent(TARGET startTag, TARGET endTag);

}
