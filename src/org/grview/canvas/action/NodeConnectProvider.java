package org.grview.canvas.action;

import java.awt.Point;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.syntax.command.Command;
import org.grview.syntax.command.CommandFactory;
import org.grview.syntax.grammar.model.SyntaxDefinitions;
import org.grview.util.Log;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

public class NodeConnectProvider implements ConnectProvider
{

	private String source = null;
	private String target = null;

	private String canvasID;
	private PropertyChangeSupport monitor;

	public NodeConnectProvider(Canvas canvas)
	{
		canvasID = canvas.getID();
		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager(canvasID));
	}

	@Override
	public void createConnection(Widget sourceWidget, Widget targetWidget)
	{
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
		String edge = "";
		int numEdges = 0;
		Collection<String> edges = canvas.getEdges();
		if (edges != null)
		{
			numEdges = edges.size();
		}
		if (canvas.getActiveTool().equals(Canvas.SUCCESSOR))
		{
			edge = Canvas.SUC_LBL + numEdges;
			canvas.getCandidateSuc().add(edge);
		}
		else if (canvas.getActiveTool().equals(Canvas.ALTERNATIVE))
		{
			edge = Canvas.ALT_LBL + numEdges;
			canvas.getCandidateAlt().add(edge);
		}
		else
		{
			edge = "edge" + numEdges;
		}
		String context = "";
		if (canvas.getActiveTool().equals(Canvas.SUCCESSOR))
		{
			context = SyntaxDefinitions.SucConnection;
		}
		else if (canvas.getActiveTool().equals(Canvas.ALTERNATIVE))
		{
			context = SyntaxDefinitions.AltConnection;
		}
		Command cmd = CommandFactory.createConnectionCommand();
		// cmd.addObject(target, source, context);
		// cmd.execute();
		if (cmd.addObject(target, source, edge, context) && cmd.execute())
		{
			canvas.addEdge(edge);
			canvas.setEdgeSource(edge, source);
			canvas.setEdgeTarget(edge, target);
			monitor.firePropertyChange("undoable", null, cmd);
		}
		else
		{
			Log.log(Log.ERROR, this, "Could not create connection!\nAn internal error ocurred.", new Exception("Failed to accept conn command in asin editor."));
		}
	}

	@Override
	public boolean hasCustomTargetWidgetResolver(Scene scene)
	{
		return false;
	}

	@Override
	public boolean isSourceWidget(Widget sourceWidget)
	{
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
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
				if (canvas.getActiveTool().equals(Canvas.SUCCESSOR) && canvas.isSuccessor(e))
				{
					return false;
				}
				if (canvas.getActiveTool().equals(Canvas.ALTERNATIVE) && canvas.isAlternative(e))
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
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
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
