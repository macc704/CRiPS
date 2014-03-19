/*
 * PPProjectViewer.java
 * Created on 2011/06/08
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.frames;

import generef.analytics.FailureKnowledgeListFile;
import generef.analytics.FailureKnowledgeRepositoryAnalyzer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import ppv.analytics.metrics.PPMetricsPrinter;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import ppv.view.parts.PPTimeLinePane;
import pres.loader.model.PLProject;
import tea.analytics.CompileErrorAnalyzerList;
import tea.analytics.CompileErrorListFile;
import tea.analytics.model.TCompilePoint;
import clib.common.filesystem.CDirectory;
import clib.common.thread.ICTask;
import clib.view.dialogs.CErrorDialog;
import clib.view.progress.CPanelProcessingMonitor;

/**
 * @author macchan
 */
public class PPProjectSetViewerFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private PPProjectSet projectSet;
	private PPTimeLinePane pane = new PPTimeLinePane();

	public PPProjectSetViewerFrame(PPProjectSet projectSet) {
		this.projectSet = projectSet;
		initialize();
		initializeMenu();
	}

	private void initialize() {
		setTitle("ProjectSetViewer - " + projectSet.getName());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pane.getTimelinePane().getTimeTransModel()
				.setRange(projectSet.getRange());
		pane.getTimelinePane().createDefaultButtons();
		setLayout(new BorderLayout());
		getContentPane().add(pane);
		load(false);
	}

	public void load(boolean drawRightSide) {
		pane.removeAllModels();
		pane.setDrawRightSide(drawRightSide);
		for (PLProject project : projectSet.getProjects()) {
			pane.addModel(project.getRootPackage());
		}
	}

	public void fitScale() {
		pane.getTimelinePane().fitScale();
	}

	private void initializeMenu() {
		setJMenuBar(new JMenuBar());

		{
			JMenu menu = new JMenu("View");
			getJMenuBar().add(menu);

			{
				JMenuItem item = new JMenuItem("Show list with Timeline");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						load(true);
						fitScale();
					}
				});
				menu.add(item);
			}

			{
				JMenuItem item = new JMenuItem("Show list without Timeline");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						load(false);
						fitScale();
					}
				});
				menu.add(item);
			}
		}

		{
			JMenu menu = new JMenu("Operation");
			getJMenuBar().add(menu);

			{
				JMenuItem item = new JMenuItem("Load All");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadProjectSet();
					}
				});
				menu.add(item);
			}

			{
				JMenuItem item = new JMenuItem("Compile All");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						compileProjectSet();
					}
				});
				menu.add(item);
			}
		}

		{
			JMenu menu = new JMenu("Analytics");
			getJMenuBar().add(menu);

			{
				JMenuItem item = new JMenuItem("Metrics");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						printMetrics();
					}
				});
				menu.add(item);
			}

			{
				JMenuItem item = new JMenuItem("CompileError Analysis");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						printCompileErrorAnalysis(
								new File("./CompileError.csv"), false);
					}
				});
				menu.add(item);
			}

			{
				JMenuItem item = new JMenuItem(
						"CompileError Analysis for CocoViewer");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						printCompileErrorAnalysis(new File(
								"./CCCompileError.csv"), true);
					}
				});
				menu.add(item);
			}

			{
				JMenuItem item = new JMenuItem("FailureKnowledgeList");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						printFailureKnowledgeList();
					}
				});
				menu.add(item);
			}
		}
	}

	private CPanelProcessingMonitor monitor = new CPanelProcessingMonitor();

	private void loadProjectSet() {
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					PPDataManager.getCurrent().loadAllProjects(projectSet,
							monitor);
					pane.getTimelinePane().getTimeTransModel()
							.setRange(projectSet.getRange());
				} catch (Exception ex) {
					ex.printStackTrace();
					CErrorDialog.show(null, "", ex);
				}
			}
		});
	}

	private void compileProjectSet() {
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					PPDataManager.getCurrent().createCompileCash(projectSet,
							monitor);
				} catch (Exception ex) {
					ex.printStackTrace();
					CErrorDialog.show(null, "", ex);
				}
			}
		});
	}

	private void printMetrics() {
		monitor.doTaskWithDialog(new ICTask() {
			public void doTask() {
				try {
					PPMetricsPrinter printer = new PPMetricsPrinter();
					// printer.printMetrics(projectSet, System.out);
					FileOutputStream out = new FileOutputStream(new File(
							"./FileMetrics.csv"));
					printer.printMetrics(projectSet, out, monitor);
				} catch (Exception ex) {
					ex.printStackTrace();
					CErrorDialog.show(null, "", ex);
				}
			}
		});
	}

	// for CocoViewer by hirao
	public void doPrintCompileErrorCSV(CDirectory baseDir) {
		printCompileErrorAnalysis(new File(baseDir.getAbsolutePath().toString()
				+ "/CompileError.csv"), true);
	}

	private void printCompileErrorAnalysis(File outfile, boolean coco) {
		// CompileErrorAnalysis
		List<CompileErrorAnalyzerList> analyzers = new ArrayList<CompileErrorAnalyzerList>();
		for (PLProject project : projectSet.getProjects()) {
			CompileErrorAnalyzerList analyzer = new CompileErrorAnalyzerList(
					project);
			analyzer.analyze();
			analyzers.add(analyzer);
			writePattern(analyzer.getCompilePoints());
		}

		// FileOutput
		CompileErrorListFile file = new CompileErrorListFile(analyzers);

		try {
			file.outputErrorList(outfile, coco);
			if (!coco) {
				file.outputPatternList();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void printFailureKnowledgeList() {
		List<FailureKnowledgeRepositoryAnalyzer> analyzers = new ArrayList<FailureKnowledgeRepositoryAnalyzer>();

		// create analyzers
		for (PLProject project : projectSet.getProjects()) {
			analyzers.add(new FailureKnowledgeRepositoryAnalyzer(project));
		}

		FailureKnowledgeListFile listFile = new FailureKnowledgeListFile();
		listFile.output(analyzers);
	}

	private void writePattern(List<TCompilePoint> compilePoints) {
		int[] num = new int[9];
		for (TCompilePoint compilePoint : compilePoints) {

			if (compilePoint.getPattern().size() > 0) {
				System.out.print(compilePoint.getTime() + "\t");
				for (int pattern : compilePoint.getPattern()) {
					String str;
					switch (pattern) {
					case 1:
						str = "1";
						break;
					case 2:
						str = "2-A";
						break;
					case 3:
						str = "2-B";
						break;
					case 4:
						str = "3-A";
						break;
					case 5:
						str = "3-B";
						break;
					case 6:
						str = "4";
						break;
					case 7:
						str = "5";
						break;
					default:
						str = "";
					}
					System.out.print(" " + str);
					num[pattern]++;
				}
				System.out.println();
			}

		}

		System.out.println("--");
		// for (int i = 0; i < num.length; i++) {
		// System.out.println(i + ":" + num[i]);
		// }
	}
}
