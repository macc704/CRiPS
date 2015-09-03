/*
 * REFileTreeNavigator.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */
package ronproeditor.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ronproeditor.ICFwApplication;
import ronproeditor.ICFwResourceRepository;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileFilter;

/**
 * Class REFileTreeNavigator
 * 
 * @author macchan
 * @version $Id: REFileTreeNavigator.java,v 1.1 2007/09/22 08:25:02 macchan Exp
 *          $
 */
public class REFileTreeNavigator extends JTree implements
		TreeSelectionListener, PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private ICFwResourceRepository repository;
	private ICFwApplication application;

	private HashMap<CFileElement, FileTreeNode> table = new HashMap<CFileElement, FileTreeNode>();
	private FileTreeNode root;

	/**
	 * Constructor for REFileTreeNavigator
	 */
	public REFileTreeNavigator(ICFwApplication application) {
		this.application = application;
		this.repository = application.getResourceRepository();
		initializeView();
		refreshModel();
	}

	private void initializeView() {
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// setRootVisible(false);
		addTreeSelectionListener(this);
		repository.addPropertyChangeListener(this);
	}

	/***********************************
	 * View => Modelの更新系
	 ************************************/

	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent evt) {
		if (getSelectionPath() == null) {
			return;
		}
		if (getSelectionPath().getLastPathComponent() == root) {
			application.doSetProjectDirectory(null);
			return;
		}
		changeModule(evt);
	}

	private void changeModule(TreeSelectionEvent evt) {
		CFileElement file = ((FileTreeNode) evt.getPath()
				.getLastPathComponent()).getFileElement();
		if (file.isFile()) {
			application.doOpen(file);
		} else {
			application.doSetProjectDirectory((CDirectory) file);
		}
	}

	/***********************************
	 * Model => Viewの更新系
	 ************************************/

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		removeTreeSelectionListener(this);
		if (evt.getPropertyName()
				.equals(ICFwResourceRepository.DOCUMENT_OPENED)) {
			refreshSelection();
		} else if (evt.getPropertyName().equals(
				ICFwResourceRepository.DOCUMENT_CLOSED)) {
			refreshSelection();
		} else if (evt.getPropertyName().equals(
				ICFwResourceRepository.MODEL_REFRESHED)) {
			refreshModel();
		} else if (evt.getPropertyName().equals(
				ICFwResourceRepository.PROJECT_REFRESHED)) {
			refreshSelection();
		}
		addTreeSelectionListener(this);
	}

	private void refreshSelection() {
		getSelectionModel().clearSelection();

		if (repository.hasCurrentFile()) {
			TreePath path = getPath(repository.getCCurrentFile());
			setSelectionPath(path);
		} else if (repository.getCCurrentProject() != null) {
			TreePath path = getPath(repository.getCCurrentProject());
			setSelectionPath(path);
		}

	}

	private TreePath getPath(CFileElement file) {
		LinkedList<FileTreeNode> pathlist = new LinkedList<FileTreeNode>();
		CDirectory root = repository.getCRootDirectory();
		pathlist.addFirst(table.get(file));
		while (!file.equals(root)) {
			file = file.getParentDirectory();
			pathlist.addFirst(table.get(file));
		}
		TreePath path = new TreePath(pathlist.toArray());
		return path;
	}

	private void refreshModel() {
		setModel(createTreeModel());
	}

	private TreeModel createTreeModel() {
		table.clear();
		CDirectory directory = repository.getCRootDirectory();
		root = new FileTreeNode(directory);
		table.put(directory, root);
		return new DefaultTreeModel(root, false);
	}

	/***********************************
	 * TreeNode Implementer
	 ************************************/

	class FileTreeNode implements MutableTreeNode {
		private CFileElement fileElement;

		private List<FileTreeNode> children = new ArrayList<FileTreeNode>();

		FileTreeNode(CFileElement file) {
			this.fileElement = file;
			if (file.isDirectory()) {
				CFileFilter fileFilter = repository.getFileFilter();
				CFileFilter dirFilter = repository.getDirFilter();
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

		public void insert(MutableTreeNode child, int index) {
		}

		public void remove(int index) {
		}

		public void remove(MutableTreeNode node) {
		}

		public void removeFromParent() {
		}

		public void setParent(MutableTreeNode newParent) {
		}

		public void setUserObject(Object object) {
		}

		@SuppressWarnings("rawtypes")
		public Enumeration children() {
			return new Vector<FileTreeNode>(children).elements();
		}

		public boolean getAllowsChildren() {
			return fileElement.isDirectory();
		}

		public TreeNode getChildAt(int childIndex) {
			return children.get(childIndex);
		}

		public int getChildCount() {
			return children.size();
		}

		public int getIndex(TreeNode node) {
			return children.indexOf(node);
		}

		public TreeNode getParent() {
			return null;
		}

		public boolean isLeaf() {
			return fileElement.isFile();
		}

		public String toString() {
			return fileElement.getName().toString();
		}
	}

}
