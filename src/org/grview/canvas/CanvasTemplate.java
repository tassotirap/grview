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
import org.grview.canvas.strategy.MoveStrategy;
import org.grview.canvas.widget.GridWidget;
import org.grview.canvas.widget.GuideLineWidget;
import org.grview.canvas.widget.LabelWidgetExt;
import org.grview.canvas.widget.LineWidget;
import org.grview.canvas.widget.MarkedWidget;
import org.grview.canvas.widget.TypedWidget;
import org.grview.util.Log;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.Anchor.Direction;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;


public class CanvasTemplate extends Canvas {
    
	private static final long serialVersionUID = -6620884339324342510L;
	private LayerWidget backgroundLayer = new LayerWidget (this);
    private LayerWidget mainLayer = new LayerWidget (this);
    private LayerWidget connectionLayer = new LayerWidget (this);
    private LayerWidget interractionLayer = new LayerWidget (this);
    
    //additional types of cursor that can be created by the user
    //private List<String> cursors = new ArrayList<String>();
    
    private String connStrategy;
    private String moveStrategy;
    private Router activeRouter;
    
    private String cursor;
    
    //object representing the current state of this canvas 
    private CanvasState state;
    
    private ArrayList<String> removedEdges = new ArrayList<String>();
    
	public CanvasTemplate(String cursor, String connectionStrategy, String movementStrategy, WidgetActionRepository actions, CanvasDecorator decorator, String id) {
		super(cursor, connectionStrategy, movementStrategy, actions, decorator, id);
		this.connStrategy = connectionStrategy;
		this.moveStrategy = movementStrategy;
		this.cursor = cursor;
	}
    
	@Override
	public void init(CanvasState state) {
		super.init(state);
		this.state = state;

		addChild (backgroundLayer);
	    addChild (mainLayer);
	    addChild (connectionLayer);
	    addChild (interractionLayer);
        
	    createActions(SELECT).addAction(actions.getAction(WidgetActionRepository.SELECT, this));
	    getActions ().addAction (actions.getAction(WidgetActionRepository.SELECT_LABEL, this));
	    getActions ().addAction (actions.getAction(WidgetActionRepository.CREATE, this));
	    getActions ().addAction (actions.getAction(WidgetActionRepository.POPUP_MENU_MAIN, this));
	    getActions ().addAction	(actions.getAction(WidgetActionRepository.NODE_HOVER, this));
	    getActions ().addAction (actions.getAction(WidgetActionRepository.LABEL_HOVER, this));
	    getActions ().addAction (actions.getAction(WidgetActionRepository.MOUSE_CENTERED_ZOOM, this));
	    getActions ().addAction (actions.getAction(WidgetActionRepository.PAN, this));
	    getActions ().addAction (actions.getAction(WidgetActionRepository.RECTANGULAR_SELECT, this));
	    getActions ().addAction (actions.getAction(WidgetActionRepository.DELETE, this));
	    getActions ().addAction (actions.getAction(WidgetActionRepository.COPY_PASTE, this));
	    setActiveTool(cursor);

	    if (state.getPreferences().getConnectionStrategy() != null) {
	    	setConnStrategy(state.getPreferences().getConnectionStrategy());
	    }
	    else {
	    	setConnStrategy(connStrategy);
	    }
	    if (state.getPreferences().getMoveStrategy() != null) {
	    	setMoveStrategy(state.getPreferences().getMoveStrategy());
	    }
	    else {
	    	setMoveStrategy(moveStrategy);
	    }
	}
	
