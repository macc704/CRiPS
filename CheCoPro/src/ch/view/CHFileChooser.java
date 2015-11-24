package ch.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import ch.library.CHFileSystem;
import ch.view.CHFileTree.FileTreeNode;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;

public class CHFileChooser extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CDirectory userRootDir;
	private CDirectory memberRootDir;
	
	private CHFileTree userTree;
	private CHFileTree memberTree;
	
	private String member;

	public CHFileChooser(CDirectory userRootDir, CDirectory memberRootDir) {
		this.userRootDir = userRootDir;
		this.memberRootDir = memberRootDir;
		member = memberRootDir.getParentDirectory().getNameByString();
	}
	
	public void createView() {
	    
		userTree = new CHFileTree(userRootDir);
		memberTree = new CHFileTree(memberRootDir);	
		userTree.initialize();
		memberTree.initialize();
	    
	    getContentPane().add(new JLabel("取り込むファイルを選択"), BorderLayout.NORTH);
	    getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
	    getContentPane().add(createBottomPanel(), BorderLayout.PAGE_END);
	}
	
	private JScrollPane createScrollPane(CHFileTree tree) {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(tree);
		scrollPane.setPreferredSize(new Dimension(180, 120));
		return scrollPane;
	}
	
	private JPanel createFileTreePanel(CHFileTree tree, JLabel label) {
	    JPanel panel = new JPanel();
	    panel.setPreferredSize(new Dimension(200, 150));
	    panel.add(label, BorderLayout.NORTH);
	    panel.add(createScrollPane(tree), BorderLayout.CENTER);
	    return panel;
	}
	
	private JPanel createCenterPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(80, 70));
		buttonPanel.add(createSingleImportButton());
		buttonPanel.add(createAllImportButton());
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(450, 200));
		panel.add(createFileTreePanel(userTree, new JLabel("あなたのfinalプロジェクト")), BorderLayout.WEST);
		panel.add(buttonPanel, BorderLayout.CENTER);
		panel.add(createFileTreePanel(memberTree, 
				new JLabel(member + "さんのfinalプロジェクト")), BorderLayout.EAST);
		
		return panel;
	}
	
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
	
	private JPanel createBottomPanel() {
		JPanel panel = new JPanel();
		panel.add(createResetButton());
		panel.add(createCancelButton());
		panel.add(createOKButton());
		return panel;
	}
	
	private JButton createOKButton() {
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 取り込み処理
			}
		});
		return button;
	}
	
	private JButton createCancelButton() {
		JButton button = new JButton("キャンセル");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO キャンセル処理
			}
		});
		return button;
	}
	
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
	
	public static void main(String[] args) {
		
		CDirectory userRootDir = new CDirectory(new CPath(CHFileSystem.FINALPROJECTPATH));
		CDirectory memberRootDir = new CDirectory(new CPath(CHFileSystem.CHDIRPATH + "/Jiro/final"));
		
		CHFileChooser frame = new CHFileChooser(userRootDir, memberRootDir);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 500, 250);
		frame.createView();
		frame.setVisible(true);
	}
}
