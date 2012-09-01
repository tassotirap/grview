package org.grview.canvas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.grview.canvas.UnidirectionalAnchor.UnidirectionalAnchorKind;
import org.grview.canvas.action.GridProvider;
import org.grview.canvas.action.LineProvider;
import org.grview.canvas.action.NodeSelectProvider;
import org.grview.canvas.action.WidgetActionRepository;
import org.grview.canvas.state.CanvasState;
import org.grview.canvas.state.Connection;
import org.grview.canvas.state.Node;
import org.grview.canvas.widget.GridWidget;
import org.grview.canvas.widget.GuideLineWidget;
import org.grview.canvas.widget.LabelWidgetExt;
import org.grview.canvas.widget.LineWidget;
import org.grview.canvas.widget.MarkedWidget;
import org.grview.canvas.widget.TypedWidget;
import org.grview.util.Log;
import org.netbeans.api.visual.anchor.Anchor.Direction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

public class CanvasTemplate extends Canvas
{

	private Router activeRouter;
	private LayerWidget backgroundLayer = new LayerWidget(this);
	private LayerWidget connectionLayer = new LayerWidget(this);

	private String connStrategy;
	private String cursor;
	private LayerWidget interractionLayer = new LayerWidget(this);

	private LayerWidget mainLayer = new LayerWidget(this);

	private String moveStrategy;

	private ArrayList<String> removedEdges = new ArrayList<String>();

	private CanvasState state;

	public CanvasTemplate(String cursor, String connectionStrategy, String movementStrategy, WidgetActionRepository actions, CanvasDecorator decorator)
	{
		super(cursor, connectionStrategy, movementStrategy, actions, decorator);
		this.connStrategy = connectionStrategy;
		this.moveStrategy = movementStrategy;
		this.cursor = cursor;
	}

	@Override
	protected void attachEdgeSourceAnchor(String edge, String oldSourceNode, String sourceNode)
	{
		Widget w = sourceNode != null ? findWidget(sourceNode) : null;
		ConnectionWidget conn = ((ConnectionWidget) findWidget(edge));
		if (isSuccessor(edge))
		{

			conn.setSourceAnchor(new UnidirectionalAnchor(w, edge, true, UnidirectionalAnchorKind.RIGHT));
		}
		else if (isAlternative(edge))
		{
			conn.setSourceAnchor(new UnidirectionalAnchor(w, edge, true, UnidirectionalAnchorKind.BOTTOM));
		}
		else
		{
			conn.setSourceAnchor(AnchorFactory.createRectangularAnchor(w));
		}
	}

	@Override
	protected void attachEdgeTargetAnchor(String edge, String oldTargetNode, String targetNode)
	{
		Widget w = targetNode != null ? findWidget(targetNode) : null;
		ConnectionWidget conn = ((ConnectionWidget) findWidget(edge));
		if (isSuccessor(edge))
		{
			conn.setTargetAnchor(new UnidirectionalAnchor(w, edge, UnidirectionalAnchorKind.LEFT, 0, false, Direction.TOP));
		}
		else if (isAlternative(edge))
		{
			conn.setTargetAnchor(new UnidirectionalAnchor(w, edge, true, UnidirectionalAnchorKind.TOP));
		}
		else
		{
			conn.setTargetAnchor(AnchorFactory.createRectangularAnchor(w));
		}
	}

	@Override
	protected Widget attachEdgeWidget(String edge)
	{
		ConnectionWidget connection = null;
		String activeTool = getCanvasActiveTool();

		if (activeTool.equals(CanvasData.SUCCESSOR) || activeTool.equals(CanvasData.ALTERNATIVE))
		{
			connection = decorator.drawConnection(activeTool, this, edge);
			connection.setRouter(getActiveRouter());
			connection.createActions(CanvasData.SELECT).addAction(actions.getAction("ConnSelect", this));
			connection.getActions(CanvasData.SELECT).addAction(actions.getAction("Reconnect", this));			
			connectionLayer.addChild(connection);
			if (activeTool.equals(CanvasData.SUCCESSOR))
			{
				if (!isSuccessor(edge))
				{
					getSuccessors().add(edge);
				}
			}
			else if (activeTool.equals(CanvasData.ALTERNATIVE))
			{
				if (!isAlternative(edge))
				{
					getAlternatives().add(edge);
				}
			}
		}
		return connection;
	}

