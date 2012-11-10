package org.grview.project.tree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.grview.model.ui.IconFactory;
import org.grview.model.ui.IconFactory.IconType;
import org.grview.project.ProjectManager;

/**
 * Display a file system in a JTree view
 * 
 * @author Gustavo H. Braga
 * @author Tasso Tirapani Silva Pinto
 */
public class FileTree implements TreeModelListener
{

	private class CustomTreeCellRenderer extends DefaultTreeCellRenderer
	{

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			IconFactory iconFactory = new IconFactory();
			if (leaf)
			{
				if (!value.toString().startsWith("."))
				{
					
					setIcon(iconFactory.getIcon(value.toString()));
				}
			}
			else
			{
				setIcon(iconFactory.getIcon(IconType.DIR_ICON));
			}

			return this;
		}
	}

	private class FileTreeMouseListener implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent evt)
		{
			if (evt.getClickCount() == 2)
			{
				open(selectedNode);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{

		}

		@Override
		public void mouseExited(MouseEvent e)
		{

		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				new JTreePopupMenu(instance).show((Component) e.getSource(), e.getX(), e.getY());
			}

		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				new JTreePopupMenu(instance).show((Component) e.getSource(), e.getX(), e.getY());
			}

		}

	}

	private class FileTreeSelectionListener implements TreeSelectionListener
	{

		@Override
		public void valueChanged(TreeSelectionEvent e)
		{
			instance.selectedNode = e.getPath();
		}

	}

	private static FileSystemModel fsmInstances;

	private static FileTree instance;

	private TreePath selectedNode;

	private JTree tree;

	public FileTree()
	{
		instance = this;
		tree = new JTree();
		CustomTreeCellRenderer renderer = new CustomTreeCellRenderer();
		tree.setCellRenderer(renderer);
		tree.setEditable(true);
		tree.setModel(getFileSystemModel(ProjectManager.getInstance().getProject().getProjectDir().getAbsolutePath()));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new FileTreeSelectionListener());
		tree.addMouseListener(new FileTreeMouseListener());
		getFileSystemModel(ProjectManager.getInstance().getProject().getProjectDir().getAbsolutePath()).addTreeModelListener(this);
	}

	private static FileSystemModel getFileSystemModel(String rootPath)
	{
		if (instance == null)
		{
			return null;
		}
		if (fsmInstances == null)
		{
			fsmInstances = new FileSystemModel(new File(rootPath));
		}
		return fsmInstances;
	}

	public static void reload(String rootPath)
	{
		getFileSystemModel(rootPath).fireTreeStructureChanged(getFileSystemModel(rootPath), new TreePath(rootPath));
	}

	public static void update(String rootPath, Object[] changedObjects)
	{
		int[] indices = new int[changedObjects.length];
		int i = 0;
		File root = new File(rootPath);
		for (Object o : changedObjects)
		{
			indices[i++] = getFileSystemModel(rootPath).getIndexOfChild(root, o);
		}
		getFileSystemModel(rootPath).fireTreeNodesChanged(new TreePath(rootPath), indices, changedObjects);
	}

	public Dimension getMinimumSize()
	{
		return new Dimension(200, 400);
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(200, 400);
	}

	public TreePath getSelectedNode()
	{
		return selectedNode;
	}

	public JTree getTree()
	{
		return tree;
	}

	public JComponent getView()
	{
		return tree;
	}

	public void open(TreePath treePath)
	{
		TreeFile node = (TreeFile) treePath.getLastPathComponent();
		if (node.isFile())
		{
			String path = node.getAbsolutePath();
			ProjectManager.getInstance().openFile(path);
		}
	}

	@Override
	public void treeNodesChanged(TreeModelEvent e)
	{
		tree.validate();
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e)
	{
		tree.validate();
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e)
	{
		tree.validate();
	}

	@Override
	public void treeStructureChanged(TreeModelEvent e)
	{
		tree.validate();
	}

}