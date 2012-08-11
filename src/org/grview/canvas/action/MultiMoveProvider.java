package org.grview.canvas.action;

import java.awt.Point;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.syntax.command.CommandFactory;
import org.grview.syntax.command.MoveCommand;
import org.grview.syntax.grammar.model.SyntaxDefinitions;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.widget.Widget;

public class MultiMoveProvider implements MoveProvider
{

	private HashMap<Widget, Point> originals = new HashMap<Widget, Point>();
	private Point original;

	private String canvasID;
	private PropertyChangeSupport monitor;

	public MultiMoveProvider(Canvas canvas)
	{
		canvasID = canvas.getID();
		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager(canvasID));
	}

	public void movementStarted(Widget widget)
	{
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
		Object object = canvas.findObject(widget);
		if (canvas.isNode(object) || canvas.isLabel(object))
		{
			for (Object o : canvas.getSelectedObjects())
				if (canvas.isNode(o) || canvas.isLabel(object))
				{
					Widget w = canvas.findWidget(o);
					if (w != null)
						originals.put(w, w.getPreferredLocation());
				}
		}
		else
		{
			originals.put(widget, widget.getPreferredLocation());
		}
	}

	public void movementFinished(Widget widget)
	{
		String context = null;
		if (originals.entrySet().size() > 1)
		{
			context = SyntaxDefinitions.Set;
		}
		else if (originals.entrySet().size() == 1)
		{
			context = SyntaxDefinitions.Node;
		}
		originals.clear();
		original = null;
		if (context != null)
		{
			MoveCommand mc = CommandFactory.createMoveCommand();
			if (mc.addObject(null, context) && mc.execute())
			{
				monitor.firePropertyChange("undoable", null, mc);
			}
		}
	}

	public Point getOriginalLocation(Widget widget)
	{
		original = widget.getPreferredLocation();
		return original;
	}

	public void setNewLocation(Widget widget, Point location)
	{
		int dx = location.x - original.x;
		int dy = location.y - original.y;
		for (Map.Entry<Widget, Point> entry : originals.entrySet())
		{
			Point point = entry.getValue();
			entry.getKey().setPreferredLocation(new Point(point.x + dx, point.y + dy));
		}
	}

}