	@Override
	protected Widget attachNodeWidget(String node)
	{
		Widget widget = null;
		String activeTool = getCanvasActiveTool();

		if (node.startsWith(LineWidget.class.getCanonicalName()))
		{
			LineWidget lWidget = new LineWidget(this);
			backgroundLayer.addChild(lWidget);
			widget = lWidget;
			setShowingLines(true);
			state.getPreferences().setShowLines(true);
		}
		else if (node.startsWith(GuideLineWidget.class.getCanonicalName()))
		{
			GuideLineWidget glWidget = new GuideLineWidget(this);
			backgroundLayer.addChild(glWidget);
			widget = glWidget;
			setShowingGuide(true);
			state.getPreferences().setShowGuide(true);
		}
		else if (node.startsWith(GridWidget.class.getCanonicalName()))
		{
			GridWidget gWidget = new GridWidget(this);
			backgroundLayer.addChild(gWidget);
			widget = gWidget;
			setShowingGrid(true);
			state.getPreferences().setShowGrid(true);
		}
		else if (!activeTool.equals(CanvasData.SELECT))
		{
			String tool = activeTool;
			if (tool.equals(CanvasData.N_TERMINAL) || tool.equals(CanvasData.TERMINAL) || tool.equals(CanvasData.LEFT_SIDE) || tool.equals(CanvasData.LAMBDA) || tool.equals(CanvasData.START))
			{
				// decide witch image the widget will use
				try
				{
					widget = decorator.drawIcon(activeTool, this, node);
				}
				catch (Exception e)
				{
					Log.log(Log.ERROR, this, "Could not create widget!", e);
					widget = new LabelWidgetExt(mainLayer.getScene(), node);
				}
				widget.createActions(CanvasData.SELECT);
				if (!tool.equals(CanvasData.LAMBDA))
					widget.getActions(CanvasData.SELECT).addAction(actions.getAction("NodeHover", this));
				widget.getActions(CanvasData.SELECT).addAction(actions.getAction("Select", this));
				if (!tool.equals(CanvasData.LAMBDA))
					widget.getActions(CanvasData.SELECT).addAction(actions.getAction("Editor", this));
				widget.getActions(CanvasData.SELECT).addAction(actions.getAction("Move", this));
				if (!tool.equals(CanvasData.LAMBDA))
					widget.createActions(CanvasData.SUCCESSOR).addAction(actions.getAction("Successor", this));
				if (!tool.equals(CanvasData.LAMBDA))
					widget.createActions(CanvasData.ALTERNATIVE).addAction(actions.getAction("Alternative", this));
				mainLayer.addChild(widget);
				if (tool.equals(CanvasData.LEFT_SIDE))
				{
					leftSides.add(node);
				}
				else if (tool.equals(CanvasData.TERMINAL))
				{
					terminals.add(node);
				}
				else if (tool.equals(CanvasData.N_TERMINAL))
				{
					nterminals.add(node);
				}
				else if (tool.equals(CanvasData.LAMBDA))
				{
					lambdas.add(node);
				}
				else if (tool.equals(CanvasData.START))
				{
					start.add(node);
				}
			}
			else if (tool.equals(CanvasData.LABEL))
			{
				widget = new LabelWidgetExt(mainLayer.getScene(), "Double Click Here to Edit");
				widget.createActions(CanvasData.SELECT).addAction(actions.getAction("LabelHover", this));
				widget.getActions(CanvasData.SELECT).addAction(actions.getAction("SelectLabel", this));
				widget.getActions(CanvasData.SELECT).addAction(actions.getAction("Editor", this));
				widget.getActions(CanvasData.SELECT).addAction(actions.getAction("Move", this));
				labels.add(node);
				mainLayer.addChild(widget);
			}
			if (widget != null)
			{
				if (widget instanceof TypedWidget)
				{
					((TypedWidget) widget).setType(tool);
				}
			}
		}
		return widget;
	}

