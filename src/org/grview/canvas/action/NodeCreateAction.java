package org.grview.canvas.action;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasData;
import org.grview.canvas.CanvasFactory;
import org.grview.syntax.command.CommandFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

public class NodeCreateAction extends WidgetAction.Adapter
{

	private PropertyChangeSupport monitor;

	public NodeCreateAction(Canvas canvas)
	{

		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager());
	}

	private String createDefaultName()
	{
		Canvas canvas = CanvasFactory.getCanvas();
		if (canvas.getCanvasActiveTool().equals(CanvasData.TERMINAL))
		{
			return String.format("Terminal%d", (canvas.getTerminals().size() + 1));
		}
		if (canvas.getCanvasActiveTool().equals(CanvasData.N_TERMINAL))
		{
			return String.format("NTerminal%d", (canvas.getNterminals().size() + 1));
		}
		if (canvas.getCanvasActiveTool().equals(CanvasData.LEFT_SIDE))
		{
			return String.format("LeftSide%d", (canvas.getLeftSides().size() + 1));
		}
		if (canvas.getCanvasActiveTool().equals(CanvasData.LAMBDA))
		{
			return String.format("Lambda%d", (canvas.getLambdas().size() + 1));
		}
		if (canvas.getCanvasActiveTool().equals(CanvasData.START))
		{
			return String.format("S%d", (canvas.getStart().size() + 1));
		}
		return String.format("node%d", (canvas.getCustomNodes().size() + 1));
	}

	private boolean isNode(Canvas canvas)
	{
		return canvas.getCanvasActiveTool().equals(CanvasData.LEFT_SIDE) || canvas.getCanvasActiveTool().equals(CanvasData.TERMINAL) || canvas.getCanvasActiveTool().equals(CanvasData.N_TERMINAL) || canvas.getCanvasActiveTool().equals(CanvasData.LAMBDA) || canvas.getCanvasActiveTool().equals(CanvasData.LABEL) || canvas.getCanvasActiveTool().equals(CanvasData.START);
	}

	@Override
	public State mousePressed(Widget widget, WidgetMouseEvent event)
	{
		Canvas canvas = CanvasFactory.getCanvas();
		if (event.getClickCount() == 1)
			if (event.getButton() == MouseEvent.BUTTON1 && isNode(canvas))
			{
				String name = createDefaultName();
				canvas.addNode(name).setPreferredLocation(widget.convertLocalToScene(event.getPoint()));
				monitor.firePropertyChange("undoable", null, CommandFactory.createAddCommand());
			}
		return State.REJECTED;
	}

}
