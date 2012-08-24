package org.grview.canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.net.URL;

import javax.swing.ImageIcon;

import org.grview.canvas.widget.IconNodeWidgetExt;
import org.grview.canvas.widget.IconNodeWidgetExt.TextOrientation;
import org.grview.canvas.widget.LabelWidgetExt;
import org.netbeans.api.visual.action.ConnectDecorator;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Widget;

public class CanvasDecorator
{

	private String[] iconName = new String[]{ CanvasData.N_TERMINAL, CanvasData.TERMINAL, CanvasData.LEFT_SIDE, CanvasData.LAMBDA, CanvasData.START };

	private URL[] icons = new URL[]{ getClass().getResource("/org/grview/images/n_terminal.png"), getClass().getResource("/org/grview/images/terminal.png"), getClass().getResource("/org/grview/images/left_side.png"), getClass().getResource("/org/grview/images/lambda.png"), getClass().getResource("/org/grview/images/start.png") };

	protected static final ConnectDecorator CONNECT_DECORATOR_SUCCESSOR = new SuccessorConnectorDecorator();
	protected static final ConnectDecorator CONNECT_DECORATOR_ALTERNATIVE = new AlternativeConnectorDecorator();

	public ConnectionWidget drawConnection(String type, Canvas canvas, String label)
	{
		ConnectionWidget connection = null;
		if (type.equals(CanvasData.SUCCESSOR))
		{
			connection = CONNECT_DECORATOR_SUCCESSOR.createConnectionWidget(canvas.getMainLayer().getScene());
		}
		else
		{
			connection = CONNECT_DECORATOR_ALTERNATIVE.createConnectionWidget(canvas.getMainLayer().getScene());
		}
		connection.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
		connection.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
		connection.setPaintControlPoints(true);
		connection.setControlPointShape(PointShape.SQUARE_FILLED_BIG);
		return connection;
	}

	public Widget drawIcon(String type, Canvas canvas, String text) throws Exception
	{
		Widget widget;
		if (type.equals(CanvasData.LAMBDA))
		{
			IconNodeWidgetExt iwidget = new IconNodeWidgetExt(canvas.getMainLayer().getScene(), TextOrientation.RIGHT_CENTER);
			iwidget.setImage(new ImageIcon(findIconPath(type)).getImage());
			iwidget.setOpaque(true);
			iwidget.repaint();
			widget = iwidget;
		}
		else
		{
			LabelWidgetExt lwidget = new LabelWidgetExt(canvas.getMainLayer().getScene(), text);
			try
			{
				lwidget.setOpaque(true);
				lwidget.setBorder(BorderFactory.createImageBorder(new Insets(6, (type.equals(CanvasData.START)) ? 18 : 8, 6, (type.equals(CanvasData.LEFT_SIDE) || type.equals(CanvasData.START)) ? 16 : 6), new ImageIcon(findIconPath(type)).getImage()));
				lwidget.setMinimumSize(new Dimension(50, 0));
				lwidget.setVerticalAlignment(LabelWidgetExt.VerticalAlignment.CENTER);
				lwidget.repaint();
				widget = lwidget;
			}
			catch (Exception e)
			{
				throw e;
			}
		}
		return widget;
	}

	public void drawSelected(Widget widget)
	{
		widget.setBackground(Color.GRAY);
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