	@Override
	public Router getActiveRouter()
	{
		return activeRouter;
	}

	@Override
	public LayerWidget getBackgroundLayer()
	{
		return backgroundLayer;
	}

	@Override
	public CanvasDecorator getCanvasDecorator()
	{
		return decorator;
	}

	@Override
	public CanvasState getCanvasState()
	{
		return state;
	}

	@Override
	public LayerWidget getConnectionLayer()
	{
		return connectionLayer;
	}

	@Override
	public String getConnStrategy()
	{
		return this.connStrategy;
	}

	@Override
	public LayerWidget getInterractionLayer()
	{
		return interractionLayer;
	}

	@Override
	public Collection<?> getLabels()
	{
		return labels;
	}

	@Override
	public LayerWidget getMainLayer()
	{
		return mainLayer;
	}

	@Override
	public String getMoveStrategy()
	{
		return moveStrategy;
	}

	/* ##########################GETTERS AND SETTERS############################ */

	@Override
	public String getNodeType(Object node)
	{
		Widget w = findWidget(node);
		if (w != null)
		{
			if (w instanceof LabelWidgetExt)
			{
				return ((LabelWidgetExt) w).getType();
			}
		}
		return null;
	}

	@Override
	public void init(CanvasState state)
	{
		super.init(state);
		this.state = state;

		addChild(backgroundLayer);
		addChild(mainLayer);
		addChild(connectionLayer);
		addChild(interractionLayer);

		createActions(CanvasData.SELECT).addAction(actions.getAction(WidgetActionRepository.SELECT, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.SELECT_LABEL, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.CREATE, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.POPUP_MENU_MAIN, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.NODE_HOVER, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.LABEL_HOVER, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.MOUSE_CENTERED_ZOOM, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.PAN, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.RECTANGULAR_SELECT, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.DELETE, this));
		getActions().addAction(actions.getAction(WidgetActionRepository.COPY_PASTE, this));
		setActiveTool(cursor);

		if (state.getPreferences().getConnectionStrategy() != null)
		{
			setConnStrategy(state.getPreferences().getConnectionStrategy());
		}
		else
		{
			setConnStrategy(connStrategy);
		}
		if (state.getPreferences().getMoveStrategy() != null)
		{
			setMoveStrategy(state.getPreferences().getMoveStrategy());
		}
		else
		{
			setMoveStrategy(moveStrategy);
		}
	}

	@Override
	public boolean isLabel(Object o)
	{
		return labels.contains(o);
	}

	public boolean isLambda(Object o)
	{
		return this.lambdas.contains(o);
	}

	public boolean isLeftSide(Object o)
	{
		return this.leftSides.contains(o);
	}

	@Override
	public boolean isNode(Object o)
	{
		if (!isLabel(o) && (isTerminal(o) || isNonTerminal(o) || isLambda(o) || isLeftSide(o) || isStart(o)))
		{
			return super.isNode(o);
		}
		return false;
	}

	public boolean isNonTerminal(Object o)
	{
		return this.nterminals.contains(o);
	}

	public boolean isStart(Object o)
	{
		return this.start.contains(o);
	}

	public boolean isTerminal(Object o)
	{
		return this.terminals.contains(o);
	}

	@Override
	public void removeEdgeSafely(String edge)
	{
		if (isEdge(edge))
		{
			if (getSuccessors().contains(edge))
			{
				getSuccessors().remove(edge);
			}
			if (getAlternatives().contains(edge))
			{
				getAlternatives().remove(edge);
			}
			super.removeEdge(edge);
		}
	}

