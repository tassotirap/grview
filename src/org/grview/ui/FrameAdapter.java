package org.grview.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.grview.project.GrviewManager;

public class FrameAdapter extends WindowAdapter
{

	@Override
	public void windowClosing(WindowEvent arg0)
	{
		GrviewManager.getInstance().exit();
	}

}
