package org.grview.canvas.action;

import java.awt.Point;
import java.beans.PropertyChangeSupport;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.syntax.command.CommandFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.ReconnectProvider;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

public class NodeReconnectProvider implements ReconnectProvider
{

	private String edge;
	private String originalNode;
	private String replacementNode;

	private String canvasID;
	private PropertyChangeSupport monitor;

	public NodeReconnectProvider(Canvas canvas)
	{
		canvasID = canvas.getID();
		monitor = new PropertyChangeSupport(this);
		monitor.addPropertyChangeListener(CanvasFactory.getVolatileStateManager(canvasID));
	}

	@Override
	public boolean hasCustomReplacementWidgetResolver(Scene scene)
	{
		return false;
	}

	@Override
	public ConnectorState isReplacementWidget(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource)
	{
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
		Object object = canvas.findObject(replacementWidget);
		replacementNode = canvas.isNode(object) ? (String) object : null;
		if (replacementNode != null && edge != null)
		{
			return ConnectorState.ACCEPT;
		}
		return ConnectorState.REJECT;
	}

	@Override
	public boolean isSourceReconnectable(ConnectionWidget connectionWidget)
	{
		/*
		 * Canvas canvas = CanvasFactory.getCanvas(canvasID); Object object =
		 * canvas.findObject (connectionWidget); edge = canvas.isEdge (object) ?
		 * (String) object : null; originalNode = edge != null ?
		 * canvas.getEdgeSource (edge) : null; return originalNode != null;
		 */
		// Actually I prefer to avoid this
		return false;
	}

	@Override
	public boolean isTargetReconnectable(ConnectionWidget connectionWidget)
	{
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
		Object object = canvas.findObject(connectionWidget);
		edge = canvas.isEdge(object) ? (String) object : null;
		originalNode = edge != null ? canvas.getEdgeTarget(edge) : null;
		return originalNode != null;
	}

	@Override
	public void reconnect(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource)
	{
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
		if (replacementWidget == null)
		{
			canvas.removeEdge(edge);
			monitor.firePropertyChange("undoable", null, CommandFactory.createDisconnectionCommand());
		}
		else if (reconnectingSource)
		{

			canvas.setEdgeSource(edge, replacementNode);
			monitor.firePropertyChange("undoable", null, CommandFactory.createConnectionCommand());

		}
		else
		{

			canvas.setEdgeTarget(edge, replacementNode);
			monitor.firePropertyChange("undoable", null, CommandFactory.createConnectionCommand());

		}
	}

	@Override
	public void reconnectingFinished(ConnectionWidget connectionWidget, boolean reconnectingSource)
	{
	}

	@Override
	public void reconnectingStarted(ConnectionWidget connectionWidget, boolean reconnectingSource)
	{
	}

	@Override
	public Widget resolveReplacementWidget(Scene scene, Point sceneLocation)
	{
		return null;
	}

}
