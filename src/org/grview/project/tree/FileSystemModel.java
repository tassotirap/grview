package org.grview.project.tree;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.grview.project.ProjectManager;

public class FileSystemModel implements TreeModel
{
	private Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();
	
	private ProjectManager projectManager;

	private File root;

	public FileSystemModel(File rootDirectory)
	{
		root = rootDirectory;
		this.projectManager = ProjectManager.getInstance();
	}

	protected void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children)
	{
		TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
		Iterator<TreeModelListener> iterator = listeners.iterator();
		TreeModelListener listener = null;
		while (iterator.hasNext())
		{
			listener = iterator.next();
			listener.treeNodesChanged(event);
		}
	}

	/*
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @param source the node where the tree model has changed
	 * 
	 * @param path the path to the root node
	 * 
	 * @see EventListenerList
	 */
	protected void fireTreeStructureChanged(Object source, TreePath path)
	{
		Iterator<TreeModelListener> iterator = listeners.iterator();
		TreeModelEvent e = null;
		while (iterator.hasNext())
		{
			if (e == null)
				e = new TreeModelEvent(source, path);
			iterator.next().treeStructureChanged(e);
		}
	}

	@Override
	public void addTreeModelListener(TreeModelListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public Object getChild(Object parent, int index)
	{
		File directory = (File) parent;
		String[] children = directory.list();
		return new TreeFile(directory, children[index]);
	}

	@Override
	public int getChildCount(Object parent)
	{
		File file = (File) parent;
		if (file.isDirectory())
		{
			String[] fileList = file.list();
			if (fileList != null)
				return file.list().length;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		File directory = (File) parent;
		File file = (File) child;
		String[] children = directory.list();
		for (int i = 0; i < children.length; i++)
		{
			if (file.getName().equals(children[i]))
			{
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object getRoot()
	{
		return root;
	}

	@Override
	public boolean isLeaf(Object node)
	{
		File file = (File) node;
		return file.isFile();
	}

	@Override
	public void removeTreeModelListener(TreeModelListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object value)
	{

		File oldFile = (File) path.getLastPathComponent();
		String fileParentPath = oldFile.getParent();

		if (projectManager.isFileOpen(oldFile.getAbsolutePath()))
		{
			JOptionPane.showMessageDialog(projectManager.getMainWindow().getFrame(), "Could not rename an opened file! Please close " + oldFile.getName() + " and try again.", "Rename File", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String newFileName = (String) value;
		File targetFile = new File(fileParentPath, newFileName);
		oldFile.renameTo(targetFile);
		File parent = new File(fileParentPath);
		int[] changedChildrenIndices = { getIndexOfChild(parent, targetFile) };
		Object[] changedChildren = { targetFile };
		fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);
		fireTreeStructureChanged(this, path.getParentPath());

		projectManager.renameFile(((File) path.getLastPathComponent()).getAbsolutePath(), targetFile.getAbsolutePath());
	}

}