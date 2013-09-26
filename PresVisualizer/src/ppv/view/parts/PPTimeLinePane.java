package ppv.view.parts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import ppv.app.taskdesigner.timeline.PPTaskTimeLineView;
import ppv.app.taskdesigner.timeline.PPTaskUnit;
import ppv.view.frames.PPProjectViewerFrame;
import ppv.view.parts.timelineview.PPCompositeTimeLineView;
import pres.loader.model.IPLUnit;
import clib.view.timeline.model.CTimeModel;
import clib.view.timeline.pane.CAbstractTimeLinePane;

/*
 * 問題　順番を入れ替えたときに，編集インディケータが表示されなくなる．
 */
public class PPTimeLinePane extends CAbstractTimeLinePane<IPLUnit> {

	private static final long serialVersionUID = 1L;

	private CTimeModel timeModel = new CTimeModel();
	private CTimeModel timeModel2 = new CTimeModel();

	private boolean drawRightSide = true;

	public PPTimeLinePane() {
		getTimelinePane().createIndicator(Color.RED, timeModel);
		getTimelinePane().createIndicator(Color.BLUE, timeModel2);
		getTimelinePane().hookIndicationChangeMouseListener();
	}

	/**
	 * @return the timeModel
	 */
	public CTimeModel getTimeModel() {
		return timeModel;
	}

	/**
	 * @return the timeModel
	 */
	public CTimeModel getTimeModel2() {
		return timeModel2;
	}

	@Override
	public JComponent createLeftPanel(final IPLUnit model) {
		JComponent panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());

		// name label
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.setOpaque(false);
		JLabel label = new JLabel(model.getName());
		namePanel.add(label);

		if (model instanceof PPTaskUnit) {
			// JButton button = new JButton("[Add]");
		} else {
			// if( model instanceof PLFile )
			JButton button = new JButton("[Detail]");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					final PPProjectViewerFrame frame = new PPProjectViewerFrame(
							model);
					frame.setBounds(50, 50, 1000, 700);
					frame.setVisible(true);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							frame.fitScale();
						}
					});
				}
			});
			namePanel.add(button, BorderLayout.EAST);
		}

		panel.add(namePanel);

		// pulling panel
		JPanel pullingPanel = new JPanel();
		pullingPanel.setBackground(Color.WHITE);
		pullingPanel.setLayout(new BorderLayout());
		pullingPanel.add(new JLabel("+", SwingConstants.CENTER));
		pullingPanel.setPreferredSize(new Dimension(20, 20));
		MouseAdapter l = createDragMouseListener(model);
		pullingPanel.addMouseListener(l);
		pullingPanel.addMouseMotionListener(l);
		panel.add(pullingPanel, BorderLayout.WEST);
		return panel;
	}

	@Override
	public int getComponentHeight(IPLUnit model) {
		if (model instanceof PPTaskUnit) {
			return 20;
		}
		return super.getComponentHeight(model);
	}

	@Override
	public JComponent createRightPanel(IPLUnit model) {
		if (drawRightSide) {
			if (model instanceof PPTaskUnit) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				PPTaskTimeLineView viewer = new PPTaskTimeLineView(
						getTimelinePane().getTimeTransModel(), timeModel,
						timeModel2, ((PPTaskUnit) model).getProvider(),
						((PPTaskUnit) model).getColor());
				viewer.setMinimumSize(new Dimension(30, 100));
				return viewer;
			}
			PPCompositeTimeLineView viewer = new PPCompositeTimeLineView(
					getTimelinePane().getTimeTransModel(), model);
			return viewer;
		} else {
			return new JPanel();
		}
	}

	/**
	 * @param drawRightSide
	 *            the drawRightSide to set
	 */
	public void setDrawRightSide(boolean drawRightSide) {
		this.drawRightSide = drawRightSide;
	}

	/**
	 * @return the drawRightSide
	 */
	public boolean isDrawRightSide() {
		return drawRightSide;
	}
}