	@Override
	public void updateState(CanvasState state) {
		//update the nodes
		Set<String> nodes = state.getNodes();
		//remove nodes
		ArrayList<Object> nToRemove = new ArrayList<Object>();
		for (Object n : this.getNodes()) {
			if (!nodes.contains(n)) {
				nToRemove.add(n);
			}
			else {
				//update labels
				Widget w1 = this.findWidget(n);
				Node n1 = state.findNode(n);
				if (w1 instanceof LabelWidgetExt) {
					((LabelWidgetExt)w1).setLabel(n1.getTitle());
					if (n1.getLocation() != null)
					w1.setPreferredLocation(mainLayer.convertLocalToScene(n1.getLocation()));
				}
				if (w1 instanceof MarkedWidget) {
					((MarkedWidget)w1).setMark(n1.getMark());
				}
				if (w1 instanceof TypedWidget) {
					((TypedWidget)w1).setType(n1.getType());
				}
			}
		}
		for (Object n : nToRemove) {
			removeNode((String)n);
		}
		//add nodes
		for (Object n : nodes) {
			if (!this.getNodes().contains(n)) {
				Node n1 = state.findNode(n);
				String oldTool = _getActiveTool();
				setActiveTool((String)state.getType(n));
				Widget w1 = addNode((String)n);
				if (n1.getLocation() != null)
				w1.setPreferredLocation(mainLayer.convertLocalToScene(n1.getLocation()));
				if (w1 instanceof LabelWidgetExt) {
					((LabelWidgetExt)w1).setLabel(n1.getTitle());
				}
				if (w1 instanceof MarkedWidget) {
					((MarkedWidget)w1).setMark(n1.getMark());
				}
				if (w1 instanceof TypedWidget) {
					((TypedWidget)w1).setType(n1.getType());
				}
				setActiveTool(oldTool);
			}
		}
		//update connections
		List<String> conn = state.getConnections();
		//remove connections
		ArrayList<Object> cToRemove = new ArrayList<Object>();
		for (Object e : this.getEdges()) {
			if (!conn.contains(e)) {
				cToRemove.add(e);
			}
		}
		removedEdges.clear();
		for (Object e : cToRemove) {
			removedEdges.add((String)e);
			if (leftSides.contains(e)) {
				leftSides.remove(e);
			}
			removeEdge((String)e);
		}
		//add connections
		//first pass, to connect first connections from initial non-terminals
		for (Object e : conn) {
			if (!this.getEdges().contains(e)) {
				Connection c1 = state.findConnection(e);
				if (state.findNode(c1.getSource()).getType().equals(LEFT_SIDE) ||
						state.findNode(c1.getSource()).getType().equals(START)) {
					String oldTool = _getActiveTool();
					setActiveTool((String)state.getType(e));
					addEdge((String)e);
					setEdgeSource((String)e, c1.getSource());
					setEdgeTarget((String)e, c1.getTarget());
					setActiveTool(oldTool);
				}
			}
		}
		for (Object e : conn) {
			if (!this.getEdges().contains(e)) {
				Connection c1 = state.findConnection(e);
				if (!state.findNode(c1.getSource()).getType().equals(LEFT_SIDE) &&
						!state.findNode(c1.getSource()).getType().equals(START)) {
					String oldTool = _getActiveTool();
					setActiveTool((String)state.getType(e));
					addEdge((String)e);
					setEdgeSource((String)e, c1.getSource());
					setEdgeTarget((String)e, c1.getTarget());
					setActiveTool(oldTool);
				}
			}
		}
		if (state.getPreferences().isShowGrid()) {
			GridProvider.getInstance(this).setVisible(true);
		}
		if (state.getPreferences().isShowGuide()) {
			LineProvider.getInstance(this).setGuideVisible(true);
		}
		if (state.getPreferences().isShowLines()) {
			LineProvider.getInstance(this).populateCanvas();
		}
		setConnStrategy(state.getPreferences().getConnectionStrategy());
		setMoveStrategy(state.getPreferences().getMoveStrategy());
		removeObjectSceneListener(this.state, ObjectSceneEventType.OBJECT_ADDED);
		removeObjectSceneListener(this.state, ObjectSceneEventType.OBJECT_REMOVED);
	    addObjectSceneListener(state, ObjectSceneEventType.OBJECT_ADDED);
	    addObjectSceneListener(state, ObjectSceneEventType.OBJECT_REMOVED);
		this.state = state;
		validate();
	}
	
	@Override
	protected void attachEdgeSourceAnchor (String edge, String oldSourceNode, String sourceNode) {
        Widget w = sourceNode != null ? findWidget (sourceNode) : null;
        ConnectionWidget conn = ((ConnectionWidget) findWidget (edge));
        if (isCandidateSuccessor(edge) || isSuccessor(edge)) {
        	
        	conn.setSourceAnchor (new UnidirectionalAnchor(w, edge, true, UnidirectionalAnchorKind.RIGHT));
        }
        else if (isCandidateAlternative(edge) || isAlternative(edge)) {
        	conn.setSourceAnchor (new UnidirectionalAnchor(w, edge, true, UnidirectionalAnchorKind.BOTTOM));
        }
        else {
        	conn.setSourceAnchor (AnchorFactory.createRectangularAnchor (w));
        }
	}
	
