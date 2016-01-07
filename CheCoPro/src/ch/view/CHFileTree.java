package ch.view;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileFilter;

public class CHFileTree extends JTree implements TreeSelectionListener, TreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DefaultTreeModel model;
	
	private FileTreeNode root;
	private CDirectory rootDir;
	private HashMap<CFileElement, FileTreeNode> table = new HashMap<>();
	
	private DefaultTreeCellRenderer renderer;
	private List<FileTreeNode> originalNodes = new ArrayList<FileTreeNode>();
	private List<FileTreeNode> insertedNodes = new ArrayList<FileTreeNode>();
	private List<FileTreeNode> overlapedNodes = new ArrayList<FileTreeNode>();
	
	public CHFileTree(CDirectory rootDir) {
		this.rootDir = rootDir;
	}
	
	public void initialize() {
		this.setToggleClickCount(1);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		renderer = (DefaultTreeCellRenderer) this.cellRenderer;
		this.setCellRenderer(this);
		addTreeSelectionListener(this);
		setModel(createTreeModel());
		createOriginalNodes();
	}
	
	public void refresh() {
		insertedNodes.clear();
		overlapedNodes.clear();
		setModel(createTreeModel());
		createOriginalNodes();
	}
	
	public void createOriginalNodes() {
		originalNodes.clear();
		for (int i = 0; i < root.getChildCount(); i++) {
			originalNodes.add((FileTreeNode) root.getChildAt(i));
		}
	}
	
	private TreeModel createTreeModel() {
		table.clear();
		root = new FileTreeNode(rootDir);
		table.put(rootDir, root);
		model = new DefaultTreeModel(root, false);
		return model;
	}
	
	public void insertNode(FileTreeNode node) {
		int index = root.getChildCount();
		for (int i = 0; i < root.getChildCount(); i++) {
			if (root.getChildAt(i).toString().equals(node.toString())) {
				if (originalNodes.contains(root.getChildAt(i))) {
					overlapedNodes.add(node);
					root.remove(i);
					index = i;
				}
			}
		}
		
		if (!insertedNodes.contains(node)) {	
			root.insert(node, index);
			addInsertedNodes(node);
		}
		
		model.reload();
	}
	
	public void addInsertedNodes(FileTreeNode node) {
		if (node.isLeaf()) {
			insertedNodes.add(node);
		}
		for (FileTreeNode aNode : node.getChildren()) {
			insertedNodes.add(node);
			addInsertedNodes(aNode);
		}		
	}
	
	public void removeNode(FileTreeNode node) {
		root.remove(node);
		model.reload();
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {

	}
	
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		JLabel label = new JLabel();
		label.setText(value.toString());
		if (leaf) {
			label.setIcon(renderer.getDefaultLeafIcon());
		} else if (expanded) {
			label.setIcon(renderer.getDefaultOpenIcon());
		} else if (!expanded) {
			label.setIcon(renderer.getDefaultClosedIcon());
		}
		
		if (selected) {
			label.setForeground(renderer.getTextSelectionColor());
			label.setBackground(renderer.getBorderSelectionColor());
		}
		
		label.setOpaque(selected);
		
		for (FileTreeNode node : insertedNodes) {
			if (node.toString().equals(value.toString())) {
				label.setOpaque(true);
				if (selected) {
					label.setBackground(Color.GRAY);
				} else {
					label.setBackground(Color.LIGHT_GRAY);
				}
			}
		}
		
		for (FileTreeNode node : overlapedNodes) {
			if (node.toString().equals(value.toString())) {
				label.setForeground(Color.RED);
			}
		}
		
		return label;
	}

	public FileTreeNode getRoot() {
		return root;
	}
	
	public List<FileTreeNode> getInsertNodes() {
		return insertedNodes;
	}
	
	public List<FileTreeNode> getOverlapesNodes() {
		return overlapedNodes;
	}

	class FileTreeNode implements MutableTreeNode {

		private CFileElement fileElement;
		private List<FileTreeNode> children = new ArrayList<FileTreeNode>();
		
		public FileTreeNode(CFileElement file) {
			
			this.fileElement = file;
			if (file.isDirectory()) {
				CFileFilter fileFilter = CFileFilter.ACCEPT_BY_NAME_FILTER("*.java", "*.hcp", "*.c", "*.cpp", "Makefile", "*.oil", "*.rb",
						"*.bat", "*.tex", "*.jpg", "*.gif", "*.png", "*.wav", "*.mp3", "*.csv", "*.dlt", "*.js");
				CFileFilter dirFilter = CFileFilter.IGNORE_BY_NAME_FILTER(".*");
				for (CFileElement child : ((CDirectory) file).getChildren()) {
					if ((child.isFile() && fileFilter.accept(child))
							|| (child.isDirectory() && dirFilter.accept(child))) {
						FileTreeNode node = new FileTreeNode(child);
						children.add(node);
						table.put(child, node);
					}
				}
			}
		}
		
		public CFileElement getFileElement() {
			return this.fileElement;
		}
		
		@Override
		public TreeNode getChildAt(int childIndex) {
			return children.get(childIndex);
		}

		@Override
		public int getChildCount() {
			return children.size();
		}

		@Override
		public TreeNode getParent() {
			return null;
		}

		@Override
		public int getIndex(TreeNode node) {
			return children.indexOf(node);
		}

		@Override
		public boolean getAllowsChildren() {
			return fileElement.isDirectory();
		}

		@Override
		public boolean isLeaf() {
			return fileElement.isFile();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Enumeration children() {
			return new Vector<FileTreeNode>(children).elements();
		}

		@Override
		public void insert(MutableTreeNode child, int index) {
			children.add(index, (FileTreeNode) child);
		}

		@Override
		public void remove(int index) {
			children.remove(index);
		}

		@Override
		public void remove(MutableTreeNode node) {
			children.remove((FileTreeNode) node);
		}

		@Override
		public void setUserObject(Object object) {
		}

		@Override
		public void removeFromParent() {
		}

		@Override
		public void setParent(MutableTreeNode newParent) {
		}
		
		@Override
		public String toString() {
			return fileElement.getName().toString();
		}
		
		public List<FileTreeNode> getChildren() {
			return children;
		}
		
	}
}
