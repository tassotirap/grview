package org.grview.canvas;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;

import org.grview.actions.ActionContextHolder;
import org.grview.actions.AsinActionContext;
import org.grview.actions.AsinActionSet;
import org.grview.bsh.input.AbstractInputHandler;
import org.grview.bsh.input.CanvasInputHandler;
import org.grview.bsh.input.DefaultInputHandlerProvider;
import org.grview.bsh.input.InputHandlerProvider;
import org.grview.canvas.action.CanvasBeanShellAction;
import org.grview.canvas.action.WidgetActionRepository;
import org.grview.canvas.state.CanvasState;
import org.grview.canvas.state.StaticStateManager;
import org.grview.canvas.state.VolatileStateManager;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.model.ObjectSceneEventType;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.widget.LayerWidget;

public abstract class Canvas extends GraphScene.StringGraph implements PropertyChangeListener, ActionContextHolder<CanvasBeanShellAction, AsinActionSet<CanvasBeanShellAction>>
{

	/**
	 * This inner classes acts as a facade used to forward key events to
	 * keylisteners "hanged" to this canvas This class was created to deal with
	 * some issues that arose when trying to integrate this canvas and
	 * infonode's docking framework. So it may be not be necessary on some
	 * cases.
	 */
	protected class KeyListenerFacade implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent e)
		{
			for (KeyListener kl : getView().getKeyListeners())
			{
				kl.keyPressed(e);
			}
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			for (KeyListener kl : getView().getKeyListeners())
			{
				kl.keyReleased(e);
			}
		}

		@Override
		public void keyTyped(KeyEvent e)
		{
			for (KeyListener kl : getView().getKeyListeners())
			{
				kl.keyTyped(e);
			}
		}
	}

	// The different possible types of cursor, only the static ones
	public final static String SELECT = "SELECT";
	public final static String CTRL_SELECT = "CTRL_SELECT";
	public final static String LEFT_SIDE = "LEFT_SIDE";
	public final static String N_TERMINAL = "N_TERMINAL";
	public final static String TERMINAL = "TERMINAL";
	public final static String LABEL = "LABEL";
	public final static String SUCCESSOR = "SUCCESSOR";
	public final static String ALTERNATIVE = "ALTERNATIVE";
	public final static String LAMBDA = "LAMBDA";

	public final static String START = "START";
	// The routing policies for connections
	public final static String R_DIRECT = "R_DIRECT";
	public final static String R_ORTHOGONAL = "R_ORTHOGONAL";

	public final static String R_FREE = "R_FREE";
	// Movement Policy
	public final static String M_FREE = "M_FREE";
	public final static String M_SNAP = "M_SNAP";
	public final static String M_ALIGN = "M_ALIGN";

	public final static String M_LINES = "M_LINES";
	// Labels for edges
	public final static String SUC_LBL = "successor";

	public final static String ALT_LBL = "alternative";
	private ArrayList<String> candidateSuc = new ArrayList<String>();

	private ArrayList<String> candidateAlt = new ArrayList<String>();
	private ArrayList<String> successors = new ArrayList<String>();

	private ArrayList<String> alternatives = new ArrayList<String>();

	private HashMap<String, Cursor> cursors = new HashMap<String, Cursor>();
	public List<String> labels = new ArrayList<String>();
	public List<String> leftSides = new ArrayList<String>();
	public List<String> terminals = new ArrayList<String>();
	public List<String> nterminals = new ArrayList<String>();
	public List<String> lambdas = new ArrayList<String>();
	public List<String> start = new ArrayList<String>();

	public List<String> customNodes = new ArrayList<String>();
	private boolean showingLines;
	private boolean showingGrid;

	private boolean showingGuide;

	// Actions repository
	public WidgetActionRepository actions;

	// Used to draw the canvas properly
	public CanvasDecorator decorator;

	// identification for this canvas
	private String id;

	// provides an action context, specially for user input
	private AsinActionContext<CanvasBeanShellAction, AsinActionSet<CanvasBeanShellAction>> actionContext;

	private InputHandlerProvider inputHandlerProvider;

	// holds references to all monitors interested in events that may occur
	// directly in this canvas
	private ArrayList<PropertyChangeSupport> monitors = new ArrayList<PropertyChangeSupport>();

	private PropertyChangeSupport monitor;

	public Canvas(String cursor, String connectionStrategy, String movementStrategy, WidgetActionRepository actions, CanvasDecorator decorator, String id)
	{
		this.actions = actions;
		this.decorator = decorator;
		this.id = id;
		monitor = new PropertyChangeSupport(this);
	}

	public String _getActiveTool()
	{
		String tool = super.getActiveTool();
		if (tool == null)
		{
			tool = SELECT; // by default select tool is activated
		}
		return tool;
	}

	/**
	 * Adds a new action set to the canvas's list of ActionSets.
	 * 
	 * @param actionSet
	 *            the actionSet to add
	 */
	public void addActionSet(AsinActionSet<CanvasBeanShellAction> actionSet)
	{
		actionContext.addActionSet(actionSet);
	}

	public boolean canZoomIn()
	{
		return getZoomFactor() < 2.0;
	}

	public boolean canZoomOut()
	{
		return getZoomFactor() > 0.1;
	}

	public void createCursors()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		Image image = toolkit.getImage(Canvas.class.getResource("/org/grview/images/cursor_h_enabled.gif"));
		cursors.put(LEFT_SIDE, toolkit.createCustomCursor(image, new Point(0, 0), "Left Side"));

		image = toolkit.getImage(Canvas.class.getResource("/org/grview/images/cursor_t_enabled.gif"));
		cursors.put(TERMINAL, toolkit.createCustomCursor(image, new Point(0, 0), "Terminal"));

		image = toolkit.getImage(Canvas.class.getResource("/org/grview/images/cursor_nt_enabled.gif"));
		cursors.put(N_TERMINAL, toolkit.createCustomCursor(image, new Point(0, 0), "Non-Terminal"));

		image = toolkit.getImage(Canvas.class.getResource("/org/grview/images/cursor_l_enabled.gif"));
		cursors.put(LAMBDA, toolkit.createCustomCursor(image, new Point(0, 0), "Lambda Alternative"));

		image = toolkit.getImage(Canvas.class.getResource("/org/grview/images/cursor_successor_enabled.gif"));
		cursors.put(SUCCESSOR, toolkit.createCustomCursor(image, new Point(0, 0), "Successor"));

		image = toolkit.getImage(Canvas.class.getResource("/org/grview/images/cursor_alternative_enabled.gif"));
		cursors.put(ALTERNATIVE, toolkit.createCustomCursor(image, new Point(0, 0), "Alternative"));

		image = toolkit.getImage(Canvas.class.getResource("/org/grview/images/cursor_label_enabled.gif"));
		cursors.put(LABEL, toolkit.createCustomCursor(image, new Point(0, 0), "Label"));

		image = toolkit.getImage(Canvas.class.getResource("/org/grview/images/cursor_s_enabled.gif"));
		cursors.put(START, toolkit.createCustomCursor(image, new Point(0, 0), "Start"));
	}

	@Override
	public JComponent createView()
	{
		JComponent component = super.createView();
		component.addKeyListener(inputHandlerProvider.getInputHandler().getKeyEventInterceptor());
		component.addMouseListener(inputHandlerProvider.getInputHandler().getMouseEventInterceptor());
		return component;
	}

	/**
	 * Fires a property change to all registered monitors
	 * 
	 * @param property
	 * @param oldObj
	 * @param newObj
	 */
	public void firePropertyChange(String property, Object oldObj, Object newObj)
	{
		for (PropertyChangeSupport monitor : monitors)
		{
			monitor.firePropertyChange(property, oldObj, newObj);
		}
		this.getMonitor().firePropertyChange(property, oldObj, newObj);
	}

	// returns the current action set
	@Override
	public AsinActionContext<CanvasBeanShellAction, AsinActionSet<CanvasBeanShellAction>> getActionContext()
	{
		return actionContext;
	}

	public abstract Router getActiveRouter();

	public List<String> getAlternatives()
	{
		return alternatives;
	}

	public abstract LayerWidget getBackgroundLayer();

	public List<String> getCandidateAlt()
	{
		return candidateAlt;
	}

	public List<String> getCandidateSuc()
	{
		return candidateSuc;
	}

	public abstract CanvasDecorator getCanvasDecorator();

	public abstract CanvasState getCanvasState();

	public abstract LayerWidget getConnectionLayer();

	public abstract String getConnStrategy();

	// management of screen objects

	public String getID()
	{
		return this.id;
	}

	public AbstractInputHandler<?> getInputHandler()
	{
		return inputHandlerProvider.getInputHandler();
	}

	public abstract LayerWidget getInterractionLayer();

	public abstract Collection<?> getLabels();

	public List<String> getLambdas()
	{
		return lambdas;
	}

	public List<String> getLeftSides()
	{
		return leftSides;
	}

	public abstract LayerWidget getMainLayer();

	/**
	 * @return the main monitor, all events of interest to canvas should be
	 *         registered through this monitor
	 */
	public PropertyChangeSupport getMonitor()
	{
		return this.monitor;
	}

	public abstract String getMoveStrategy();

	/* ##########################GETTERS AND SETTERS############################ */

	public abstract String getNodeType(Object node);

	public List<String> getNterminals()
	{
		return nterminals;
	}

	public BufferedImage getScreenshot()
	{
		BufferedImage bi = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = bi.createGraphics();
		getScene().paint(graphics);
		graphics.dispose();
		return bi;
	}

	public List<String> getStart()
	{
		return start;
	}

	/**
	 * This method is here for a mere convenience, is really essential for the
	 * canvas itself
	 * 
	 * @return the static state manager of this canvas
	 */
	public StaticStateManager getStaticStateManager()
	{
		return CanvasFactory.getStaticStateManager(id);
	}

	public List<String> getSuccessors()
	{
		return successors;
	}

	public List<String> getTerminals()
	{
		return terminals;
	}

	/**
	 * This method is here for a mere convenience, is really essential for the
	 * canvas itself
	 * 
	 * @return the volatile state manager of this canvas
	 */
	public VolatileStateManager getVolatileStateManager()
	{
		return CanvasFactory.getVolatileStateManager(id);
	}

	/**
	 * Must initialize canvas with the given state
	 * 
	 * @param state
	 */
	public void init(CanvasState state)
	{
		addObjectSceneListener(state, ObjectSceneEventType.OBJECT_ADDED);
		addObjectSceneListener(state, ObjectSceneEventType.OBJECT_REMOVED);
		initInputHandler();
		createCursors();
	}

	/**
	 * Creates an actionContext and initializes the input handler for this
	 * canvas. When compared to TextArea, Canvas has an mixed approach to input
	 * event handling, for it can deal with input through invocation of action
	 * beans or through direct , usual, invocation, mainly when the input comes
	 * from some widget.
	 */
	public void initInputHandler()
	{
		actionContext = new AsinActionContext<CanvasBeanShellAction, AsinActionSet<CanvasBeanShellAction>>()
		{
			@Override
			public void invokeAction(EventObject evt, CanvasBeanShellAction action)
			{
				action.invoke(Canvas.this);
			}
		};

		// setMouseHandler(new CanvasMouseHandler(this));
		inputHandlerProvider = new DefaultInputHandlerProvider(new CanvasInputHandler(this)
		{
			@Override
			protected CanvasBeanShellAction getAction(String action)
			{
				return actionContext.getAction(action);
			}
		});
	}

	public boolean isAlternative(String edge)
	{
		return alternatives.contains(edge);
	}

	public boolean isCandidateAlternative(String edge)
	{
		return candidateAlt.contains(edge);
	}

	public boolean isCandidateSuccessor(String edge)
	{
		return candidateSuc.contains(edge);
	}

	public abstract boolean isLabel(Object o);

	/**
	 * @return the showingGrid
	 */
	public boolean isShowingGrid()
	{
		return showingGrid;
	}

	/**
	 * @return the showingGuide
	 */
	public boolean isShowingGuide()
	{
		return showingGuide;
	}

	/**
	 * @return the showingLines
	 */
	public boolean isShowingLines()
	{
		return showingLines;
	}

	public boolean isSuccessor(String edge)
	{
		return successors.contains(edge);
	}

	/**
	 * painting with antialias
	 */
	@Override
	public void paintChildren()
	{
		Object anti = getGraphics().getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		Object textAnti = getGraphics().getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);

		getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		getGraphics().setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		super.paintChildren();

		getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, anti);
		getGraphics().setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, textAnti);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		for (PropertyChangeSupport monitor : monitors)
		{
			event.setPropagationId("canvas:" + id);
			monitor.firePropertyChange(event);
		}
		this.getMonitor().firePropertyChange(event);
	}

	/**
	 * Register a new monitor to events in canvas
	 * 
	 * @param monitor
	 *            a property change support instance that may contain the
	 *            interested listeners
	 */
	public void registerMonitor(PropertyChangeSupport monitor)
	{
		if (!monitors.contains(monitor))
		{
			monitors.add(monitor);
		}
	}

	public abstract void removeEdgeSafely(String edge);

	public abstract void removeNodeSafely(String node);

	public void saveToFile(File file) throws IOException
	{
		StaticStateManager ssm = new StaticStateManager();
		ssm.setFile(file);
		ssm.setObject(this.getCanvasState());
		ssm.write();
	}

	public abstract void select(Object object);

	public abstract void select(Object object, boolean invertSelection);

	@Override
	public void setActiveTool(String activeTool)
	{
		super.setActiveTool(activeTool);
		if (activeTool.equals(SELECT))
		{
			this.setCursor(Cursor.getDefaultCursor());
		}
		else
		{
			this.setCursor(cursors.get(activeTool));
		}
	}

	public abstract void setConnStrategy(String policy);

	/**
	 * Called to give properly give focus to this canvas
	 */
	public void setFocused()
	{
		getView().grabFocus();
		Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if (c != null)
		{
			if (c.getKeyListeners().length == 0)
			{
				c.addKeyListener(new KeyListenerFacade());
			}
		}
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setLabels(List<String> labels)
	{
		this.labels = labels;
	}

	public abstract void setMoveStrategy(String strategy);

	/**
	 * @param showingGrid
	 *            the showingGrid to set
	 */
	public void setShowingGrid(boolean showingGrid)
	{
		this.showingGrid = showingGrid;
		getCanvasState().getPreferences().setShowGrid(showingGrid);
	}

	/**
	 * @param showingGuide
	 *            the showingGuide to set
	 */
	public void setShowingGuide(boolean showingGuide)
	{
		this.showingGuide = showingGuide;
		getCanvasState().getPreferences().setShowGuide(showingGuide);
	}

	/**
	 * @param showingLines
	 *            the showingLines to set
	 */
	public void setShowingLines(boolean showingLines)
	{
		this.showingLines = showingLines;
		getCanvasState().getPreferences().setShowLines(showingLines);
	}

	// //INNER CLASSES////////

	/**
	 * Unregister a monitor of events
	 * 
	 * @param monitor
	 *            a Property change support previously registered in canvas
	 */
	public void unregisterMonitor(PropertyChangeSupport monitor)
	{
		if (monitors.contains(monitor))
		{
			monitors.remove(monitor);
		}
	}

	public abstract void updateState(CanvasState state);
}
