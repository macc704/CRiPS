/*
 * PPRunPointPanel.java
 * Created on 2011/06/22
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts;

import javax.swing.JButton;
import javax.swing.JPanel;

import pres.loader.logmodel.PLFileLog;
import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLUnit;
import pres.loader.model.PLProject;
import pres.loader.model.PLStampedProject;
import pres.loader.utils.PLLogSelecters;
import clib.common.compiler.CCompileResult;
import clib.common.execution.CExecutionFrame;
import clib.common.execution.CJavaCommandExecuter;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;
import clib.common.model.ICModelChangeListener;
import clib.common.thread.ICTask;
import clib.common.time.CTime;
import clib.view.actions.CActionUtils;
import clib.view.timeline.model.CTimeModel;

/**
 * @author macchan
 * 
 */
public class PPRunPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private CTimeModel timeModel;
	private IPLUnit unit;

	JButton runButton = new JButton(CActionUtils.createAction("Run",
			new ICTask() {
				public void doTask() {
					run();
				}
			}));

	/**
	 * @param timePane
	 * @param project
	 */
	public PPRunPane(CTimeModel timeModel, IPLUnit unit) {
		this.timeModel = timeModel;
		this.unit = unit;
		initialize();
	}

	private void initialize() {
		runButton.setEnabled(false);
		add(runButton);
		timeModel.addModelListener(new ICModelChangeListener() {
			public void modelUpdated(Object... args) {
				CTime time = timeModel.getTime();
				PLProject project = unit.getProject();
				CCompileResult result = project.getCompileResult(time);
				if (result != null && result.isSuccess()) {
					runButton.setEnabled(true);
				} else {
					runButton.setEnabled(false);
				}
			}
		});
	}

	void run() {
		CTime time = timeModel.getTime();
		PLProject project = unit.getProject();

		PLLog theLastRun = unit.getOrderedLogs().select(PLLogSelecters.RUN)
				.searchElementBefore(time);
		if (theLastRun == null) {
			throw new RuntimeException();
		}

		PLStampedProject stamp = project.getStampedProject(time);
		if (stamp == null) {
			throw new RuntimeException();
		}

		CCompileResult result = stamp.getCompileResult();
		if (result == null) {
			throw new RuntimeException();
		}

		CDirectory stampBase = stamp.getProjectBaseDirectory();
		CDirectory base = project.getProjectBaseDir();// 画像ファイルがここにあるので．

		CExecutionFrame execFrame = new CExecutionFrame();
		CJavaCommandExecuter executer = new CJavaCommandExecuter(
				execFrame.getExecuter(), base);
		executer.setBinDirPath(stampBase.findOrCreateDirectory(
				project.getSrcPath()).getAbsolutePath());
		executer.addClasspathDir(stamp.getLibDir().getAbsolutePath());
		CPath mainclassPath = ((PLFileLog) theLastRun).getPath()
				.getRelativePath(project.getSrcPath());
		executer.setMainClass(createFQCN(mainclassPath)); // ClassPathはtheLastRunのものにする

		execFrame.open();
		executer.execute();
	}

	public String createFQCN(CPath mainclassPath) {
		String clString = mainclassPath.toString();
		int index = clString.lastIndexOf(".");
		if (index >= 0) {
			clString = clString.substring(0, index);
		}
		// System.out.println(clString);
		clString = clString.replaceAll("/", ".");
		return clString;
	}
}
