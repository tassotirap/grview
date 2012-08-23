package org.grview.canvas.action;

import java.awt.Point;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.canvas.CanvasData;
import org.grview.syntax.command.CommandFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

public class NodeConnectProvider implements ConnectProvider
{

	private String source = null;
	private String target = null;

	private PropertyChangeSupport monitor;

	public NodeConnectProvider(Canvas canvas)
	{
		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager());
	}

	@Override
	public void createConnection(Widget sourceWidget, Widget targetWidget)
	{
		Canvas canvas = CanvasFactory.getCanvas();
		String edge = "";
		int numEdges = 0;
		Collection<String> edges = canvas.getEdges();
		if (edges != null)
		{
			numEdges = edges.size();
		}
		if (canvas.getCanvasActiveTool().equals(CanvasData.SUCCESSOR))
		{
			edge = CanvasData.SUC_LBL + numEdges;
			canvas.getCandidateSuc().add(edge);
		}
		else if (canvas.getCanvasActiveTool().equals(CanvasData.ALTERNATIVE))
		{
			edge = CanvasData.ALT_LBL + numEdges;
			canvas.getCandidateAlt().add(edge);
		}
		else
		{
			edge = "edge" + numEdges;
		}

		canvas.addEdge(edge);
		canvas.setEdgeSource(edge, source);
		canvas.setEdgeTarget(edge, target);
		monitor.firePropertyChange("undoable", null, CommandFactory.createConnectionCommand());

	}

	@Override
	public boolean hasCustomTargetWidgetResolver(Scene scene)
	{
		return false;
	}

	@Override
	public boolean isSourceWidget(Widget sourceWidget)
	{
		Canvas canvas = CanvasFactory.getCanvas();
		Object object = canvas.findObject(sourceWidget);
		source = canvas.isNode(object) ? (String) object : null;
		if (source != null)
		{
			Collection<String> edges = canvas.findNodeEdges(source, true, false);
			if (edges.size() >= 2)
			{
				return false;
			}
			for (String e : edges)
			{
				if (canvas.getCanvasActiveTool().equals(CanvasData.SUCCESSOR) && canvas.isSuccessor(e))
				{
					return false;
				}
				if (canvas.getCanvasActiveTool().equals(CanvasData.ALTERNATIVE) && canvas.isAlternative(e))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget)
	{
		Canvas canvas = CanvasFactory.getCanvas();
		Object object = canvas.findObject(targetWidget);
		target = canvas.isNode(object) ? (String) object : null;
		if (target != null)
		{
			return source.equals(target) ? ConnectorState.REJECT_AND_STOP : ConnectorState.ACCEPT;
		}
		return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
	}

	@Override
	public Widget resolveTargetWidget(Scene scene, Point sceneLocation)
	{
		return null;
	}

}
