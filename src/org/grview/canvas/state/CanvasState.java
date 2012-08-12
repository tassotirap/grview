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

	private String id;

	public String getID()
	{
		return id;
	}

	public CanvasState(String id)
	{
		this.id = id;
	}

	public void focusChanged(ObjectSceneEvent arg0, Object arg1, Object arg2)
	{
	}

	public void highlightingChanged(ObjectSceneEvent arg0, Set<Object> arg1, Set<Object> arg2)
	{

	}

	public void hoverChanged(ObjectSceneEvent arg0, Object arg1, Object arg2)
	{
	}

	public void addNode(Node node)
	{
		nodes.put(node.getName(), node);
	}

	@Override
	public void objectAdded(ObjectSceneEvent event, Object added)
	{
		Canvas canvas = (Canvas) event.getObjectScene();
		if (canvas.isNode(added) || canvas.isLabel(added))
		{
			Widget w = canvas.findWidget(added);
			Node node = new Node();
			node.setName((String) added);
			node.setLocation(w.getPreferredLocation());
			if (w instanceof TypedWidget)
			{
				node.setType(((TypedWidget) w).getType());
			}
			else
			{
				// This type should not be trusted! Could be wrong
				node.setType(canvas.getActiveTool());
			}
			if (w instanceof MarkedWidget)
			{
				node.setMark(((MarkedWidget) w).getMark());
			}
			if (w instanceof LabelWidget)
			{ // has a title
				node.setTitle(((LabelWidget) w).getLabel());
			}
			nodes.put((String) added, node);
		}
		else if (canvas.isSuccessor((String) added) || canvas.isAlternative((String) added))
		{
			Widget w = canvas.findWidget(added);
			Connection conn = new Connection();
			conn.setName((String) added);
			conn.setType(canvas.getActiveTool());
			if (w instanceof ConnectionWidget)
			{
				conn.setSource(canvas.getEdgeSource((String) added));
				conn.setTarget(canvas.getEdgeTarget((String) added));
			}
			connections.put((String) added, conn);
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

	public void objectStateChanged(ObjectSceneEvent arg0, Object arg1, ObjectState arg2, ObjectState arg3)
	{
	}

	public void selectionChanged(ObjectSceneEvent arg0, Set<Object> arg1, Set<Object> arg2)
	{
		// TODO Auto-generated method stub

	}

	public void update(Canvas canvas)
	{
		for (String node : nodes.keySet())
		{
			Widget w = canvas.findWidget(node);
			if (w != null)
			{
				nodes.get(node).setLocation(w.getPreferredLocation());
				if (w instanceof LabelWidget)
				{
					nodes.get(node).setTitle(((LabelWidget) w).getLabel());
				}
				if (w instanceof MarkedWidget)
				{
					nodes.get(node).setMark(((MarkedWidget) w).getMark());
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

	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals("writing"))
		{
			Canvas canvas = CanvasFactory.getCanvas(this.id);
			if (canvas != null)
			{
				update(canvas);
			}
		}
	}

	public Node findNode(Object node)
	{
		if (nodes.containsKey(node))
		{
			return nodes.get(node);
		}
		return null;
	}

	public Connection findConnection(Object conn)
	{
		if (connections.containsKey(conn))
		{
			return connections.get(conn);
		}
		return null;
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

	public Set<String> getNodes()
	{
		return nodes.keySet();
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

	public void addConnection(Connection c)
	{
		connections.put(c.getName(), c);
	}

	public Preferences getPreferences()
	{
		return preferences;
	}

	public void setId(String id)
	{
		this.id = id;		
	}
}
