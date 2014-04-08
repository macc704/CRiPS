/*
 * PPTaskDesignFrame.java
 * Created on 2012/05/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import ppv.app.datamanager.PPDataManager;
import ppv.app.taskdesigner.controller.PPActualTaskEditorDescripter;
import ppv.app.taskdesigner.controller.PPDefectFixingTaskEditorDescripter;
import ppv.app.taskdesigner.controller.PPEstimatedTaskEditorDescripter;
import ppv.app.taskdesigner.model.PPActualTask;
import ppv.app.taskdesigner.model.PPDefectFixingTask;
import ppv.app.taskdesigner.model.PPEstimatedTask;
import ppv.app.taskdesigner.model.PPTaskDesignModel;
import pres.loader.model.IPLUnit;
import pres.loader.model.PLFile;
import clib.common.collections.CObservableList;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CFilename;
import clib.common.io.CIOUtils;
import clib.common.table.CCSVFileIO;
import clib.common.thread.ICTask;
import clib.common.time.CTime;
import clib.view.actions.CAction;
import clib.view.actions.CActionUtils;
import clib.view.dialogs.CErrorDialog;
import clib.view.timeline.model.CTimeModel;
import cswing.table.view.CElementEditableTablePanel;
import cswing.table.view.ICElementEditableTableListener;

/**
 * @author macchan
 * 
 */
public class PPTaskDesignController {

	private static final FileNameExtensionFilter filter = new FileNameExtensionFilter(
			"ppv形式", "ppv");

	private CTimeModel model1;
	private CTimeModel model2;

	private IPLUnit unit;

	private CElementEditableTablePanel<PPActualTask> actualTaskTable;
	private CElementEditableTablePanel<PPDefectFixingTask> defectFixingTaskTable;
	// private CObservableList<PPDefectFixingTask> defectFixingTasks = new
	// CObservableList<PPDefectFixingTask>();
	private CElementEditableTablePanel<PPEstimatedTask> estimatedTaskTable;

	private File file;

	private JFrame frame;

	/**
	 * 
	 */
	public PPTaskDesignController(IPLUnit unit, CTimeModel model1,
			CTimeModel model2) {
		this.unit = unit;
		this.model1 = model1;
		this.model2 = model2;

		initializeCETaskPanel();// 順序に意味あり
		initializeTaskPanel();// 順序に意味あり
		initializeDefectFixingPanel();
	}

	private void initializeDefectFixingPanel() {
		PPDefectFixingTaskEditorDescripter descripter = new PPDefectFixingTaskEditorDescripter(
				this);
		defectFixingTaskTable = new CElementEditableTablePanel<PPDefectFixingTask>(
				descripter);
		defectFixingTaskTable.setLabelwidth(75);
		defectFixingTaskTable.setTextlen(30);
	}

	private void initializeCETaskPanel() {
		PPEstimatedTaskEditorDescripter descripter = new PPEstimatedTaskEditorDescripter();
		estimatedTaskTable = new CElementEditableTablePanel<PPEstimatedTask>(
				descripter);
		estimatedTaskTable
				.addElementEditableTableListener(new ICElementEditableTableListener<PPEstimatedTask>() {
					@Override
					public void elementRemoved(PPEstimatedTask cetask) {
						for (PPActualTask task : actualTaskTable.getModels()) {
							if (task.getCeTask() == cetask) {
								task.setCeTask(null);
								actualTaskTable.getTableModel()
										.fireTableDataChanged();
							}
						}
					}

					@Override
					public void elementAdded(PPEstimatedTask object) {
					}
				});
		estimatedTaskTable.setLabelwidth(75);
		estimatedTaskTable.setTextlen(30);
	}

	private void initializeTaskPanel() {
		PPActualTaskEditorDescripter metamodel = new PPActualTaskEditorDescripter(
				this, estimatedTaskTable.getModels());
		actualTaskTable = new CElementEditableTablePanel<PPActualTask>(
				metamodel) {

			private static final long serialVersionUID = 1L;

			protected JPopupMenu createPopup() {
				JPopupMenu menu = new JPopupMenu();
				{
					int count = getSelectedCount();
					if (count == 1) {
						menu.add(modifyAction);
						menu.add(showRangeInViewAction);
						menu.add(resetTimeAction);
					}
					if (count > 0) {
						menu.add(removeAction);
					}
					menu.add(addAction);
				}
				return menu;
			}
		};
		actualTaskTable.setLabelwidth(75);
		actualTaskTable.setTextlen(30);
	}