	@Override
	public void removeNodeSafely(String node)
	{
		if (isLabel(node))
		{
			labels.remove(node);
		}
		super.removeNode(node);
		if (node.startsWith(GridWidget.class.getCanonicalName()))
		{
			setShowingGrid(false);
		}
		else if (node.startsWith(LineWidget.class.getCanonicalName()))
		{
			setShowingLines(false);
		}
		else if (node.startsWith(GuideLineWidget.class.getCanonicalName()))
		{
			setShowingGuide(false);
		}
	}

	/**
	 * Direct select method, not to be called after direct user action
	 */
	@Override
	public void select(Object object)
	{
		select(object, false);
	}

	@Override
	public void select(Object object, boolean invertSelection)
	{
		Widget w;
		if ((w = findWidget(object)) != null)
		{
			NodeSelectProvider nsp = new NodeSelectProvider(this);
			if (!getSelectedObjects().contains(w))
			{
				if (nsp.isSelectionAllowed(w, null, invertSelection))
					nsp.select(w, null, invertSelection);
				validate();
			}
		}
	}

	@Override
	public void setConnStrategy(String policy)
	{
		if (policy != null)
		{
			state.getPreferences().setConnectionStrategy(policy);
			if (policy.equals(CanvasData.R_ORTHOGONAL) || policy.equals(CanvasData.R_FREE) || policy.equals(CanvasData.R_DIRECT))
			{
				if (!policy.equals(connStrategy) || activeRouter == null)
				{
					connStrategy = policy;
					if (policy.equals(CanvasData.R_ORTHOGONAL))
					{
						activeRouter = RouterFactory.createOrthogonalSearchRouter(mainLayer);
					}
					else if (policy.equals(CanvasData.R_DIRECT))
					{
						activeRouter = RouterFactory.createDirectRouter();
					}
					else if (policy.equals(CanvasData.R_FREE))
					{
						activeRouter = RouterFactory.createFreeRouter();
					}
					for (String edge : getEdges())
					{
						ConnectionWidget cw = (ConnectionWidget) findWidget(edge);
						if (cw != null)
						{
							cw.setRouter(activeRouter);
							cw.calculateRouting();
						}
					}
					repaint();
				}
			}
		}
	}

	@Override
	public void setMoveStrategy(String strategy)
	{
		MoveTracker mt = new MoveTracker(this, actions);
		if (strategy != null)
		{
			state.getPreferences().setMoveStrategy(strategy);
			if (strategy.equals(CanvasData.M_ALIGN) || strategy.equals(CanvasData.M_SNAP) || strategy.equals(CanvasData.M_FREE) || strategy.equals(CanvasData.M_LINES))
			{
				if (moveStrategy == null || !moveStrategy.equals(strategy))
				{
					moveStrategy = strategy;
					mt.notifyObservers(strategy);
				}
			}
		}
	}

	// /* ### PRIVATE NESTED CLASSES ############################ */

