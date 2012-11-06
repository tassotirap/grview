package org.grview.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.grview.project.ProjectMediator;

public class FrameAdapter extends WindowAdapter
{

	@Override
	public void windowClosing(WindowEvent arg0)
	{
		ProjectMediator.exit();
	}

}
