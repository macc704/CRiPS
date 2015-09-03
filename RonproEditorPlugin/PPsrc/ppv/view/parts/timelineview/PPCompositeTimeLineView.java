package ppv.view.parts.timelineview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLUnit;
import pres.loader.utils.PLLogSelecters;
import pres.loader.utils.PLLogSeparator;
import pres.loader.utils.PLWorkingTimeCalculator;
import clib.view.timeline.model.CTimeTransformationModel;

/*
 * プログラム編集タイムラインの一行を表現するクラス．
 * 各情報のタイムライン表示がレイヤーとしてここに重ねられる．
 * 
 * @author macchan
 * @TODO 詳細になった時に状態提示バーのみが消える（行と編集は生き残り）件，
 *       boxedLayoutPanelの中のオブジェクトの大きさが変わらないことが問題．
 *       -> getMaximumsize()を超えていたことが判明, BoxLayout時にそのMaximumに制限される．仕様通り
 *       -> PPAbstractTimeLineView#コンストラクタで，mamimumをInteger.MAX_VALUEにすることで改善．
 * 
 */
public class PPCompositeTimeLineView extends PPAbstractTimeLineView {

	private static final long serialVersionUID = 1L;

	private JPanel boxedLayoutPanel = new JPanel();

	public PPCompositeTimeLineView(CTimeTransformationModel timeModel,
			IPLUnit unit) {
		super(timeModel, unit);
		initialize();
	}

	private void initialize() {
		// for this view
		setLayout(null);
		setOpaque(true);
		setBackground(Color.WHITE);

		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				for (Component c : getComponents()) {
					c.setSize(getSize());
				}
				repaint();
			}
		});

		// install one views
		add(new PPSourceCountLineView(getTimeModel(), getUnit()));

		// boxed layout panel
		boxedLayoutPanel.setLayout(new BoxLayout(boxedLayoutPanel,
				BoxLayout.Y_AXIS));

		boxedLayoutPanel.setOpaque(false);
		add(boxedLayoutPanel);

		{// dummy
			boxedLayoutPanel
					.add(new PPEventLineView(getTimeModel(), getUnit()));
		}

		{// Run
			boxedLayoutPanel.add(new PPEventLineView(getTimeModel(), getUnit(),
					"START_RUN", Color.BLUE));
			// boxedLayoutPanel.add(new PPSubtypeStateLineView(getTimeModel(),
			// getUnit(), Color.BLUE, "START_RUN", "STOP_RUN"));
		}

		{// Compilation Error
			boxedLayoutPanel.add(new PPEventLineView(getTimeModel(), getUnit(),
					"COMPILE", Color.RED));
			boxedLayoutPanel.add(new PPCompileErrorStateLineView(
					getTimeModel(), getUnit(), Color.RED));
		}

		{// BlockEditor StateLine
			PPNewStateLineView lineview = new PPNewStateLineView(
					getTimeModel(), getUnit());
			lineview.addData(Color.ORANGE.darker(),
					new PLWorkingTimeCalculator().calculateForBE(getUnit())
							.getWorkingTimes());
			boxedLayoutPanel.add(lineview);
		}

		{// DENO使用StateLine
			PPNewStateLineView lineview = new PPNewStateLineView(
					getTimeModel(), getUnit());
			lineview.addData(Color.RED.darker(), new PLWorkingTimeCalculator()
					.calculateForND(getUnit()).getWorkingTimes());
			boxedLayoutPanel.add(lineview);
		}

		{// Working StateLine
			PPNewStateLineView lineview = new PPNewStateLineView(
					getTimeModel(), getUnit());
			lineview.addData(Color.CYAN.darker(), new PLWorkingTimeCalculator()
					.calculate(getUnit()).getWorkingTimes());
			boxedLayoutPanel.add(lineview);
		}

		{// Focus StateLine
			boxedLayoutPanel.add(new PPSubtypeStateLineView(getTimeModel(),
					getUnit(), Color.GREEN.darker(), "FOCUS_GAINED",
					"FOCUS_LOST"));
		}

		// boxedLayoutPanel.add(new PPDevelopingStateLineView(getTimeModel(),
		// getUnit(), Color.GREEN));

		// 下のレイヤーにtexteditラインを書く．
		{
			PPEventLineView lineview = new PPEventLineView(getTimeModel(),
					getUnit());
			// List<PLLog> logs = getUnit().getOrderedLogs().select(
			// PLLogSelecters.TEXTEDIT);
			List<PLLog> logs = getUnit().getOrderedLogs();// 一旦全部取ってこないと，format内などが解析できない
			PLLogSeparator separator = new PLLogSeparator();
			separator.separateForNonOneTextEdit(logs);
			lineview.addEvents(
					getUnit().getOrderedLogs().select(PLLogSelecters.TEXTEDIT),
					Color.CYAN.brighter());
			lineview.addEvents(separator.getSeparated(), Color.CYAN.darker());
			add(lineview);
		}

		{// TODO H24.1.21 保井追加 BlockEditorによる編集時間をライン表示
			PPEventLineView lineview = new PPEventLineView(getTimeModel(),
					getUnit());
			List<PLLog> logs = getUnit().getOrderedLogs().select(
					PLLogSelecters.BLOCKEDIT);
			PLLogSeparator separator = new PLLogSeparator();
			separator.separateForInvalidBlockEditorLog(logs);
			lineview.addEvents(separator.getNonSeparated(),
					Color.ORANGE.brighter());
			lineview.addEvents(separator.getSeparated(), Color.ORANGE.darker());
			add(lineview);
		}

	}
}