	@Override
    protected void attachEdgeTargetAnchor (String edge, String oldTargetNode, String targetNode) {
        Widget w = targetNode != null ? findWidget (targetNode) : null;
        ConnectionWidget conn = ((ConnectionWidget) findWidget (edge));
    //    boolean hasConn = false;
        if (isCandidateSuccessor(edge) || isSuccessor(edge)) {
//        	if (!removedEdges.contains(edge)) {
//	        	for (String st : findNodeEdges(targetNode, false, true)) {
//	        		if (!st.equals(edge)) {
//	        			hasConn = true;
//	        			break;
//	        		}
//	        	}
//	        	
//        	}
//        	if (leftSides.contains(targetNode)) {
//        		if (lastEdgeSource != null) {
//        			//Widget w2 = findWidget (lastEdgeSource);
//        			UnidirectionalAnchor ua = new UnidirectionalAnchor(w, conn, UnidirectionalAnchorKind.LEFT, 0, false, Direction.TOP);
//        			//ua.setSecundaryReference(w2);
//        			conn.setTargetAnchor(ua);
//        			return;
//        		}
//        	}
//        	if (!hasConn) {
//        		conn.setTargetAnchor (new UnidirectionalAnchor(w, conn, true, UnidirectionalAnchorKind.LEFT));
//        	}
//        	else {
        		conn.setTargetAnchor (new UnidirectionalAnchor(w, edge, UnidirectionalAnchorKind.LEFT, 0, false, Direction.TOP) );
//        	}
        }
        else if (isCandidateAlternative(edge) || isAlternative(edge)) {
        	conn.setTargetAnchor (new UnidirectionalAnchor(w, edge, true, UnidirectionalAnchorKind.TOP));
        }
        else {
        	conn.setTargetAnchor (AnchorFactory.createRectangularAnchor (w));
        }
    }
	
	@Override
	protected Widget attachEdgeWidget(String edge) {
        ConnectionWidget connection = null;
        String activeTool = _getActiveTool();
        
        if (activeTool.equals(SUCCESSOR) || activeTool.equals(ALTERNATIVE))
        {
    		connection = decorator.drawConnection(activeTool, this, edge);
    		connection.setRouter(getActiveRouter());
	        connection.createActions(SELECT).addAction (actions.getAction("ConnSelect", this));
	        connection.getActions(SELECT).addAction (actions.getAction("Reconnect", this));
	        connection.getActions(SELECT).addAction (actions.getAction("FreeMoveCP", this));
	        connection.getActions(SELECT).addAction (actions.getAction("AddRemoveCP", this));
	        connectionLayer.addChild (connection);
	        if (activeTool.equals(SUCCESSOR)) {
	        	if (!isSuccessor(edge)) {
	        		getSuccessors().add(edge);
	        	}
	        	if (isCandidateSuccessor(edge)) {
	        		getCandidateSuc().remove(edge);
	        	}
	        }
	        else if (activeTool.equals(ALTERNATIVE)) {
	        	if (!isAlternative(edge)) {
	        		getAlternatives().add(edge);
	        	}
	        	if (isCandidateAlternative(edge)) {
	        		getCandidateAlt().remove(edge);
	        	}
	        }
        }
        return connection;
	}
	