	@Override
	public void updateState(CanvasState state)
	{
		// update the nodes
		Set<String> nodes = state.getNodes();
		// remove nodes
		ArrayList<Object> nToRemove = new ArrayList<Object>();
		for (Object node : this.getNodes())
		{
			if (!nodes.contains(node))
			{
				nToRemove.add(node);
			}
			else
			{
				// update labels
				Widget w1 = this.findWidget(node);
				Node n1 = state.findNode(node);
				if (w1 instanceof LabelWidgetExt)
				{
					((LabelWidgetExt) w1).setLabel(n1.getTitle());
					if (n1.getLocation() != null)
						w1.setPreferredLocation(mainLayer.convertLocalToScene(n1.getLocation()));
				}
				if (w1 instanceof MarkedWidget)
				{
					((MarkedWidget) w1).setMark(n1.getMark());
				}
				if (w1 instanceof TypedWidget)
				{
					((TypedWidget) w1).setType(n1.getType());
				}
			}
		}
		for (Object n : nToRemove)
		{
			removeNode((String) n);
		}
		// add nodes
		for (Object n : nodes)
		{
			if (!this.getNodes().contains(n))
			{
				Node n1 = state.findNode(n);
				String oldTool = getCanvasActiveTool();
				setActiveTool((String) state.getType(n));
				Widget w1 = addNode((String) n);
				if (n1.getLocation() != null)
					w1.setPreferredLocation(mainLayer.convertLocalToScene(n1.getLocation()));
				if (w1 instanceof LabelWidgetExt)
				{
					((LabelWidgetExt) w1).setLabel(n1.getTitle());
				}
				if (w1 instanceof MarkedWidget)
				{
					((MarkedWidget) w1).setMark(n1.getMark());
				}
				if (w1 instanceof TypedWidget)
				{
					((TypedWidget) w1).setType(n1.getType());
				}
				setActiveTool(oldTool);
			}
		}
		// update connections
		List<String> conn = state.getConnections();
		// remove connections
		ArrayList<Object> cToRemove = new ArrayList<Object>();
		for (Object e : this.getEdges())
		{
			if (!conn.contains(e))
			{
				cToRemove.add(e);
			}
		}
		removedEdges.clear();
		for (Object e : cToRemove)
		{
			removedEdges.add((String) e);
			if (leftSides.contains(e))
			{
				leftSides.remove(e);
			}
			removeEdge((String) e);
		}
		// add connections
		// first pass, to connect first connections from initial non-terminals
		for (Object e : conn)
		{
			if (!this.getEdges().contains(e))
			{
				Connection c1 = state.findConnection(e);
				// Conection with problem
				if (c1.getSource() == null || c1.getTarget() == null)
				{
					state.objectRemoved(null, e);
				}
				else if (state.findNode(c1.getSource()).getType().equals(CanvasData.LEFT_SIDE) || state.findNode(c1.getSource()).getType().equals(CanvasData.START))
				{
					String oldTool = getCanvasActiveTool();
					setActiveTool((String) state.getType(e));
					addEdge((String) e);
					setEdgeSource((String) e, c1.getSource());
					setEdgeTarget((String) e, c1.getTarget());
					setActiveTool(oldTool);
				}
			}
		}

		// update connections
		conn = state.getConnections();
		for (Object e : conn)
		{
			if (!this.getEdges().contains(e))
			{
				Connection c1 = state.findConnection(e);
				if (c1.getSource() == null || c1.getTarget() == null)
				{
					state.objectRemoved(null, e);
				}
				if (!state.findNode(c1.getSource()).getType().equals(CanvasData.LEFT_SIDE) && !state.findNode(c1.getSource()).getType().equals(CanvasData.START))
				{
					String oldTool = getCanvasActiveTool();
					setActiveTool((String) state.getType(e));
					addEdge((String) e);
					setEdgeSource((String) e, c1.getSource());
					setEdgeTarget((String) e, c1.getTarget());
					setActiveTool(oldTool);
				}
			}
		}
		if (state.getPreferences().isShowGrid())
		{
			GridProvider.getInstance(this).setVisible(true);
		}
		if (state.getPreferences().isShowGuide())
		{
			LineProvider.getInstance(this).setGuideVisible(true);
		}
		if (state.getPreferences().isShowLines())
		{
			LineProvider.getInstance(this).populateCanvas();
		}
		setConnStrategy(state.getPreferences().getConnectionStrategy());
		setMoveStrategy(state.getPreferences().getMoveStrategy());
		removeObjectSceneListener(this.state, ObjectSceneEventType.OBJECT_ADDED);
		removeObjectSceneListener(this.state, ObjectSceneEventType.OBJECT_REMOVED);
		addObjectSceneListener(state, ObjectSceneEventType.OBJECT_ADDED);
		addObjectSceneListener(state, ObjectSceneEventType.OBJECT_REMOVED);
		this.state = state;

		state.update(this);

		validate();
	}
}
