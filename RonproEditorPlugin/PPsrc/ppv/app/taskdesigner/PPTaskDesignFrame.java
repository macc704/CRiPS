/*
 * PPTaskDesignFrame.java
 * Created on 2012/05/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import clib.common.thread.ICTask;
import clib.view.actions.CAction;
import clib.view.actions.CActionUtils;

/**
 * @author macchan
 * 
 */
public class PPTaskDesignFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private PPTaskDesignController controller;

	private JPanel mainPanel = new JPanel();
	private JSplitPane split = new JSplitPane();
	private JSplitPane split2 = new JSplitPane();

	/**
	 * 
	 */
	public PPTaskDesignFrame(PPTaskDesignController controller) {
		this.controller = controller;
		initialize();
		initializeEstimatedTaskPanel();// 順序に意味あり
		initializeActualTaskPanel();// 順序に意味あり
		initializeDefectFixingTaskPanel();
		split.setResizeWeight(0.33);
		split.setDividerLocation(0.33);
		split2.setResizeWeight(0.5);
		split2.setDividerLocation(0.5);
		initializeMenuBar();
	}

	private void initialize() {
		setTitle("Task Viewer - " + controller.getUnit().getName());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// setAlwaysOnTop(true);

		getContentPane().setLayout(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());
		getContentPane().add(mainPanel);
		mainPanel.add(split);
		split.setRightComponent(split2);
	}

	private void initializeEstimatedTaskPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("見積"));
		panel.add(controller.getEstimatedTaskTable());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton button = new JButton();
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setPreferredSize(new Dimension(25, 25));
		buttonPanel.add(button);
		button.setAction(controller.getEstimatedTaskTable().addAction);
		button.setText("+");
		panel.add(buttonPanel, BorderLayout.SOUTH);

		// split.setRightComponent(panel);
		split.setLeftComponent(panel);
	}

	private void initializeActualTaskPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("実績"));
		panel.add(controller.getActualTaskTable());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton button = new JButton();
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setPreferredSize(new Dimension(25, 25));
		buttonPanel.add(button);
		button.setAction(controller.getActualTaskTable().addAction);
		button.setText("+");
		panel.add(buttonPanel, BorderLayout.SOUTH);

		// split.setRightComponent(panel);
		split2.setLeftComponent(panel);
	}

	private void initializeDefectFixingTaskPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("欠陥修正"));
		panel.add(controller.getDefectFixingTaskTable());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton button = new JButton();
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setPreferredSize(new Dimension(25, 25));
		buttonPanel.add(button);
		button.setAction(controller.getDefectFixingTaskTable().addAction);
		button.setText("+");
		panel.add(buttonPanel, BorderLayout.SOUTH);

		split2.setRightComponent(panel);
	}

	private void initializeMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		{
			JMenu menu = new JMenu("File");
			menuBar.add(menu);
			{
				CAction action = CActionUtils.createAction("Save",
						new ICTask() {
							public void doTask() {
								controller.doSave();
							}
						});
				action.setAcceralator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
						KeyEvent.CTRL_MASK));
				menu.add(action);
			}
			{
				CAction action = CActionUtils.createAction("Save As",
						new ICTask() {
							public void doTask() {
								controller.doSaveAs();
							}
						});
				menu.add(action);
			}
			{
				CAction action = CActionUtils.createAction("Load",
						new ICTask() {
							public void doTask() {
								controller.doLoad();
							}
						});
				action.setAcceralator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
						KeyEvent.CTRL_MASK));
				menu.add(action);
			}
			{
				CAction action = CActionUtils.createAction("Export",
						new ICTask() {
							public void doTask() {
								controller.doExport();
							}
						});
				action.setAcceralator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
						KeyEvent.CTRL_MASK));
				menu.add(action);
			}
		}
	}

}
