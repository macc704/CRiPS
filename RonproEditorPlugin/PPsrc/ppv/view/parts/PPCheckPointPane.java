/*
 * SavePointPanel.java
 * Created on 2011/06/22
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLUnit;
import clib.common.model.ICModelChangeListener;
import clib.common.thread.ICTask;
import clib.common.time.CTime;
import clib.common.time.CTimeOrderedList;
import clib.common.utils.ICChecker;
import clib.view.actions.CAction;
import clib.view.actions.CActionUtils;
import clib.view.timeline.model.CTimeModel;

/**
 * @author macchan
 */
public class PPCheckPointPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final DateFormat formatter = new SimpleDateFormat(
			"MM/dd HH:mm:ss");
	private ICChecker<PLLog> checkPointChecker;

	private CTimeModel timeModel;
	private IPLUnit unit;

	private JButton firstButton = new JButton();
	private JLabel previousLabel = new JLabel();
	private JButton previousButton = new JButton();
	private JLabel currentLabel = new JLabel();
	private JPanel currentComponent = new JPanel();
	private JLabel nextLabel = new JLabel();
	private JButton nextButton = new JButton();
	private JButton lastButton = new JButton();

	/**
	 * @param timePane
	 * @param project
	 */
	public PPCheckPointPane(CTimeModel timeModel, IPLUnit unit,
			final String kind) {
		this(timeModel, unit, new ICChecker<PLLog>() {
			public boolean check(PLLog t) {
				return t.getSubType().equals(kind);
			}
		});
	}

	/**
	 * @param timePane
	 * @param project
	 */
	public PPCheckPointPane(CTimeModel timeModel, IPLUnit unit,
			ICChecker<PLLog> checker) {
		this.timeModel = timeModel;
		this.unit = unit;
		this.checkPointChecker = checker;
		initialize();
	}

	public void initialize() {
		// previous
		JPanel previous = new JPanel();
		add(previous);
		previous.setLayout(new BorderLayout());
		previous.add(new JLabel("Previous"), BorderLayout.NORTH);
		{
			JPanel panel = new JPanel();
			previous.add(panel, BorderLayout.CENTER);
			firstButton.setAction(createFirst());
			panel.add(firstButton);
			previousButton.setAction(createBack());
			previousButton.setEnabled(false);
			panel.add(previousButton);
		}
		previous.add(previousLabel, BorderLayout.SOUTH);
		// current
		JPanel current = new JPanel();
		add(current);
		current.setLayout(new BorderLayout());
		current.add(new JLabel("Current"), BorderLayout.NORTH);
		current.add(currentComponent);
		current.add(currentLabel, BorderLayout.SOUTH);
		// next
		JPanel next = new JPanel();
		add(next);
		next.setLayout(new BorderLayout());
		next.add(new JLabel("Next"), BorderLayout.NORTH);
		{
			JPanel panel = new JPanel();
			next.add(panel, BorderLayout.CENTER);
			nextButton.setAction(createFwd());
			nextButton.setEnabled(false);
			panel.add(nextButton);
			lastButton.setAction(createLast());
			panel.add(lastButton);
		}
		next.add(nextLabel, BorderLayout.SOUTH);

		timeModel.addModelListener(new ICModelChangeListener() {
			public void modelUpdated(Object... args) {
				refresh();
			}
		});
	}

	public void setCurrentComponent(JComponent comp) {
		currentComponent.setLayout(new BorderLayout());
		currentComponent.add(comp);
	}

	void refresh() {
		CTime time = timeModel.getTime();
		currentLabel.setText(time.toString(formatter));

		// before
		{
			PLLog target = getCheckPointBefore();
			if (target != null) {
				previousButton.setEnabled(true);
				previousLabel.setText(target.getTime().toString(formatter));
			} else {
				previousButton.setEnabled(false);
				previousLabel.setText(" ");
			}
		}

		// after
		{
			PLLog target = getCheckPointAfter();
			if (target != null) {
				nextButton.setEnabled(true);
				nextLabel.setText(target.getTime().toString(formatter));
			} else {
				nextButton.setEnabled(false);
				nextLabel.setText(" ");
			}
		}

	}

	private CTimeOrderedList<PLLog> getCheckPoints() {
		CTimeOrderedList<PLLog> logs = new CTimeOrderedList<PLLog>(
				unit.getLogs());
		return logs.select(checkPointChecker);
	}

	private void gotoTheTime(PLLog target) {
		if (target != null) {
			CTime newTime = new CTime(target.getTimestamp());
			timeModel.setTime(newTime);
		}
	}

	private CAction createFirst() {
		return CActionUtils.createAction("|<", new ICTask() {
			public void doTask() {
				PLLog target = getCheckPoints().getFirst();
				gotoTheTime(target);
			}
		});
	}

	private CAction createLast() {
		return CActionUtils.createAction(">|", new ICTask() {
			public void doTask() {
				PLLog target = getCheckPoints().getLast();
				gotoTheTime(target);
			}
		});
	}

	private CAction createBack() {
		return CActionUtils.createAction("<", new ICTask() {
			public void doTask() {
				PLLog target = getCheckPointBefore();
				gotoTheTime(target);
			}
		});
	}

	private CAction createFwd() {
		return CActionUtils.createAction(">", new ICTask() {
			public void doTask() {
				PLLog target = getCheckPointAfter();
				gotoTheTime(target);
			}
		});
	}

	private PLLog getCheckPointBefore() {
		CTime time = timeModel.getTime();
		time = new CTime(time.getAsLong() - 1);
		return getCheckPoints().searchElementBefore(time);
	}

	private PLLog getCheckPointAfter() {
		CTime time = timeModel.getTime();
		time = new CTime(time.getAsLong() + 1);
		return getCheckPoints().searchElementAfter(time);
	}

}
