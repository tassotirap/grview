package org.grview.bsh.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.grview.canvas.Canvas;
import org.grview.canvas.action.CanvasBeanShellAction;

//TODO not yet fully integrated and customizable
public abstract class CanvasInputHandler extends BasicInputHandler<CanvasBeanShellAction>
{

	protected class KeyHandler implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent evt)
		{
			// posso chamar quaisquer ações (externas) do canvas por aqui
			/*
			 * switch (evt.getKeyCode()) { case KeyEvent.VK_B:
			 * getAction("build").invoke(canvas); }
			 */

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
			canvas.setFocused();
		}

		@Override
		public void mouseReleased(MouseEvent event)
		{
		}

	}

	private Canvas canvas;

	public CanvasInputHandler(Canvas canvas)
	{
		this.canvas = canvas;

		setKeyEventInterceptor(new KeyHandler());
		setMouseEventInterceptor(new MouseHandler());
	}

	@Override
	public void invokeAction(CanvasBeanShellAction action)
	{
		action.invoke(canvas);
	}
}
