package ch.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.library.CHFileSystem;
import ch.view.CHFileTree.FileTreeNode;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CPath;
import clib.view.windowmanager.CWindowCentraizer;

/**
 * 取り込みファイルの選択View
 * @author Yuya Kato
 *
 */
public class CHFileChooser extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CDirectory userRootDir;
	private CDirectory memberRootDir;
	
	private CHFileTree userTree;
	private CHFileTree memberTree;
	
	private String member;
	
	private CFileFilter acceptFilter;

	public CHFileChooser(CDirectory userRootDir, CDirectory memberRootDir) {
		this.userRootDir = userRootDir;
		this.memberRootDir = memberRootDir;
		member = memberRootDir.getParentDirectory().getNameByString();
	}
	
	public void createView() {
	    
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 250);
		setModal(true);
		setResizable(false);
		CWindowCentraizer.centerWindow(this);
		
		userTree = new CHFileTree(userRootDir);
		memberTree = new CHFileTree(memberRootDir);	
		userTree.initialize();
		memberTree.initialize();
	    
	    getContentPane().add(new JLabel("取り込むファイルを選択"), BorderLayout.NORTH);
	    getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
	    getContentPane().add(createBottomPanel(), BorderLayout.PAGE_END);
	}
	
	/**
	 * treeを乗せるスクロールパネル
	 * @param tree
	 * @return
	 */
	private JScrollPane createScrollPane(CHFileTree tree) {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(tree);
		scrollPane.setPreferredSize(new Dimension(180, 120));
		return scrollPane;
	}
	
	/**
	 * ツリーを乗せたスクロールパネルを乗せるパネル
	 * @param tree
	 * @param label
	 * @return
	 */
	private JPanel createFileTreePanel(CHFileTree tree, JLabel label) {
	    JPanel panel = new JPanel();
	    panel.setPreferredSize(new Dimension(200, 150));
	    panel.add(label, BorderLayout.NORTH);
	    panel.add(createScrollPane(tree), BorderLayout.CENTER);
	    return panel;
	}
	
	/**
	 * ユーザとメンバーのファイルリストと移動ボタンを含むパネル
	 * @return
	 */
	private JPanel createCenterPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(80, 70));
		buttonPanel.add(createSingleImportButton());
		buttonPanel.add(createAllImportButton());
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(450, 200));
		panel.add(createFileTreePanel(userTree, new JLabel("あなた")), BorderLayout.WEST);
		panel.add(buttonPanel, BorderLayout.CENTER);
		panel.add(createFileTreePanel(memberTree, 
				new JLabel(member)), BorderLayout.EAST);
		
		return panel;
	}
	
	/**
	 * 選択している一つのファイルをユーザに移動するボタン
	 * @return
	 */
	private JButton createSingleImportButton() {
		JButton button = new JButton("<");
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FileTreeNode selectedNode = (FileTreeNode) memberTree.getLastSelectedPathComponent();
				if (selectedNode != null) {
					userTree.insertNode(selectedNode);
				}
			}
		});
		
		return button;
	}
	
	/**
	 * 全てのファイルをユーザに移動するボタン
	 * @return
	 */
	private JButton createAllImportButton() {
		JButton button = new JButton("<<");
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < memberTree.getRoot().getChildCount(); i++) {
					userTree.insertNode((FileTreeNode) memberTree.getRoot().getChildAt(i));
				}
			}
		});
		
		return button;
	}
	
	/**
	 * OK，キャンセル，リセットボタンを含むパネル
	 * @return
	 */
	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		panel.add(createResetButton());
		panel.add(createCancelButton());
		panel.add(createOKButton());
		return panel;
	}
	
	/**
	 * OKボタン
	 * @return
	 */
	private JButton createOKButton() {
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (!userTree.getOverlapesNodes().isEmpty()) {
					CHWarningDialog dialog = doOpenWarningDialog();
					if(dialog.isOk()) {
						makeAcceptFilter();
					} else {
						return;
					}
				} else if (!userTree.getInsertNodes().isEmpty()) {
					makeAcceptFilter();
				}
				doClose();
			}
		});
		return button;
	}
	
	/**
	 * キャンセルボタン
	 * @return
	 */
	private JButton createCancelButton() {
		JButton button = new JButton("キャンセル");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				doClose();
			}
		});
		return button;
	}
	
	/**
	 * ファイルの移動を元に戻すリセットボタン
	 * @return
	 */
	private JButton createResetButton() {
		JButton button = new JButton("リセット");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				userTree.refresh();
			}
		});
		return button;
	}
	
	public void doOpen() {
		createView();
		setVisible(true);
	}
	
	public void doClose() {
		setVisible(false);
	}
	
	public CHWarningDialog doOpenWarningDialog() {
		CHWarningDialog dialog = new CHWarningDialog(0, userTree.getOverlapesNodes());
		dialog.open();
		return dialog;
	}
	
	public void makeAcceptFilter() {
		String[] paths = new String[userTree.getInsertNodes().size()];
		for (int i = 0; i < userTree.getInsertNodes().size(); i++) {
			paths[i] = userTree.getInsertNodes().get(i).toString();
		}
		acceptFilter = CFileFilter.ACCEPT_BY_NAME_FILTER(paths);
	}
	
	public CFileFilter getAcceptFilter() {
		return acceptFilter;
	}
	
	public static void main(String[] args) {
		
		CDirectory userRootDir = new CDirectory(new CPath(CHFileSystem.SYNCPROJECTPATH));
		CDirectory memberRootDir = new CDirectory(new CPath(CHFileSystem.CHDIRPATH + "/Jiro/final"));
		
		CHFileChooser frame = new CHFileChooser(userRootDir, memberRootDir);
		
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		frame.doOpen();
	}
}
