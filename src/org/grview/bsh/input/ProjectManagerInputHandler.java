package org.grview.bsh.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.grview.project.ProjectManager;
import org.grview.project.ProjectManagerBeanShellAction;

public abstract class ProjectManagerInputHandler extends BasicInputHandler<ProjectManagerBeanShellAction>
{

	protected class KeyHandler implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent evt)
		{
		}

		@Override
		public void keyReleased(KeyEvent arg0)
		{
		}

		@Override
		public void keyTyped(KeyEvent arg0)
		{
		}

	}

	protected class MouseHandler implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent e)
		{
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
		public void mousePressed(MouseEvent event)
		{
		}

		@Override
		public void mouseReleased(MouseEvent event)
		{
		}

	}

	ProjectManager pManager;

	public ProjectManagerInputHandler(ProjectManager pManager)
	{
		this.pManager = pManager;

		setKeyEventInterceptor(new KeyHandler());
		setMouseEventInterceptor(new MouseHandler());
	}

	@Override
	public void invokeAction(ProjectManagerBeanShellAction action)
	{
		action.invoke(pManager);
	}

}