	public CAction showRangeInViewAction = CActionUtils.createAction(
			"ShowRangeInView", new ICTask() {
				public void doTask() {
					doSet();
				}
			});

	public CAction resetTimeAction = CActionUtils.createAction("ResetTime",
			new ICTask() {
				public void doTask() {
					doReset();
				}
			});

	/**
	 * @param frame
	 *            the frame to set
	 */
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	protected void doSet() {
		List<PPActualTask> tasks = actualTaskTable.getSelectedModels();
		if (tasks.size() != 1) {
			throw new RuntimeException();
		}
		PPActualTask task = tasks.get(0);
		model2.setTime(task.getStart());
		model1.setTime(task.getEnd());
	}

	protected void doReset() {
		CTime start = model2.getTime();
		CTime end = model1.getTime();
		if (end.before(start)) {
			JOptionPane.showMessageDialog(frame, "終了（赤線）が開始（青線）の前にあります．",
					"エラー", JOptionPane.ERROR_MESSAGE);
			return;
		}

		List<PPActualTask> tasks = actualTaskTable.getSelectedModels();
		if (tasks.size() != 1) {
			throw new RuntimeException();
		}
		PPActualTask task = tasks.get(0);
		task.setStart(start);
		task.setEnd(end);

	}

	public PPActualTask createTask() {
		CTime startT = model2.getTime();
		CTime endT = model1.getTime();
		if (endT.before(startT)) {
			JOptionPane.showMessageDialog(frame, "終了（赤線）が開始（青線）の前にあります．",
					"エラー", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		PPActualTask task = new PPActualTask("", startT, endT);
		return task;
	}

	public void doAddActualTask() {
		actualTaskTable.doAdd();
	}

	public void doSave() {
		try {
			if (file == null) {
				doSaveAs();
				return;
			}

			saveInternal(file);

		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, ex);
		}
	}

	public void doSaveAs() {
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(filter);
			chooser.showSaveDialog(frame);
			File selected = chooser.getSelectedFile();
			if (selected != null) {
				String path = selected.getAbsolutePath();
				if (!path.endsWith(".ppv")) {
					path += ".ppv";
					selected = new File(path);
				}
				saveInternal(selected);
				this.file = selected;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, ex);
		}
	}

