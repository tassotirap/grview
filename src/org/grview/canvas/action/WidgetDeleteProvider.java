package org.grview.canvas.action;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.syntax.command.CommandFactory;
import org.netbeans.api.visual.widget.Widget;

public class WidgetDeleteProvider
{

	PropertyChangeSupport monitor;
	private Canvas canvas;

	public WidgetDeleteProvider(Canvas canvas)
	{
		this.canvas = canvas;
		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager());
	}

	/** delete all selected widgets **/
	public void deleteSelected()
	{
		deleteThese(canvas.getSelectedObjects());
	}

	/**
	 * Delete all widgets
	 * 
	 * @param widgets
	 *            , Widgets, or a set of them
	 */
	public void deleteThese(Object... widgets)
	{
		ArrayList<Object> toRemove = new ArrayList<Object>();
		for (Object w : widgets)
		{
			if (w instanceof Set<?>)
			{
				for (Object obj : ((Set<?>) w))
				{
					toRemove.add(obj);
				}
			}
			else if (w instanceof Collection<?>)
			{
				for (Object obj : ((Collection<?>) w))
				{
					toRemove.add(obj);
				}
			}
			else if (w instanceof Widget)
			{
				Object obj = canvas.findObject((Widget) w);
				if (obj != null)
				{
					toRemove.add(obj);
				}
			}
			else
			{
				toRemove.add(w);
			}
		}
		Object[] objs = toRemove.toArray();
		for (int i = 0; i < objs.length; i++)
		{
			if (canvas.isNode(objs[i]) || canvas.isLabel(objs[i]))
			{
				// if there is edges attached to this node, I must remove them
				// first
				Collection<String> edges = canvas.findNodeEdges(objs[i].toString(), true, true);
				deleteThese(edges);
				canvas.removeNodeSafely((String) objs[i]);
				monitor.firePropertyChange("undoable", null, CommandFactory.createDelCommand());
			}
			else if (canvas.isEdge(objs[i]))
			{
				canvas.removeEdgeSafely((String) objs[i]);
				monitor.firePropertyChange("undoable", null, CommandFactory.createDelCommand());
			}
		}
	}

	public boolean isDeletionAllowed()
	{
		return isDeletionAllowed(canvas.getSelectedObjects());
	}

	public boolean isDeletionAllowed(Object... widgets)
	{
		// TODO could be a little better
		return (widgets != null && widgets.length >= 1);
	}
}
