package org.grview.project.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

public class JTreePopupMenu extends JPopupMenu
{

	private static final long serialVersionUID = 1L;
	private final FileTree fileTree;

	public JTreePopupMenu(FileTree fileTree)
	{
		this.fileTree = fileTree;
		initialize();
	}

	public void initialize()
	{
		final TreePath node = fileTree.getSelectedNode();
		JMenuItem open = new JMenuItem("Open");
		open.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				fileTree.open(node);
			}
		});

		JMenuItem rename = new JMenuItem("Rename");

		rename.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				fileTree.getTree().startEditingAtPath(node);
			}
		});

		if (node != null)
		{
			TreeFile tFile = (TreeFile) node.getLastPathComponent();
			if (tFile.isFile())
			{
				add(open);
				add(rename);
			}
		}
	}
}
