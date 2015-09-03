package ppv.view.parts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import ppv.app.taskdesigner.timeline.PPTaskTimeLineView;
import ppv.app.taskdesigner.timeline.PPTaskUnit;
import ppv.view.frames.PPProjectViewerFrame;
import ppv.view.parts.timelineview.PPCompositeTimeLineView;
import pres.loader.model.IPLUnit;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CPath;
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
		// namePanel.addMouseListener(new MouseAdapter() {
		// @Override
		// public void mouseClicked(MouseEvent e) {
		// System.out.println("Hello World!");
		// }
		// });

		final JLabel label = new JLabel(model.getName());
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

		// pulling panel と rename panel を置くパネル
		final JPanel emptyPanel = new JPanel();
		emptyPanel.setBackground(Color.WHITE);
		emptyPanel.setPreferredSize(new Dimension(50, 20));

		// pulling panel
		JPanel pullingPanel = new JPanel();
		pullingPanel.setBackground(Color.WHITE);
		pullingPanel.setLayout(new BorderLayout());
		pullingPanel.add(new JLabel("+", SwingConstants.CENTER));
		pullingPanel.setPreferredSize(new Dimension(20, 20));
		MouseAdapter l = createDragMouseListener(model);
		pullingPanel.addMouseListener(l);
		pullingPanel.addMouseMotionListener(l);
		emptyPanel.add(pullingPanel, BorderLayout.WEST);
		// panel.add(pullingPanel, BorderLayout.WEST);

		// rename panel
		JPanel renamePanel = new JPanel();
		renamePanel.setBackground(Color.WHITE);
		renamePanel.setLayout(new BorderLayout());
		renamePanel.add(new JLabel("◯", SwingConstants.CENTER));
		renamePanel.setPreferredSize(new Dimension(20, 20));
		renamePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getButton() == 3) {
					final JPopupMenu renameMenu = new JPopupMenu();
					// renameMenu.add("rename");
					final JMenuItem menuItem = new JMenuItem("Rename");
					menuItem.setLocation(e.getPoint());

					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							// System.out.println("Hell World!");
							String newLabel = showRenameMenu(menuItem
									.getLocation());
							if (newLabel != null) {
								try {
									CPath oldPath = model.getPath();
									String newName = newLabel;
									CPath newPath = oldPath.getParentPath()
											.appendedPath(newName);

									System.out.println(oldPath);
									System.out.println(newPath);
									System.out.println(newName);

									// projectの名前を書き換える ファイルアクセス rename to
									CDirectory basedir = model.getProject()
											.getProjectBaseDir();

									CDirectory pres2Dir = basedir
											.findDirectory(new CPath(".pres2"));
									// 名前既存のディレクトリに同名ファイルがあるかどうかの確認
									if (isExistingName(pres2Dir, newName)) {
										// 存在するため、ポップアップを表示し、書き換えるかどうかを聞く
										mergeFile(oldPath, newPath, basedir, pres2Dir, newName);

									} else {
										//既存ディレクトリに同名ファイルが存在しないため、マージしないで処理
										// head file
										CFile file = basedir.findFile(oldPath);
										file.renameTo(newName);

										// recorded directory
										CDirectory recordingDir = pres2Dir
												.findDirectory(oldPath);
										recordingDir.copyTo(newName);
										recordingDir = pres2Dir
												.findDirectory(oldPath);
										boolean b = recordingDir.delete();
										System.out.println(b);

										// pres.logの書き換え
										CFile logfile = pres2Dir
												.findFile("pres2.log");
										String oldKey = "\t"
												+ oldPath.toString();
										String newKey = "\t"
												+ newPath.toString();
										List<String> lines = logfile
												.loadTextAsList();
										List<String> newLines = new ArrayList<String>();
										for (String line : lines) {
											String newLine = line.replace(
													oldKey, newKey);
											newLines.add(newLine);
										}
										CFile newlogfile = pres2Dir
												.findOrCreateFile("tmp.log");
										newlogfile.saveTextFromList(newLines);
										logfile.delete();
										newlogfile.copyTo("pres2.log");
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}

								// CPath newPath = oldPath.getParentPath().a
								//
								// CFile file = CFileSystem.(oldPath) ;
								//
								// file.renameTo(newLabel);
								//
								// System.out.println(dir.getDirectoryChildren()
								// .get(0).findDirectory(oldLabel));

								// CPath newPath = dir
								// .getAbsolutePath()
								// .appendedPath(
								// model.getPath().getParentPath()
								// .appendedPath(newLabel));
								// System.out.println(newPath.toString());

								// System.out.println(dir.getAbsolutePath());

								// 中身の書き換え処理みたいのがいる
							}
						}
					});

					renameMenu.add(menuItem);

					renameMenu.show(
							emptyPanel.getComponentAt(e.getX(), e.getY()),
							e.getX(), e.getY());

					// renameMenu.addMouseListener(new MouseAdapter() {
					// @Override
					// public void mouseClicked(MouseEvent e) {
					// // super.mouseClicked(e);
					// System.out.println("Hello World!");
					// }
					// });
				}
			}
		});
		emptyPanel.add(renamePanel, BorderLayout.EAST);

		panel.add(emptyPanel, BorderLayout.WEST);

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

	private boolean isExistingName(CDirectory pres2Dir, String newName) {
		for (CFileElement file : pres2Dir.getChildren()) {
			if (file.getNameByString().equals(newName)) {
				// popupの表示
				return true;
			}
		}
		return false;
	}
	
	private void mergeFile(CPath oldPath, CPath newPath, CDirectory basedir, CDirectory pres2Dir, String newName){
		//ダイアログ表示
		int num = JOptionPane.showConfirmDialog(null, "ファイルをマージしますか？");
		System.out.println("num::"  + num);
		if(num==0){			
			// recorded directory
			CDirectory recordingDir = pres2Dir
					.findDirectory(oldPath);
			recordingDir.copyTo(newName);
			recordingDir = pres2Dir
					.findDirectory(oldPath);
			boolean b = recordingDir.delete();
			System.out.println(b);

			// pres.logの書き換え
			CFile logfile = pres2Dir
					.findFile("pres2.log");
			String oldKey = "\t"
					+ oldPath.toString();
			String newKey = "\t"
					+ newPath.toString();
			List<String> lines = logfile
					.loadTextAsList();
			List<String> newLines = new ArrayList<String>();
			for (String line : lines) {
				String newLine = line.replace(
						oldKey, newKey);
				newLines.add(newLine);
			}
			CFile newlogfile = pres2Dir
					.findOrCreateFile("tmp.log");
			newlogfile.saveTextFromList(newLines);
			logfile.delete();
			newlogfile.copyTo("pres2.log");
		}
	}

	private String showRenameMenu(Point point) {
		String result = JOptionPane.showInputDialog(null, "ファイル名を入力してください。",
				"ファイル名の変更", JOptionPane.OK_CANCEL_OPTION);
		return result;

		// JFrame frame = new JFrame();
		// frame.setTitle("まいたきゃわわ");
		// frame.setBounds(point.x, point.y, 300, 400);
		// frame.setVisible(true);
	}
}
