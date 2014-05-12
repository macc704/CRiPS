package src.coco.view;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import ppv.view.parts.PPCompileResultPane;
import ppv.view.parts.PPSourcePane;
import ppv.view.parts.PPTimeLinePane;
import pres.loader.model.IPLUnit;
import pres.loader.model.PLFile;
import pres.loader.model.PLPackage;
import src.coco.model.CCCompileErrorManager;
import src.coco.model.CCOperateLog;
import clib.common.time.CTime;
import clib.view.timeline.model.CTimeModel;
import clib.view.timeline.model.CTimeTransformationModel;

public class CCSourceCompareViewer extends JFrame {
	private static final long serialVersionUID = 1L;

	private IPLUnit unit;

	private PPTimeLinePane timelinePane = new PPTimeLinePane();

	public CCSourceCompareViewer(IPLUnit unit, final int errorID,
			final int rowIndex, final CCCompileErrorManager manager) {
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
		initializeData();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				manager.writePresLog(CCOperateLog.SubType.SOURCE_CLOSE,
						errorID, rowIndex);
			}
		});
	}

	private void initialize() {
		JPanel mainPanel = new JPanel();
		JPanel southPanel = new JPanel();
		JSplitPane verticalSplitPane;
		JSplitPane topSplitPane;

		String pjName = unit.getProject().getName();
		String unitName = unit.getName();
		setTitle("Project Viewer - " + pjName + " " + unitName);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		CTimeModel model1 = timelinePane.getTimeModel();
		CTimeModel model2 = timelinePane.getTimeModel2();

		if (unit instanceof PLFile
				|| ((PLPackage) unit).getChildren().size() <= 1) {
			this.timelinePane.setDefaultComponentHeight(100);
		}

		mainPanel.setLayout(new BorderLayout());
		add(mainPanel);

		verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPanel.add(verticalSplitPane);

		// 下部 timelinepane
		southPanel.setLayout(new BorderLayout());
		verticalSplitPane.setRightComponent(southPanel);
		CTimeTransformationModel transModel = new CTimeTransformationModel(
				unit.getRange());
		timelinePane.getTimelinePane().setTimeTransModel(transModel);
		southPanel.add(timelinePane);

		// 上部
		topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitPane.setLeftComponent(topSplitPane);

		JSplitPane topRightSplitPane = sourceAndComsolePanel(unit, model1,
				"修正後");
		JSplitPane topLeftSplitPane = sourceAndComsolePanel(unit, model2, "修正前");

		topSplitPane.setRightComponent(topRightSplitPane);
		topSplitPane.setLeftComponent(topLeftSplitPane);

		// 大きさの調整
		topSplitPane.setResizeWeight(0.5);
		verticalSplitPane.setResizeWeight(0.8);
	}

	private JSplitPane sourceAndComsolePanel(IPLUnit unit, CTimeModel model,
			String frameMessage) {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		PPSourcePane sourcePanel = new PPSourcePane(unit, model);
		sourcePanel.setBorder(BorderFactory.createTitledBorder(frameMessage));
		splitPane.setLeftComponent(sourcePanel);

		PPCompileResultPane compileResultPanel = new PPCompileResultPane(model,
				unit);
		compileResultPanel.setBorder(BorderFactory
				.createTitledBorder("CompileResult"));
		splitPane.setRightComponent(compileResultPanel);

		splitPane.setResizeWeight(0.8);
		return splitPane;
	}

	private void initializeData() {
		// Adding Time Line Views
		if (unit instanceof PLFile) {
			timelinePane.addModel(unit);
		} else {
			throw new RuntimeException("ファイルの取得に失敗しました : " + unit.getPath());
		}

		CTime end = unit.getEnd();
		timelinePane.getTimeModel().setTime(end);

		CTime start = unit.getStart();
		timelinePane.getTimeModel2().setTime(start);
	}

	public void fitScale() {
		timelinePane.getTimelinePane().fitScale();
	}

	public PPTimeLinePane getTimelinePane() {
		return timelinePane;
	}
}