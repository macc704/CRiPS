/*
 * PPMainViewer.java
 * Created on 2011/06/06
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ppv.app.taskdesigner.PPTaskDesignController;
import ppv.app.taskdesigner.PPTaskDesignFrame;
import ppv.app.taskdesigner.model.PPActualTask;
import ppv.app.taskdesigner.model.PPDefectFixingTask;
import ppv.app.taskdesigner.timeline.PPActualTaskProvider;
import ppv.app.taskdesigner.timeline.PPDefectFixingTaskProvider;
import ppv.app.taskdesigner.timeline.PPTaskUnit;
import ppv.view.parts.PPSourceMetricsPane;
import ppv.view.parts.PPTimeLinePane;
import pres.loader.model.IPLUnit;
import pres.loader.model.PLFile;
import pres.loader.model.PLPackage;
import clib.common.thread.ICTask;
import clib.common.time.CTime;
import clib.view.actions.CAction;
import clib.view.actions.CActionUtils;
import clib.view.timeline.model.CTimeModel;
import clib.view.timeline.model.CTimeTransformationModel;
import clib.view.timeline.pane.ICSelectionListener;
import cswing.table.view.ICElementEditableTableListener;

/**
 * @author macchan
 */
public class PPProjectViewerFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private IPLUnit unit;

	private PPTimeLinePane timelinePane = new PPTimeLinePane();
	private JSplitPane verticalSplitPane;
	private JSplitPane topSplitPane;
	private PPSourceMetricsPane source1;
	private PPSourceMetricsPane source2;
	private JPanel mainPanel = new JPanel();
	private JPanel southPanel = new JPanel();

	private PPTaskDesignFrame taskFrame;
	private PPTaskDesignController taskController;
	private JToggleButton autoNext = new JToggleButton();

	public PPProjectViewerFrame(IPLUnit unit) {
		// 単体srcフォルダの場合，自動で中身を展開する．（仮の機能）
		if (unit instanceof PLPackage
				&& ((PLPackage) unit).getChildren().size() == 1
				&& ((PLPackage) unit).getChildren().get(0) instanceof PLPackage
				&& "src".equals(((PLPackage) unit).getChildren().get(0)
						.getName())) {
			unit = ((PLPackage) unit).getChildren().get(0);
		}
		this.unit = unit;
		initialize();
		initializeToolBar();
		// initializeMenu();
		initializeData();
	}

	private void initialize() {
		String pjName = unit.getProject().getName();
		String unitName = unit.getName();
		setTitle("Project Viewer - " + pjName + " " + unitName);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		CTimeModel model1 = timelinePane.getTimeModel();
		CTimeModel model2 = timelinePane.getTimeModel2();
		// this.taskFrame = new PPTaskDesignFrame(unit, model1, model2);

		if (unit instanceof PLFile
				|| ((PLPackage) unit).getChildren().size() <= 1) {
			this.timelinePane.setDefaultComponentHeight(100);
		}

		mainPanel.setLayout(new BorderLayout());
		add(mainPanel);

		verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		// add(verticalSplitPane);
		mainPanel.add(verticalSplitPane);

		// 下部 timelinepane
		southPanel.setLayout(new BorderLayout());
		verticalSplitPane.setRightComponent(southPanel);
		CTimeTransformationModel transModel = new CTimeTransformationModel(
				unit.getRange());
		timelinePane.getTimelinePane().setTimeTransModel(transModel);
		timelinePane.getTimelinePane().createDefaultButtons();
		southPanel.add(timelinePane);

		// 上部
		topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitPane.setLeftComponent(topSplitPane);
		source1 = new PPSourceMetricsPane(unit, model1);
		topSplitPane.setRightComponent(source1);
		source2 = new PPSourceMetricsPane(unit, model2);
		topSplitPane.setLeftComponent(source2);
		source2.setVisible(false);

		// 大きさの調整
		topSplitPane.setResizeWeight(0.5);
		verticalSplitPane.setResizeWeight(0.7);
	}

	protected PPTaskDesignController getTaskController() {
		if (taskController == null) {
			taskController = new PPTaskDesignController(unit,
					timelinePane.getTimeModel(), timelinePane.getTimeModel2());
			taskController.getActualTaskTable()
					.addElementEditableTableListener(
							new ICElementEditableTableListener<PPActualTask>() {
								@Override
								public void elementAdded(PPActualTask object) {
									if (autoNext.isSelected()) {
										doBtoR();
									}
								}

								@Override
								public void elementRemoved(PPActualTask object) {
								}
							});
		}
		return taskController;
	}

	/**
	 * @return the taskManager
	 */
	protected void showTaskFrame() {
		if (taskFrame != null && taskFrame.isVisible()) {
			taskFrame.toFront();
		} else {
			taskFrame = new PPTaskDesignFrame(getTaskController());
			Rectangle r = getBounds();
			int w = r.width / 2;
			int h = r.height / 2;
			int x = r.x + w / 2;
			int y = r.y + h / 2;
			taskFrame.setBounds(x, y, w, h);
			taskFrame.setVisible(true);
		}
	}

	private void initializeToolBar() {
		JToolBar toolBar = new JToolBar();
		// mainPanel.add(toolBar, BorderLayout.NORTH);
		southPanel.add(toolBar, BorderLayout.NORTH);
		{
			CAction action = CActionUtils.createAction("TaskViewer",
					new ICTask() {
						public void doTask() {
							showTaskFrame();
						}
					});

			toolBar.add(action);
		}
		{
			CAction action = CActionUtils.createAction("V|V", new ICTask() {
				public void doTask() {
					doToggleExtraView();
				}
			});
			toolBar.add(action);
		}
		{
			CAction action = CActionUtils.createAction("<->", new ICTask() {
				public void doTask() {
					doReverse();
				}
			});

			toolBar.add(action);
		}
		{
			autoNext.setText("Auto->!");
			autoNext.setSelected(true);
			toolBar.add(autoNext);
		}
		{
			CAction action = CActionUtils.createAction("->!", new ICTask() {
				public void doTask() {
					doBtoR();
				}
			});

			toolBar.add(action);
		}
		{
			CAction action = CActionUtils.createAction("Add", new ICTask() {
				public void doTask() {
					getTaskController().doAddActualTask();
				}
			});

			toolBar.add(action);
		}
	}

	protected void doBtoR() {
		CTimeModel model1 = timelinePane.getTimeModel();
		CTimeModel model2 = timelinePane.getTimeModel2();
		model2.setTime(model1.getTime());
	}

	protected void doReverse() {
		CTimeModel model1 = timelinePane.getTimeModel();
		CTimeModel model2 = timelinePane.getTimeModel2();
		CTime tmp = model1.getTime();
		model1.setTime(model2.getTime());
		model2.setTime(tmp);
	}

	protected void doToggleExtraView() {
		if (!source2.isVisible()) {
			source2.setVisible(true);
			topSplitPane.setDividerLocation(0.5);
		} else {
			source2.setVisible(false);
		}
	}

	@SuppressWarnings("unused")
	private void initializeMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		{
			JMenu menu = new JMenu("View");
			menuBar.add(menu);
			{
				CAction action = CActionUtils.createAction("ToggleExtraView",
						new ICTask() {
							public void doTask() {
								doToggleExtraView();
							}
						});
				menu.add(action);
			}
		}
	}

	private void initializeData() {
		// TODO Task Line View
		timelinePane
				.addModel(new PPTaskUnit<PPDefectFixingTask>("欠陥修正",
						new PPDefectFixingTaskProvider(getTaskController()),
						Color.RED));
		timelinePane.addModel(new PPTaskUnit<PPActualTask>("作業内容",
				new PPActualTaskProvider(getTaskController()), Color.YELLOW));

		// Adding Time Line Views
		if (unit instanceof PLFile) {
			timelinePane.addModel(unit);
		} else if (unit instanceof PLPackage) {
			List<IPLUnit> children = ((PLPackage) unit).getChildren();

			// 時間順に並び替える
			Collections.sort(children, new Comparator<IPLUnit>() {
				@Override
				public int compare(IPLUnit u1, IPLUnit u2) {
					// bug#11 fix
					try {
						return (int) (u1.getStart().getAsLong() - u2.getStart()
								.getAsLong());
					} catch (Exception ex) {// bug#11 応急処置
						return 0;
					}
				}
			});
			for (IPLUnit each : children) {
				timelinePane.addModel(each);
			}
		} else {
			throw new RuntimeException();
		}

		CTime end = unit.getEnd();
		timelinePane.getTimeModel().setTime(end);

		CTime start = unit.getStart();
		timelinePane.getTimeModel2().setTime(start);

		timelinePane.addSelectionListener(new ICSelectionListener() {
			public void selectionChanged() {
				IPLUnit unit = timelinePane.getSelectedModel();
				source1.setSelectedUnit(unit);
				source2.setSelectedUnit(unit);
			}
		});
	}

	public void fitScale() {
		timelinePane.getTimelinePane().fitScale();
	}
}
