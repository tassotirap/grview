package org.grview.canvas.action;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasData;
import org.grview.canvas.CanvasFactory;
import org.grview.canvas.state.Connection;
import org.grview.canvas.state.Node;
import org.grview.syntax.command.CommandFactory;
import org.grview.util.ClipboardManager;
import org.netbeans.api.visual.widget.Widget;

public class WidgetCopyPasteProvider
{

	private Canvas canvas;
	private PropertyChangeSupport monitor;

	public WidgetCopyPasteProvider(Canvas canvas)
	{
		this.canvas = canvas;
		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager());
	}

	/** copy all selected widgets to clipboard **/
	public void copySelected()
	{
		copyThese(canvas.getSelectedObjects());
	}

	/**
	 * copy all widgets to clipboard **
	 * 
	 * @param widgets
	 *            widgets to copied, or a set of them
	 */
	public void copyThese(Object... widgets)
	{
		Widget widget;
		boolean hasSelection = false;
		WidgetSelection ws = new WidgetSelection(canvas);
		for (Object w : widgets)
		{
			if (w instanceof Set<?>)
			{
				for (Object obj : (Set<?>) w)
				{
					if ((widget = canvas.findWidget(obj)) != null)
					{
						ws.addSelection(widget);
						hasSelection = true;
					}
				}
			}
			else if (w instanceof Widget)
			{
				ws.addSelection((Widget) w);
				hasSelection = true;
			}
			else if ((widget = canvas.findWidget(w)) != null)
			{
				ws.addSelection(widget);
				hasSelection = true;
			}
		}
		if (hasSelection)
		{
			ClipboardManager.setClipboardContents(ws, ws);
		}
	}

	/**
	 * copy and delete all selected widgets
	 * 
	 * @param wdp
	 *            a Provider to decide how to delete widgets in scene. If null a
	 *            default one will be used.
	 * **/
	public void cutSelected(WidgetDeleteProvider wdp)
	{
		if (wdp == null)
			wdp = new WidgetDeleteProvider(canvas);
		copySelected();
		wdp.deleteSelected();
		canvas.updateState(canvas.getCanvasState());
	}

	public void cutThese(WidgetDeleteProvider wdp, Object... widgets)
	{
		if (wdp == null)
			wdp = new WidgetDeleteProvider(canvas);
		copyThese(widgets);
		wdp.deleteThese(widgets);
		canvas.updateState(canvas.getCanvasState());
	}

	/**
	 * paste copied widgets
	 * 
	 * @param p
	 *            origin point to paste the widgets
	 */
	@SuppressWarnings("unchecked")
	public void paste(Point p)
	{
		Object contents = ClipboardManager.getClipboardContents();
		PointerInfo pi = MouseInfo.getPointerInfo();
		Point pm = canvas.convertLocalToScene(pi.getLocation());
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Connection> connections = new ArrayList<Connection>();
		Point firstNode = null;
		Point originalFirstNode = null;
		HashMap<String, String> oldNewNames = new HashMap<String, String>();
		if (contents != null && contents instanceof ArrayList)
		{
			for (Object obj : (ArrayList) contents)
			{
				if (obj instanceof Connection)
				{
					connections.add((Connection) obj);
				}
				else if (obj instanceof Node)
				{
					nodes.add((Node) obj);
				}
			}
		}
		for (Node n : nodes)
		{
			if (canvas.findWidget(n.getName()) != null)
			{
				int i = 0;
				String oldName = "";
				String newName = "";
				oldName = n.getName();
				do
				{
					if (n.getName().endsWith("_" + (i - 1)))
						n.setName(n.getName().substring(0, n.getName().length() - ("_" + Math.abs(i - 1)).length()));
					n.setName(n.getName() + "_" + (i++));
					newName = n.getName();
					oldNewNames.put(oldName, newName);
					if (n.getType().equals(CanvasData.LEFT_SIDE) || n.getType().equals(CanvasData.START))
					{
						if (n.getTitle().endsWith("_" + (i - 2)))
							n.setTitle(n.getTitle().substring(0, n.getTitle().length() - ("_" + Math.abs(i - 2)).length()));
						n.setTitle(n.getTitle() + "_" + (i - 1));
					}
				}
				while (canvas.findWidget(n.getName()) != null);
				if (firstNode == null)
				{
					originalFirstNode = n.getLocation();
					if (canvas.getInterractionLayer().getState().isHovered() && canvas.isHitAt(pm))
					{
						p = pm;
					}
					else if (p == null)
					{
						p = new Point();
						p.setLocation(n.getLocation().x + 50, n.getLocation().y - 50);
					}
					firstNode = p;
				}
				else if (p == null)
				{
					p = new Point();
					int x = firstNode.x + (n.getLocation().x - originalFirstNode.getLocation().x);
					int y = firstNode.y + (n.getLocation().y - originalFirstNode.getLocation().y);
					p.setLocation(x, y);
				}
			}
			else if (p == null)
			{
				p = n.getLocation();
			}
			n.setLocation(p);
			p = null;
			canvas.getCanvasState().addNode(n);
			monitor.firePropertyChange("undoable", null, CommandFactory.createAddCommand());
		}
		for (Connection c : connections)
		{
			String newSource = oldNewNames.get(c.getSource());
			if (newSource != null)
				c.setSource(newSource);
			String newTarget = oldNewNames.get(c.getTarget());
			c.setTarget(newTarget);
			int i = 0;
			String oldName = c.getName();
			do
			{
				if (c.getName().endsWith("_" + (i)))
					c.setName(c.getName().substring(0, c.getName().length() - ("_" + Math.abs(i)).length()));
				String newName = c.getName() + "_" + (i++);
				c.setName(newName);
				oldNewNames.put(oldName, newName);
			}
			while (canvas.findWidget(c.getName()) != null);
			canvas.getCanvasState().addConnection(c);
			monitor.firePropertyChange("undoable", null, CommandFactory.createConnectionCommand());
		}
		canvas.updateState(canvas.getCanvasState());
		for (String name : oldNewNames.values())
		{
			canvas.select(name, true);
		}
	}

}
