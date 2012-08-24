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
import java.util.AbstractMap;
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

	private static final double MIN_ZOOM = 0.5;

	private static final double MAX_ZOOM = 1.5;

	private AsinActionContext<CanvasBeanShellAction, AsinActionSet<CanvasBeanShellAction>> actionContext;
	

	private AbstractMap<String, Cursor> cursors = new HashMap<String, Cursor>();
	private InputHandlerProvider inputHandlerProvider;
	private PropertyChangeSupport monitor;

	private List<PropertyChangeSupport> monitors = new ArrayList<PropertyChangeSupport>();
	private boolean showingGrid;
	private boolean showingGuide;
	private boolean showingLines;
	
	protected WidgetActionRepository actions;
	
	private List<String> customNodes = new ArrayList<String>();
	
	protected CanvasDecorator decorator;

	protected List<String> alternatives = new ArrayList<String>();
	protected List<String> labels = new ArrayList<String>();
	protected List<String> lambdas = new ArrayList<String>();
	protected List<String> leftSides = new ArrayList<String>();
	protected List<String> nterminals = new ArrayList<String>();
	protected List<String> start = new ArrayList<String>();
	protected List<String> successors = new ArrayList<String>();
	protected List<String> terminals = new ArrayList<String>();

	public Canvas(String cursor, String connectionStrategy, String movementStrategy, WidgetActionRepository actions, CanvasDecorator decorator)
	{
		this.actions = actions;
		this.decorator = decorator;
		monitor = new PropertyChangeSupport(this);
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
		return getZoomFactor() < MAX_ZOOM;
	}

	public boolean canZoomOut()
	{
		return getZoomFactor() > MIN_ZOOM;
	}

	public void createCursors()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		Image image = toolkit.getImage(Canvas.class.getResource(CanvasData.CURSOS_LEFT_SIDE_ENABLED));
		cursors.put(CanvasData.LEFT_SIDE, toolkit.createCustomCursor(image, new Point(0, 0), "Left Side"));

		image = toolkit.getImage(Canvas.class.getResource(CanvasData.CURSOS_TERMINAL_ENABLED));
		cursors.put(CanvasData.TERMINAL, toolkit.createCustomCursor(image, new Point(0, 0), "Terminal"));

		image = toolkit.getImage(Canvas.class.getResource(CanvasData.CURSOS_N_TERMINAL_ENABLED));
		cursors.put(CanvasData.N_TERMINAL, toolkit.createCustomCursor(image, new Point(0, 0), "Non-Terminal"));

		image = toolkit.getImage(Canvas.class.getResource(CanvasData.CURSOS_LAMBDA_ENABLED));
		cursors.put(CanvasData.LAMBDA, toolkit.createCustomCursor(image, new Point(0, 0), "Lambda Alternative"));

		image = toolkit.getImage(Canvas.class.getResource(CanvasData.CURSOS_SUCCESSOR_ENABLED));
		cursors.put(CanvasData.SUCCESSOR, toolkit.createCustomCursor(image, new Point(0, 0), "Successor"));

		image = toolkit.getImage(Canvas.class.getResource(CanvasData.CURSOS_ALTERNATIVE_ENABLED));
		cursors.put(CanvasData.ALTERNATIVE, toolkit.createCustomCursor(image, new Point(0, 0), "Alternative"));

		image = toolkit.getImage(Canvas.class.getResource(CanvasData.CURSOS_LABEL_ENABLED));
		cursors.put(CanvasData.LABEL, toolkit.createCustomCursor(image, new Point(0, 0), "Label"));

		image = toolkit.getImage(Canvas.class.getResource(CanvasData.CURSOS_START_ENABLED));
		cursors.put(CanvasData.START, toolkit.createCustomCursor(image, new Point(0, 0), "Start"));
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


	public String getCanvasActiveTool()
	{
		String tool = super.getActiveTool();
		if (tool == null)
		{
			tool = CanvasData.SELECT;
		}
		return tool;
	}

	public abstract CanvasDecorator getCanvasDecorator();

	public abstract CanvasState getCanvasState();

	public abstract LayerWidget getConnectionLayer();

	public abstract String getConnStrategy();

	public List<String> getCustomNodes()
	{
		return customNodes;
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
		return CanvasFactory.getStaticStateManager();
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
		return CanvasFactory.getVolatileStateManager();
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
			event.setPropagationId("canvas");
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
		if (activeTool.equals(CanvasData.SELECT))
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
		Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if (component != null)
		{
			if (component.getKeyListeners().length == 0)
			{
				component.addKeyListener(new KeyListener()
				{

					@Override
					public void keyPressed(KeyEvent e)
					{
						for (KeyListener keyListener : getView().getKeyListeners())
						{
							keyListener.keyPressed(e);
						}
					}

					@Override
					public void keyReleased(KeyEvent e)
					{
						for (KeyListener keyListener : getView().getKeyListeners())
						{
							keyListener.keyReleased(e);
						}
					}

					@Override
					public void keyTyped(KeyEvent e)
					{
						for (KeyListener keyListener : getView().getKeyListeners())
						{
							keyListener.keyTyped(e);
						}
					}
				});
			}
		}
	}

	/**
	 * 
	 * @param labels
	 */
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

	// //INNER CLASSES////////

	/**
	 * @param showingLines
	 *            the showingLines to set
	 */
	public void setShowingLines(boolean showingLines)
	{
		this.showingLines = showingLines;
		getCanvasState().getPreferences().setShowLines(showingLines);
	}

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
