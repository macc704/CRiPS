/*
 * PPTaskTimeLineView.java
 * Created on 2012/06/07
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.timeline;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import ppv.app.taskdesigner.model.PPRangeTask;
import ppv.view.parts.timelineview.PPAbstractTimeLineView;
import clib.common.model.ICModelChangeListener;
import clib.common.thread.ICTask;
import clib.common.time.CTime;
import clib.common.time.CTimeInterval;
import clib.view.actions.CActionUtils;
import clib.view.timeline.model.CTimeModel;
import clib.view.timeline.model.CTimeTransformationModel;

/**
 * @author macchan
 * 
 */
public class PPTaskTimeLineView<T extends PPRangeTask> extends
		PPAbstractTimeLineView {

	private static final long serialVersionUID = 1L;

	private CTimeModel tModel1;
	private CTimeModel tModel2;
	private IPPTaskProvider<T> provider;
	private Color color;

	/**
	 * @param timeModel
	 * @param unit
	 */
	public PPTaskTimeLineView(CTimeTransformationModel ttModel,
			CTimeModel tModel1, CTimeModel tModel2,
			IPPTaskProvider<T> provider, Color color) {
		super(ttModel, null);
		this.tModel1 = tModel1;
		this.tModel2 = tModel2;
		this.provider = provider;
		this.color = color;
		initialize();
	}

	private void initialize() {
		provider.addModelChangeListener(new ICModelChangeListener() {
			@Override
			public void modelUpdated(Object... args) {
				refresh();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refresh();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.isMetaDown()) {
					JPopupMenu menu = new JPopupMenu();
					menu.add(CActionUtils.createAction("Add", new ICTask() {
						@Override
						public void doTask() {
							provider.doAdd();
						}
					}));
					menu.show(e.getComponent(), e.getX(), e.getY());
					return;
				}
				if (e.getClickCount() == 2 && !e.isMetaDown()) {
					fit(e);
				}
			}

			void fit(MouseEvent e) {
				int x = e.getX();
				CTime time = getTimeModel().x2Time(x);
				List<? extends PPRangeTask> tasks = provider.getTasks();
				PPRangeTask beforeTask = null;
				PPRangeTask afterTask = null;
				for (int i = 0; i < tasks.size(); i++) {
					PPRangeTask task = tasks.get(i);
					if (task.getStart().after(time)) {
						afterTask = task;
						break;
					}
					beforeTask = task;
				}

				CTime start = getTimeModel().getRange().getStart();
				if (beforeTask != null) {
					start = beforeTask.getEnd();
				}

				CTime end = getTimeModel().getRange().getEnd();
				if (afterTask != null) {
					end = afterTask.getStart();
				}

				tModel1.setTime(end);
				tModel2.setTime(start);
			}
		});
		setLayout(null);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		refresh();
	}

	private void refresh() {
		removeAll();
		List<T> tasks = provider.getTasks();
		for (T task : tasks) {
			RangePanel panel = new RangePanel(task);
			add(panel);
		}
		validate();
		repaint();
	}

	class RangePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private T task;

		/**
		 * 
		 */
		public RangePanel(T task) {
			this.task = task;
			task.addModelListener(new ICModelChangeListener() {
				@Override
				public void modelUpdated(Object... args) {
					refresh();
				}
			});
			AMouseAdapter handler = new AMouseAdapter();
			addMouseListener(handler);
			addMouseMotionListener(handler);
			setLayout(new FlowLayout());
			refresh();
		}

		public void refresh() {
			removeAll();
			add(new JLabel(task.getName()));
			double start = getTimeModel().time2X(task.getStart());
			double end = getTimeModel().time2X(task.getEnd());
			int w = (int) (end - start);
			int h = PPTaskTimeLineView.this.getHeight();
			setBounds((int) start, 0, w, h);
			validate();
			repaint();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(color);
			g2d.fill3DRect(0, 0, getWidth() - 1, getHeight() - 1, true);
			// g2d.setColor(Color.BLACK);
			// g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			// g2d.setColor(Color.BLACK);
		}

		class AMouseAdapter extends MouseAdapter {
			final static int RELEASED = 0;
			final static int LEFT = 1;
			final static int RIGHT = 2;
			final static int DRAG = 3;
			final static int X = 3;

			private int state = RELEASED;
			private int px;

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					modify();
					return;
				}

				if (e.isMetaDown()) {
					JPopupMenu menu = new JPopupMenu();
					menu.add(CActionUtils.createAction("Remove", new ICTask() {
						@Override
						public void doTask() {
							provider.doRemove(task);
						}
					}));
					menu.show(e.getComponent(), e.getX(), e.getY());
					return;
				}

				fit();
				px = e.getX();
				if (px <= X) {
					state = LEFT;
				} else if (px >= e.getComponent().getWidth() - X - 1) {
					state = RIGHT;
				} else {
					state = DRAG;
				}
			}

			void fit() {
				tModel1.setTime(task.getEnd());
				tModel2.setTime(task.getStart());
			}

			void modify() {
				provider.doModify(task);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (state == RELEASED) {
					return;
				}

				Component target = e.getComponent();
				int mx = e.getX();
				int x = target.getX();
				int dx = mx - px;
				switch (state) {
				case LEFT: {
					CTime sTime = getTimeModel().x2Time(x + mx);
					sTime = CTime.chooseAfter(sTime, leftLimit());
					sTime = CTime.chooseBefore(sTime,
							task.getEnd().progressed(-10));
					task.setStart(sTime);
					tModel2.setTime(sTime);
				}
					break;
				case RIGHT: {
					CTime eTime = getTimeModel().x2Time(x + mx);
					eTime = CTime.chooseAfter(eTime, task.getStart()
							.progressed(10));
					eTime = CTime.chooseBefore(eTime, rightLimit());
					task.setEnd(eTime);
					tModel1.setTime(eTime);
				}
					break;
				case DRAG:
					try {
						task.beginTransaction();

						CTime sTime = task.getStart();
						CTime eTime = task.getEnd();
						int startX = (int) getTimeModel().time2X(sTime);
						CTime newSTime = getTimeModel().x2Time(startX + dx);
						int endX = (int) getTimeModel().time2X(eTime);
						CTime newETime = getTimeModel().x2Time(endX + dx);

						CTimeInterval intval = eTime.diffrence(sTime);
						CTime leftLimit = leftLimit();
						if (newSTime.before(leftLimit)) {
							newSTime = leftLimit;
							newETime = leftLimit.progressed(intval.getTime());
						}
						CTime rightLimit = rightLimit();
						if (newETime.after(rightLimit)) {
							newETime = rightLimit;
							newSTime = rightLimit.progressed(-intval.getTime());
						}

						task.setStart(newSTime);
						task.setEnd(newETime);
						// CTime eTime = getTimeModel().x2Time(x + dx);
						// eTime = CTime.chooseAfter(eTime, task.getStart());
						// eTime = CTime.chooseBefore(eTime, rightLimit());
						// task.setEnd(eTime);
					} finally {
						task.endTransaction();
					}
					break;
				default:
					break;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				fit();
				state = RELEASED;
			}
		}

		CTime leftLimit() {
			List<? extends PPRangeTask> tasks = provider.getTasks();
			int index = tasks.indexOf(task);
			if (index <= 0) {
				return getTimeModel().getRange().getStart();
			}
			return tasks.get(index - 1).getEnd();
		}

		CTime rightLimit() {
			List<? extends PPRangeTask> tasks = provider.getTasks();
			int index = tasks.indexOf(task);
			if (index <= -1 || index >= tasks.size() - 1) {
				return getTimeModel().getRange().getEnd();
			}
			return tasks.get(index + 1).getStart();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Graphics2D g2d = (Graphics2D) g;
		//
		// List<PPTask> tasks = getTaskUnit().getController().getTaskTable()
		// .getModels();
		// for (PPTask task : tasks) {
		// double start = getTimeModel().time2X(task.getStart());
		// double end = getTimeModel().time2X(task.getEnd());
		// // g2d.setBackground(Color.YELLOW);
		// int w = (int) (end - start) - 1;
		// int h = getHeight() - 1;
		// g2d.setColor(Color.YELLOW);
		// g2d.fill3DRect((int) start, 0, w, h, true);
		// g2d.setColor(Color.BLACK);
		// // g2d.drawRect((int) start, 0, w, h);
		// }
	}
}
