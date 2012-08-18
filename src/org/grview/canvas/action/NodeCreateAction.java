package org.grview.canvas.action;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.syntax.command.CommandFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

public class NodeCreateAction extends WidgetAction.Adapter
{

	private PropertyChangeSupport monitor;

	private String canvasID;

	public NodeCreateAction(Canvas canvas)
	{
		canvasID = canvas.getID();
		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager(canvasID));
	}

	private String createDefaultName()
	{
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
		if (canvas.getActiveTool().equals(Canvas.TERMINAL))
		{
			return String.format("Terminal%d", (canvas.getTerminals().size() + 1));
		}
		if (canvas.getActiveTool().equals(Canvas.N_TERMINAL))
		{
			return String.format("NTerminal%d", (canvas.getNterminals().size() + 1));
		}
		if (canvas.getActiveTool().equals(Canvas.LEFT_SIDE))
		{
			return String.format("LeftSide%d", (canvas.getLeftSides().size() + 1));
		}
		if (canvas.getActiveTool().equals(Canvas.LAMBDA))
		{
			return String.format("Lambda%d", (canvas.getLambdas().size() + 1));
		}
		if (canvas.getActiveTool().equals(Canvas.START))
		{
			return String.format("S%d", (canvas.getStart().size() + 1));
		}
		return String.format("node%d", (canvas.customNodes.size() + 1));
	}

	@Override
	public State mousePressed(Widget widget, WidgetMouseEvent event)
	{
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
		if (event.getClickCount() == 1)
			if (event.getButton() == MouseEvent.BUTTON1 && ((canvas.getActiveTool().equals(Canvas.LEFT_SIDE) || canvas.getActiveTool().equals(Canvas.TERMINAL) || canvas.getActiveTool().equals(Canvas.N_TERMINAL) || canvas.getActiveTool().equals(Canvas.LAMBDA) || canvas.getActiveTool().equals(Canvas.LABEL) || canvas.getActiveTool().equals(Canvas.START))))
			{
				String name = createDefaultName();
				canvas.addNode(name).setPreferredLocation(widget.convertLocalToScene(event.getPoint()));
				monitor.firePropertyChange("undoable", null, CommandFactory.createAddCommand());

				/*
				 * you should only return consumed if you are sure, no one else
				 * will be receiving mouse events
				 */
				/* return State.CONSUMED; */
			}
		return State.REJECTED;
	}

}
