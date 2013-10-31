/*
 * PPPresDataManager.java
 * Created on 2011/07/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import ppv.app.PPresVisualizer;
import ppv.app.datamanager.IPPVLoader;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPEclipsePPVLoader;
import ppv.app.datamanager.PPRonproPPVLoader;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileSystem;
import clib.common.utils.CDate;
import clib.view.app.javainfo.CJavaInfoPanels;
import clib.view.dnd.CFileDropInDataTransferHandler;
import clib.view.dnd.ICFileDroppedListener;
import clib.view.list.CDirectoryListModel;
import clib.view.list.CListPanel;

/**
 * @author macchan
 * 
 */
public class PPDataManagerFrame extends JFrame {

	public static final boolean AUTO_LOAD = false;
	public static final boolean AUTO_COMPILE = false;
	public static final boolean COCO_DATA = false;

	private static final long serialVersionUID = 1L;

	private PPDataManager manager;

	private CListPanel<CDirectory> listPanel = new CListPanel<CDirectory>();

	public PPDataManagerFrame(PPDataManager manager) {
		this.manager = manager;
		initialize();
		initializeMenu();
	}

	private void initialize() {
		setTitle("PresVisualizer - " + PPresVisualizer.VERSION);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		initializeProjectSets();
		listPanel.getJList().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		listPanel.getJList().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					CDirectory dir = listPanel.getSelectedElement();
					manager.openProjectSet(dir.getNameByString(), AUTO_LOAD,
							AUTO_COMPILE, COCO_DATA);
				}
				if (e.getButton() == MouseEvent.BUTTON3
						&& e.getClickCount() == 1) {
					JPopupMenu popup = createPopup(listPanel
							.getSelectedElement());
					popup.show(listPanel, e.getX(), e.getY());
				}
			}
		});
		getContentPane().add(new JScrollPane(listPanel));

		CFileDropInDataTransferHandler.set(this, new PPFileDroppedListener());
	}

	private void initializeProjectSets() {
		CDirectoryListModel model = new CDirectoryListModel(
				manager.getDataDir());
		listPanel.setModel(model);
		listPanel.refresh();
	}

	protected JPopupMenu createPopup(final CDirectory dir) {
		JPopupMenu popup = new JPopupMenu();

		{
			JMenuItem item = new JMenuItem("Delete");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					manager.deleteProjectSet(dir);
					initializeProjectSets();
				}
			});
			popup.add(item);
		}

		return popup;
	}

	private void initializeMenu() {
		setJMenuBar(new JMenuBar());

		{
			JMenu menu = new JMenu("Tools");
			getJMenuBar().add(menu);

			{
				JMenuItem item = new JMenuItem("ClearCash");
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						manager.clearCompileCash();
					}
				});
				menu.add(item);
			}
		}

		{
			JMenu menu = new JMenu("Info");
			getJMenuBar().add(menu);
			menu.add(CJavaInfoPanels.createJavaInformationAction());
		}
	}

	/*************************************************************
	 * Drag&Dropの処理
	 *************************************************************/

	private CDirectory getSelectedDir() {

		// CDirectory selected = listPanel.getSelectedElement();
		// if (selected != null) {
		// return selected;
		// } else {
		// return manager.getDataDir().findOrCreateDirectory("default");
		// }

		// 読み込まれたかどうかがわかりにくいので，変更
		return manager.getDataDir().findOrCreateDirectory(CDate.current());
	}

	class PPFileDroppedListener implements ICFileDroppedListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see clib.view.dnd.ICFileDroppedListener#fileDropped(java.util.List)
		 */
		@Override
		public void fileDropped(List<File> files) {
			IPPVLoader loader = chooseLoaderType();
			if (loader == null) {
				return;
			}

			for (File file : files) {
				CFileElement cfile = CFileSystem.convertToCFile(file);
				loadOneFile(cfile, loader);
			}

			listPanel.refresh();
		}

		private void loadOneFile(CFileElement file, IPPVLoader loader) {
			// 作りかけ
			// if (file.isFile() && "zip".equals(file.getName().getExtension()))
			// {
			// ZipFile zip = new ZipFile(file.toJavaFile());
			// }

			if (file.isDirectory()) {
				manager.loadDir((CDirectory) file, loader);
			} else {// is File
				manager.loadOneFile((CFile) file, getSelectedDir(), loader);
			}
		}

		private IPPVLoader chooseLoaderType() {
			JComboBox<IPPVLoader> combobox = new JComboBox<IPPVLoader>();
			combobox.addItem(new PPEclipsePPVLoader());
			combobox.addItem(new PPRonproPPVLoader());
			int res = JOptionPane.showConfirmDialog(PPDataManagerFrame.this,
					combobox, "Open", JOptionPane.OK_CANCEL_OPTION);
			if (res != JOptionPane.OK_OPTION) {
				return null;
			}
			IPPVLoader loader = (IPPVLoader) combobox.getSelectedItem();
			return loader;
		}

	}
}
