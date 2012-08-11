package org.grview.project.tree;

import java.io.File;

public class TreeFile extends File
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TreeFile(File parent, String child)
	{
		super(parent, child);
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
