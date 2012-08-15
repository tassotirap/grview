package org.grview.canvas;

import java.awt.Color;
import java.net.URL;

import org.netbeans.api.visual.action.ConnectDecorator;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

public abstract class CanvasDecorator
{

	private String[] iconName = new String[]{ Canvas.N_TERMINAL, Canvas.TERMINAL, Canvas.LEFT_SIDE, Canvas.LAMBDA, Canvas.START };

	private URL[] icons = new URL[]{ getClass().getResource("/org/grview/images/n_terminal.png"), getClass().getResource("/org/grview/images/terminal.png"), getClass().getResource("/org/grview/images/left_side.png"), getClass().getResource("/org/grview/images/lambda.png"), getClass().getResource("/org/grview/images/start.png") };

	protected static final ConnectDecorator CONNECT_DECORATOR_SUCCESSOR = new SuccessorConnectorDecorator();
	protected static final ConnectDecorator CONNECT_DECORATOR_ALTERNATIVE = new AlternativeConnectorDecorator();

	public abstract ConnectionWidget drawConnection(String type, Canvas canvas, String label);

	public abstract Widget drawIcon(String type, Canvas canvas, String label) throws Exception;

	public void drawSelected(Widget widget)
	{
		widget.setBackground(Color.WHITE);
		widget.setForeground(Color.BLACK);
	}

	public void drawUnselected(Widget widget)
	{
		widget.setBackground(Color.BLUE);
		widget.setForeground(Color.WHITE);
	}

	public URL findIconPath(String type)
	{
		for (int i = 0; i < icons.length && i < iconName.length; i++)
		{
			if (iconName[i].equals(type))
			{
				return icons[i];
			}
		}
		return null;
	}

	public ConnectDecorator getConnDecoratorAlt()
	{
		return CONNECT_DECORATOR_ALTERNATIVE;
	}

	public ConnectDecorator getConnDecoratorSuc()
	{
		return CONNECT_DECORATOR_SUCCESSOR;
	}
}