	@Override
	protected Widget attachNodeWidget(String node) {
		Widget widget = null;
		String activeTool = _getActiveTool();
		
		if (node.startsWith(LineWidget.class.getCanonicalName())) {
			LineWidget lWidget = new LineWidget(this);
			backgroundLayer.addChild(lWidget);
			widget = lWidget;
			setShowingLines(true);
			state.getPreferences().setShowLines(true);
		}
		else if (node.startsWith(GuideLineWidget.class.getCanonicalName())) {
			GuideLineWidget glWidget = new GuideLineWidget(this);
			backgroundLayer.addChild(glWidget);
			widget = glWidget;
			setShowingGuide(true);
			state.getPreferences().setShowGuide(true);
		}
		else if (node.startsWith(GridWidget.class.getCanonicalName())) {
			GridWidget gWidget = new GridWidget(this);
			backgroundLayer.addChild(gWidget);
			widget = gWidget;
			setShowingGrid(true);
			state.getPreferences().setShowGrid(true);
		}
		else if (!activeTool.equals(SELECT)) {
			String tool = activeTool;
			if (tool.equals(N_TERMINAL) || tool.equals(TERMINAL) ||
					tool.equals(LEFT_SIDE) || tool.equals(LAMBDA) ||
					tool.equals(START)) {
				//decide witch image the widget will use
		    	try
		    	{
		    		widget = decorator.drawIcon(activeTool, this, node);
		    	} 
		    	catch (Exception e) {
		    		Log.log(Log.ERROR, this, "Could not create widget!", e);
		    		widget = new LabelWidgetExt (mainLayer.getScene (), node);
		    	}
		    	widget.createActions(SELECT);
		    	if (!tool.equals(LAMBDA)) widget.getActions(SELECT).addAction(actions.getAction("NodeHover", this));
		    	widget.getActions(SELECT).addAction(actions.getAction("Select", this));
	        	if (!tool.equals(LAMBDA)) widget.getActions(SELECT).addAction(actions.getAction("Editor", this));
	        	widget.getActions(SELECT).addAction(actions.getAction("Move", this));
	        	if (!tool.equals(LAMBDA))	widget.createActions(SUCCESSOR).addAction(actions.getAction("Successor", this));
	        	if (!tool.equals(LAMBDA)) widget.createActions(ALTERNATIVE).addAction(actions.getAction("Alternative", this));
	    		mainLayer.addChild(widget);
		    	if (tool.equals(LEFT_SIDE)) {
		    		leftSides.add(node);
		    	}
		    	else if (tool.equals(TERMINAL)) {
		    		terminals.add(node);
		    	}
		    	else if (tool.equals(N_TERMINAL)) {
		    		nterminals.add(node);
		    	}
		    	else if (tool.equals(LAMBDA)) {
		    		lambdas.add(node);
		    	}
		    	else if (tool.equals(START)) {
		    		start.add(node);
		    	}
			}
			else if (tool.equals(LABEL)) {
				widget = new LabelWidgetExt(mainLayer.getScene(), "Double Click Here to Edit");
				widget.createActions(SELECT).addAction(actions.getAction("LabelHover", this));
				widget.getActions(SELECT).addAction(actions.getAction("SelectLabel", this));
				widget.getActions(SELECT).addAction(actions.getAction("Editor", this));
				widget.getActions(SELECT).addAction(actions.getAction("Move", this));
				labels.add(node);
				mainLayer.addChild(widget);
				/////////////// customNodes.add(node);
			}
			if (widget != null) {
				if (widget instanceof TypedWidget) {
					((TypedWidget) widget).setType(tool);
				}
			}
		}
		return widget;
	}
	
	@Override
	public boolean isLabel(Object o) {
		return labels.contains(o);
	}
	
	public boolean isTerminal(Object o) {
		return this.terminals.contains(o);
	}
	
	public boolean isLeftSide(Object o) {
		return this.leftSides.contains(o);
	}
	
	public boolean isStart(Object o) {
		return this.start.contains(o);
	}
	
	public boolean isNonTerminal(Object o) {
		return this.nterminals.contains(o);
	}
	
	public boolean isLambda(Object o) {
		return this.lambdas.contains(o);
	}
	
	@Override
	public boolean isNode(Object o) {
		if (!isLabel(o) && 
			(isTerminal(o) || isNonTerminal(o) || isLambda(o) || isLeftSide(o) || isStart(o))) {
			return super.isNode(o);
		}
		return false;
	}
	
	@Override
	public void removeNodeSafely(String node) {
		if (isLabel(node)) {
				labels.remove(node);
		}
		super.removeNode(node);
		if (node.startsWith(GridWidget.class.getCanonicalName())) {
			setShowingGrid(false);
		}
		else if (node.startsWith(LineWidget.class.getCanonicalName())) {
			setShowingLines(false);
		}
		else if (node.startsWith(GuideLineWidget.class.getCanonicalName())) {
			setShowingGuide(false);
		}
	}
	
	@Override
	public void removeEdgeSafely(String edge) {
		if (isEdge(edge)) {
			if (getSuccessors().contains(edge)) getSuccessors().remove(edge);
			if (getCandidateSuc().contains(edge)) getCandidateSuc().remove(edge);
			if (getAlternatives().contains(edge)) getAlternatives().remove(edge);
			if (getCandidateAlt().contains(edge)) getCandidateAlt().remove(edge);
			super.removeEdge(edge);
		}
	}

	
	/*##########################GETTERS AND SETTERS############################*/

	@Override
	public CanvasState getCanvasState() {
		return state;
	}
	
	@Override
	public Collection<?> getLabels() {
		return labels;
	}
	