	protected void doLoad() {
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(filter);
			chooser.showOpenDialog(frame);
			File selected = chooser.getSelectedFile();
			if (selected != null) {
				loadInternal(selected);
				this.file = selected;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, ex);
		}
	}

	private void saveInternal(File file) throws Exception {
		PPTaskDesignModel manager = new PPTaskDesignModel(
				actualTaskTable.getModels(), estimatedTaskTable.getModels(),
				defectFixingTaskTable.getModels());
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				file));
		oos.writeObject(manager);
		oos.close();
	}

	private void loadInternal(File file) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		PPTaskDesignModel manager = (PPTaskDesignModel) ois.readObject();
		ois.close();
		actualTaskTable.setModels(manager.getActualTasks());
		estimatedTaskTable.setModels(manager.getEstimatedTasks());
		defectFixingTaskTable.setModels(manager.getDefectFixingTasks());
	}

	protected void doExport() {
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.showSaveDialog(frame);
			File f = chooser.getSelectedFile();
			if (f != null) {
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}

				CDirectory createdDir = exportToDir();

				CDirectory outDir = CFileSystem.findDirectory(f.getParentFile()
						.getAbsolutePath());
				CFilename name = new CFilename(f.getName());
				name.setExtension("zip");
				CFile outFile = outDir.findOrCreateFile(name);

				CIOUtils.zip(createdDir, outFile);
				createdDir.delete();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, ex);
		}
	}

	private CDirectory exportToDir() throws Exception {
		// create dir
		CDirectory tmpdir = PPDataManager.getCurrent().getTMPDir();
		String tmpname = Long.toString(new Date().getTime());
		CDirectory dir = tmpdir.findOrCreateDirectory(tmpname);
		dir.deleteInside();

		PPTaskDesignModel manager = new PPTaskDesignModel(
				actualTaskTable.getModels(), estimatedTaskTable.getModels(),
				defectFixingTaskTable.getModels());
		PPDataExporter exporter = new PPDataExporter(manager, unit);

		// data.ppv
		CFile ppv = dir.findOrCreateFile("data.ppv");
		saveInternal(ppv.toJavaFile());

		// data.csv
		CFile fileActual = dir.findOrCreateFile("data.csv");
		CCSVFileIO.save(exporter.createActual(), fileActual);

		// estact.csv
		CFile fileCostEffectiveness = dir.findOrCreateFile("estact.csv");
		CCSVFileIO.save(exporter.createCostEffectiveness(),
				fileCostEffectiveness);

		// defect.csv
		CFile fileDefect = dir.findOrCreateFile("defect.csv");
		CCSVFileIO.save(exporter.createDefect(), fileDefect);

		// source
		PLFile plFile = unit.getFile(unit.getEnd());
		String pjname = plFile.getName();
		String source = plFile.getSource(unit.getEnd());
		CFile sourceFile = dir.findOrCreateFile(pjname);
		sourceFile.saveText(source);

		CFile fileHtml = dir.findOrCreateFile("index.html");
		fileHtml.saveText(createHtml(pjname, sourceFile, fileActual,
				fileCostEffectiveness, fileDefect));

		return dir;
	}

	private String createHtml(String name, CFile sourceFile, CFile actualCSV,
			CFile costEffectivenessCSV, CFile defectCSV) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append("<head><title>" + name + "</title></head>");
		buf.append("<body>");
		buf.append("<h1>予実サマリー</h1>");
		buf.append(csvToHtml(costEffectivenessCSV));
		buf.append("<h1>プロセス詳細</h1>");
		buf.append(csvToHtml(actualCSV));
		buf.append("<h1>欠陥記録</h1>");
		buf.append(csvToHtml(defectCSV));
		buf.append("<h1>最終成果物</h1>");
		buf.append("<pre>");
		buf.append(sourceFile.loadText());
		buf.append("</pre>");
		buf.append("</body>");
		buf.append("</html>");
		return buf.toString();
	}

	private String csvToHtml(CFile file) {
		StringBuffer buf = new StringBuffer();
		buf.append("<table>");
		String[][] whole = CCSVFileIO.load(file);
		for (String[] row : whole) {
			buf.append("<tr>");
			for (String data : row) {
				buf.append("<td>" + data + "</td>");
			}
			buf.append("</tr>");
		}
		buf.append("</table>");
		return buf.toString();
	}

	/**
	 * @return the taskTable
	 */
	public CElementEditableTablePanel<PPActualTask> getActualTaskTable() {
		return actualTaskTable;
	}

	/**
	 * @return the ceTaskTable
	 */
	public CElementEditableTablePanel<PPEstimatedTask> getEstimatedTaskTable() {
		return estimatedTaskTable;
	}

	/**
	 * @return the defectFixingTaskTable
	 */
	public CElementEditableTablePanel<PPDefectFixingTask> getDefectFixingTaskTable() {
		return defectFixingTaskTable;
	}

	/**
	 * @return the unit
	 */
	public IPLUnit getUnit() {
		return unit;
	}

	/**
	 * @return
	 */
	public List<PPActualTask> getActualTasks() {
		List<PPActualTask> tasks = new ArrayList<PPActualTask>(
				getActualTaskTable().getModels());
		Collections.sort(tasks, new Comparator<PPActualTask>() {
			@Override
			public int compare(PPActualTask a, PPActualTask b) {
				return (int) (a.getStart().getAsLong() - b.getStart()
						.getAsLong());
			}
		});
		return tasks;
	}

	/**
	 * @return the defectFixingTasks
	 */
	public CObservableList<PPDefectFixingTask> getDefectFixingTasks() {
		CObservableList<PPDefectFixingTask> tasks = new CObservableList<PPDefectFixingTask>(
				getDefectFixingTaskTable().getModels());
		Collections.sort(tasks, new Comparator<PPDefectFixingTask>() {
			@Override
			public int compare(PPDefectFixingTask a, PPDefectFixingTask b) {
				return (int) (a.getStart().getAsLong() - b.getStart()
						.getAsLong());
			}
		});
		return tasks;
	}

	/**
	 * 
	 */
	public void doAddDefectFixingTask() {
		defectFixingTaskTable.doAdd();
	}

	/**
	 * @param task
	 */
	public void doRemoveDefectFixingTask(PPDefectFixingTask task) {
		defectFixingTaskTable.doRemove(task);
	}

	/**
	 * @return
	 */
	public PPDefectFixingTask createDefectFixingTask() {
		CTime startT = model2.getTime();
		CTime endT = model1.getTime();
		if (endT.before(startT)) {
			JOptionPane.showMessageDialog(frame, "終了（赤線）が開始（青線）の前にあります．",
					"エラー", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		PPDefectFixingTask task = new PPDefectFixingTask("", startT, endT);
		return task;
	}
}
