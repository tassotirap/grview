package org.grview.canvas.state;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.canvas.widget.MarkedWidget;
import org.grview.canvas.widget.TypedWidget;
import org.netbeans.api.visual.model.ObjectSceneEvent;
import org.netbeans.api.visual.model.ObjectSceneListener;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;

public class CanvasState implements Serializable, ObjectSceneListener, PropertyChangeListener
{

	private static final long serialVersionUID = -7729464439313780001L;

	private HashMap<String, Node> nodes = new HashMap<String, Node>();
	private HashMap<String, Connection> connections = new HashMap<String, Connection>();
	private Preferences preferences = new Preferences();

	public CanvasState()
	{
	}

	public void addConnection(Connection c)
	{
		connections.put(c.getName(), c);
	}

	public void addNode(Node node)
	{
		nodes.put(node.getName(), node);
	}

	public Connection findConnection(Object conn)
	{
		if (connections.containsKey(conn))
		{
			return connections.get(conn);
		}
		return null;
	}

	public Node findNode(Object node)
	{
		if (nodes.containsKey(node))
		{
			return nodes.get(node);
		}
		return null;
	}


	/**
	 * Returns an ordered set of connections. The connections are ordered
	 * according to their names, so that this collection reflects the order of
	 * these connections's creation.
	 * 
	 * @return an ordered set of connections
	 */
	public List<String> getConnections()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(connections.keySet());
		Collections.sort(list);
		return list;
	}

	public Set<String> getNodes()
	{
		return nodes.keySet();
	}

	public Preferences getPreferences()
	{
		return preferences;
	}

	public Object getType(Object obj)
	{
		if (nodes.containsKey(obj))
		{
			return nodes.get(obj).getType();
		}
		if (connections.containsKey(obj))
		{
			return connections.get(obj).getType();
		}
		return null;
	}

	@Override
	public void highlightingChanged(ObjectSceneEvent arg0, Set<Object> arg1, Set<Object> arg2)
	{

	}

	@Override
	public void hoverChanged(ObjectSceneEvent arg0, Object arg1, Object arg2)
	{
	}

	@Override
	public void objectAdded(ObjectSceneEvent event, Object added)
	{
		Canvas canvas = (Canvas) event.getObjectScene();
		Widget widget = canvas.findWidget(added);
		String name = (String) added;
		
		if (canvas.isNode(added) || canvas.isLabel(added))
		{

			Node node = new Node();
			node.setName(name);
			node.setLocation(widget.getPreferredLocation());
			if (widget instanceof TypedWidget)
			{
				node.setType(((TypedWidget) widget).getType());
			}
			else
			{
				node.setType(canvas.getCanvasActiveTool());
			}
			if (widget instanceof MarkedWidget)
			{
				node.setMark(((MarkedWidget) widget).getMark());
			}
			if (widget instanceof LabelWidget)
			{
				node.setTitle(((LabelWidget) widget).getLabel());
			}
			nodes.put(name, node);
		}
		else if (canvas.isSuccessor(name) || canvas.isAlternative(name))
		{	
			Connection conn = new Connection();
			conn.setName(name);
			conn.setType(canvas.getCanvasActiveTool());
			if (widget instanceof ConnectionWidget)
			{
				conn.setSource(canvas.getEdgeSource(name));
				conn.setTarget(canvas.getEdgeTarget(name));
			}
			connections.put(name, conn);
		}
	}

	@Override
	public void objectRemoved(ObjectSceneEvent event, Object removed)
	{
		if (nodes.containsKey(removed))
		{
			nodes.remove(removed);
		}
		if (connections.containsKey(removed))
		{
			connections.remove(removed);
		}

	}

	@Override
	public void objectStateChanged(ObjectSceneEvent arg0, Object arg1, ObjectState arg2, ObjectState arg3)
	{
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals("writing"))
		{
			Canvas canvas = CanvasFactory.getCanvas();
			if (canvas != null)
			{
				update(canvas);
			}
		}
	}

	@Override
	public void selectionChanged(ObjectSceneEvent arg0, Set<Object> arg1, Set<Object> arg2)
	{
	}

	public void update(Canvas canvas)
	{
		for (String node : nodes.keySet())
		{
			Widget widget = canvas.findWidget(node);
			if (widget != null)
			{
				nodes.get(node).setLocation(widget.getPreferredLocation());
				if (widget instanceof LabelWidget)
				{
					nodes.get(node).setTitle(((LabelWidget) widget).getLabel());
				}
				if (widget instanceof MarkedWidget)
				{
					nodes.get(node).setMark(((MarkedWidget) widget).getMark());
				}
			}
		}
		for (String conn : connections.keySet())
		{
			Widget w = canvas.findWidget(conn);
			if (w != null && w instanceof ConnectionWidget)
			{
				connections.get(conn).setSource(canvas.getEdgeSource(conn));
				connections.get(conn).setTarget(canvas.getEdgeTarget(conn));
			}
		}
	}

	@Override
	public void focusChanged(ObjectSceneEvent event, Object oldObject, Object newObject)
	{
		
	}
}
