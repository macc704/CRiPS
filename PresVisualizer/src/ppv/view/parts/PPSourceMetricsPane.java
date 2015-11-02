package ppv.view.parts;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import pres.loader.model.IPLFileProvider;
import pres.loader.model.IPLUnit;
import pres.loader.model.PLFile;
import clib.common.model.ICModelChangeListener;
import clib.common.time.CTime;
import clib.view.timeline.model.CTimeModel;

public class PPSourceMetricsPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private IPLUnit unit;
	private CTimeModel timeModel;

	JSplitPane split = new JSplitPane();
	private PPSourcePane sourcePane;
	private IPLUnit selectedUnit;
	private PPBlockPane blockPane;
	private JScrollPane blockPaneInSP;

	/**
	 * @param timeModel
	 */
	public PPSourceMetricsPane(IPLUnit unit, CTimeModel timeModel) {
		this.unit = unit;
		this.timeModel = timeModel;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		add(split);
		sourcePane = new PPSourcePane(new IPLFileProvider() {
			@Override
			public PLFile getFile(CTime time) {
				if (selectedUnit != null) {
					return selectedUnit.getFile(time);
				}
				return unit.getFile(time);
			}
		}, timeModel);

		blockPane = new PPBlockPane(new IPLFileProvider() {
			@Override
			public PLFile getFile(CTime time) {
				if (selectedUnit != null) {
					return selectedUnit.getFile(time);
				}
				return unit.getFile(time);
			}
		}, timeModel);
		FlowLayout layout = new FlowLayout();// blockpaneの配置
		layout.setAlignment(FlowLayout.LEFT);
		blockPane.setLayout(layout);

		timeModel.addModelListener(new ICModelChangeListener() {

			@Override
			public void modelUpdated(Object... args) {

				if (sourcePane.getCurrentTextEditLogTimestamp() >= blockPane.getCurrentImgStamp()) {
					split.setLeftComponent(sourcePane);
				} else {
					split.setLeftComponent(blockPaneInSP);
				}
			}
		});
		blockPaneInSP = new JScrollPane(blockPane);
		split.setLeftComponent(blockPaneInSP);

		PPUtilitiesPane utilitiesPane = new PPUtilitiesPane(timeModel, unit);
		split.setRightComponent(utilitiesPane);
		split.setResizeWeight(0.75);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				split.setDividerLocation(0.75);
			}
		});
	}

	public void setSelectedUnit(IPLUnit selectedUnit) {
		if (this.selectedUnit != selectedUnit) {
			this.selectedUnit = selectedUnit;
			sourcePane.refresh();
			blockPane.refresh();
		}
	}

}
