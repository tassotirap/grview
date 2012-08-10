package org.grview.project.tree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.grview.model.FileNames;
import org.grview.model.ui.IconRepository;
import org.grview.project.Project;
import org.grview.project.ProjectManager;
import org.grview.project.tree.FileSystemModel.TreeFile;
import org.grview.ui.MainWindow;

/**
 * Display a file system in a JTree view
 * 
 * @author Gustavo H. Braga
 */
public class FileTree implements TreeModelListener
{

	private static final long serialVersionUID = 1L;

	private TreePath selectedNode;
	private static FileTree instance;
	private static HashMap<String, FileSystemModel> fsmInstances = new HashMap<String, FileSystemModel>();
	private static HashMap<String, FileTree> ftInstances = new HashMap<String, FileTree>();
	private JTree tree;
	private Project project;

	public FileTree(Project project)
	{
		this.project = project;
		instance = this;
		ftInstances.put(project.getProjectDir().getAbsolutePath(), instance);
		tree = new JTree();
		CustomTreeCellRenderer renderer = new CustomTreeCellRenderer();
		tree.setCellRenderer(renderer);
		tree.setEditable(true);
		tree.setModel(getFileSystemModel(project.getProjectDir().getAbsolutePath()));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new FileTreeSelectionListener());
		tree.addMouseListener(new FileTreeMouseListener());
		getFileSystemModel(project.getProjectDir().getAbsolutePath()).addTreeModelListener(this);
	}

	private static FileSystemModel getFileSystemModel(String rootPath)
	{
		if (!ftInstances.containsKey(rootPath))
		{
			return null;
		}
		if (!fsmInstances.containsKey(rootPath))
		{
			fsmInstances.put(rootPath, new FileSystemModel(new File(rootPath)));
		}
		return fsmInstances.get(rootPath);
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

	public JComponent getView()
	{
		return tree;
	}

	public JTree getTree()
	{
		return tree;
	}

	public Dimension getMinimumSize()
	{
		return new Dimension(200, 400);
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(200, 400);
	}

	private class CustomTreeCellRenderer extends DefaultTreeCellRenderer
	{

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{

			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			if (leaf)
			{
				if (!value.toString().startsWith("."))
					setIcon(IconRepository.getIconByFileName(value.toString()));
			}
			else
			{
				setIcon(IconRepository.getInstance().DIR_ICON);
			}

			return this;
		}
	}

	private class FileTreeSelectionListener implements TreeSelectionListener
	{

		public void valueChanged(TreeSelectionEvent e)
		{
			instance.selectedNode = e.getPath();
		}

	}

	public void open(TreePath treePath)
	{
		TreeFile node = (TreeFile) treePath.getLastPathComponent();
		if (node.isFile())
		{
			String path = node.getAbsolutePath();
			ProjectManager.openFile(path);
		}
	}

	public TreePath getSelectedNode()
	{
		return selectedNode;
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

	private class FileTreeMouseListener implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() == 2)
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

}