/*
 * PPMetricsPane.java
 * Created on 2011/06/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import pres.loader.model.IPLUnit;
import pres.loader.utils.PLLogSelecters;
import clib.view.panels.CVerticalFlowLayout;
import clib.view.timeline.model.CTimeModel;

/**
 * @author macchan
 */
public class PPUtilitiesPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private CTimeModel timeModel;
	private IPLUnit unit;

	/**
	 * @param timePane
	 * @param project
	 */
	public PPUtilitiesPane(CTimeModel timeModel, IPLUnit unit) {
		this.timeModel = timeModel;
		this.unit = unit;
		initialize();
	}

	public void initialize() {
		// setLayout(new CVerticalFlowLayout());
		setLayout(new BorderLayout());
		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitter.setResizeWeight(0.5);
		add(splitter);

		// LeftComponent
		JPanel northPanel = new JPanel();
		splitter.setLeftComponent(new JScrollPane(northPanel));
		northPanel.setLayout(new CVerticalFlowLayout());

		// PPCheckPointPane allEventPane = new PPCheckPointPane(timeModel, unit,
		// PLLogSelecters.ALL);
		// allEventPane.setBorder(BorderFactory
		// .createTitledBorder("AllEventPoint"));
		// allEventPane.setCurrentComponent(new PPRunPane(timeModel, unit));
		// northPanel.add(allEventPane);

		// PPCheckPointPane eventPane = new PPCheckPointPane(timeModel, unit,
		// PLLogSelecters.COMPILE_RUN);
		// eventPane.setBorder(BorderFactory.createTitledBorder("EventPoint"));
		// eventPane.setCurrentComponent(new PPRunPane(timeModel, unit));
		// northPanel.add(eventPane);

		PPCheckPointPane runpointPane = new PPCheckPointPane(timeModel, unit,
				"START_RUN");
		runpointPane.setBorder(BorderFactory.createTitledBorder("RunPoint"));
		northPanel.add(runpointPane);
		runpointPane.setCurrentComponent(new PPRunPane(timeModel, unit));

		PPCheckPointPane compilepointPane = new PPCheckPointPane(timeModel,
				unit, "COMPILE");
		compilepointPane.setBorder(BorderFactory
				.createTitledBorder("CompilePoint"));
		northPanel.add(compilepointPane);

		PPCheckPointPane editPointPane = new PPCheckPointPane(timeModel, unit,
				PLLogSelecters.TEXTEDIT);
		editPointPane.setBorder(BorderFactory.createTitledBorder("EditPoint"));
		northPanel.add(editPointPane);

		// RightComponent
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new CVerticalFlowLayout());
		splitter.setRightComponent(new JScrollPane(southPanel));

		PPMetricsPane metricsPane = (new PPMetricsPane(timeModel, unit));
		metricsPane.setBorder(BorderFactory.createTitledBorder("Metrics"));
		metricsPane.setPreferredSize(new Dimension(50, 320));
		southPanel.add(metricsPane);

		PPCompileResultPane compileResultPane = new PPCompileResultPane(
				timeModel, unit);
		compileResultPane.setBorder(BorderFactory
				.createTitledBorder("CompileResult"));
		compileResultPane.setPreferredSize(new Dimension(50, 150));
		southPanel.add(new JScrollPane(compileResultPane));

	}
}
