package org.grview.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.grview.project.ProjectManager;

public class FrameAdapter extends WindowAdapter
{

	@Override
	public void windowClosing(WindowEvent arg0)
	{
		ProjectManager.getInstance().exit();
	}

}