	@Override
	public void setConnStrategy(String policy) {
		if (policy != null) {
			state.getPreferences().setConnectionStrategy(policy);
			if (policy.equals(R_ORTHOGONAL) || policy.equals(R_FREE) || policy.equals(R_DIRECT)) {
				if (!policy.equals(connStrategy) || activeRouter == null) {
					connStrategy = policy;
		            if ( policy.equals(R_ORTHOGONAL)) {
		            	activeRouter = RouterFactory.createOrthogonalSearchRouter (mainLayer);
		            }
		            else if (policy.equals(R_DIRECT)) {
		            	activeRouter = RouterFactory.createDirectRouter();
		            }
		            else if (policy.equals(R_FREE)) {
		            	activeRouter = RouterFactory.createFreeRouter();
		            }
		            for (String edge : getEdges()) {
		            	ConnectionWidget cw = (ConnectionWidget)findWidget(edge);
		            	if (cw != null) {
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
	public String getConnStrategy() {
		return this.connStrategy;
	}
	
	@Override
	public void setMoveStrategy(String strategy) {
		MoveTracker mt = MoveTracker.getInstance(this, actions);
		if (strategy != null) {
			state.getPreferences().setMoveStrategy(strategy);
			if (strategy.equals(M_ALIGN) || strategy.equals(M_SNAP) 
					|| strategy.equals(M_FREE) || strategy.equals(M_LINES)) {
				if (moveStrategy == null || !moveStrategy.equals(strategy)) {
					moveStrategy = strategy;
					mt.notifyObservers(strategy);
				}
			}
		}
	}
	
	@Override
	public String getMoveStrategy() {
		return moveStrategy;
	}
	
	@Override
	public Router getActiveRouter()
	{
		return activeRouter;
	}
	
	@Override
	public LayerWidget getBackgroundLayer() {
		return backgroundLayer;
	}
	
	@Override
	public LayerWidget getMainLayer() {
		return mainLayer;
	}
	
	@Override
	public LayerWidget getInterractionLayer() {
		return interractionLayer;
	}
	
	@Override
	public LayerWidget getConnectionLayer() {
		return connectionLayer;
	}
	
	@Override
	public CanvasDecorator getCanvasDecorator() {
		return decorator;
	}
	
	@Override
	public String getNodeType(Object node) {
		Widget w = findWidget(node);
		if (w != null) {
			if (w instanceof LabelWidgetExt) {
				return ((LabelWidgetExt)w).getType();
			}
		}
		return null;
	}
	
	/**
	 * Direct select method, not to be called after direct user action
	 */
	@Override
	public void select(Object object) {
		select(object, false);
	}
	
	@Override
	public void select(Object object, boolean invertSelection) {
		Widget w;
		if ((w = findWidget(object)) != null) {
			NodeSelectProvider nsp = new NodeSelectProvider(this);
			if (!getSelectedObjects().contains(w)) {
				if (nsp.isSelectionAllowed(w, null, invertSelection))
					nsp.select(w, null, invertSelection);
					validate();
			}
		}
	}
	
	
	
//	/* ### PRIVATE NESTED CLASSES ############################ */
	
	private static class MoveTracker extends MoveStrategy {
		
		private static Canvas canvas;
		private static MoveTracker instance;
		
		private WidgetAction activeMovement;
		
		// private constructor: singleton
		/**
		 * Creates a new MoveTracker, this class is supposed to look after changes in the policy of movement,
		 * and make the proper modifications on existing nodes on canvas, and also notify the active action repository.
		 * @param canvas
		 */
		private MoveTracker(Canvas canvas, WidgetActionRepository arepo) {
			MoveTracker.canvas = canvas;
			activeMovement = canvas.actions.getAction("Move", canvas);
			addObserver(arepo);
		}
		
		public static MoveTracker getInstance(Canvas canvas, WidgetActionRepository arepo) {
			if (instance == null) {
				instance = new MoveTracker(canvas, arepo);
			}
			return instance;
		}
		
		@Override
		public void notifyObservers(Object obj) {
			setChanged();
			super.notifyObservers(obj);
			WidgetAction ma = canvas.actions.getAction("Move", canvas);
			for (String nd : canvas.getNodes()) {
				Object objw = canvas.findWidget(nd);
				if (objw != null) {
					Widget w = (Widget)objw;
					if (w instanceof LabelWidgetExt) {
						w.getActions(Canvas.SELECT).removeAction(activeMovement);
						w.getActions(Canvas.SELECT).addAction(ma);
					}
				}
			}
			activeMovement = ma;
		}
	}
}
