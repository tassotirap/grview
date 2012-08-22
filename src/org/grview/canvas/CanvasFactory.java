package org.grview.canvas;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;

import org.grview.canvas.action.WidgetActionRepository;
import org.grview.canvas.action.WidgetActionRepositoryFactory;
import org.grview.canvas.state.CanvasState;
import org.grview.canvas.state.StaticStateManager;
import org.grview.canvas.state.VolatileStateManager;

public class CanvasFactory implements PropertyChangeListener
{
	private CanvasDecorator decorator;

	private WidgetActionRepository actions;
	private static String defaultCursor = CanvasData.SELECT;
	private static String connStrategy = CanvasData.R_ORTHOGONAL;

	private static String moveStrategy = CanvasData.M_FREE;

	private static String projectPath = "";
	private HashMap<String, Canvas> canvasByPath;
	private HashMap<Canvas, String> pathByCanvas;
	private HashMap<String, Canvas> canvasById;
	private HashMap<String, VolatileStateManager> listVolatileStateManager;
	private HashMap<String, StaticStateManager> listStaticStateManager;

	private HashMap<String, CanvasState> states;

	private static int defaultBufferCapacity = 20;

	private static CanvasFactory instance;

	private CanvasFactory()
	{
		canvasByPath = new HashMap<String, Canvas>();
		pathByCanvas = new HashMap<Canvas, String>();
		canvasById = new HashMap<String, Canvas>();
		listVolatileStateManager = new HashMap<String, VolatileStateManager>();
		listStaticStateManager = new HashMap<String, StaticStateManager>();
		states = new HashMap<String, CanvasState>();
		decorator = new CanvasDecorator();
		actions = WidgetActionRepositoryFactory.getDefaultRepository();
	}

	private static CanvasFactory getInstance()
	{
		if (instance == null)
		{
			instance = new CanvasFactory();
		}
		return instance;
	}

	public static Canvas createCanvas(File file)
	{
		CanvasFactory canvasFactory = getInstance();
		canvasFactory.resetActions();
		String id = String.valueOf(canvasFactory.canvasById.size());
		Canvas canvas = null;
		StaticStateManager staticStateManager = new StaticStateManager();
		canvasFactory.listStaticStateManager.put(id, staticStateManager);
		staticStateManager.setFile(file);
		try
		{
			canvas = new CanvasTemplate(defaultCursor, connStrategy, moveStrategy, canvasFactory.actions, canvasFactory.decorator, id);
			canvasFactory.canvasById.put(id, canvas);
			Object state = staticStateManager.read();
			if (state == null || !(state instanceof CanvasState))
			{
				canvasFactory.states.put(id, new CanvasState(id));
			}
			else
			{
				canvasFactory.states.put(id, (CanvasState) state);
			}
			canvasFactory.states.get(id).setId(id);
			canvas.setId(id);
			VolatileStateManager volatileStateManager = new VolatileStateManager(canvasFactory.states.get(id), defaultBufferCapacity);
			volatileStateManager.init();
			canvasFactory.listVolatileStateManager.put(id, volatileStateManager);
			volatileStateManager.getMonitor().addPropertyChangeListener("object_state", canvas);
			volatileStateManager.getMonitor().addPropertyChangeListener("writing", canvas);
			volatileStateManager.getMonitor().addPropertyChangeListener("undoable", canvas);
			canvas.getMonitor().addPropertyChangeListener("object_state", canvasFactory);
			canvas.getMonitor().addPropertyChangeListener("writing", canvasFactory.states.get(id));
			canvas.init(canvasFactory.states.get(id));
			canvas.updateState(canvasFactory.states.get(id));
			staticStateManager.setObject(canvasFactory.states.get(id));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		canvasFactory.canvasByPath.put(file.getAbsolutePath(), canvas);
		canvasFactory.pathByCanvas.put(canvas, file.getAbsolutePath());
		return canvas;
	}

	/**
	 * Returns an existing canvas
	 * 
	 * @param id
	 *            identifies the existing canvas
	 * @return the existing canvas with id, or null if there isn't one
	 */
	public static Canvas getCanvas(String id)
	{
		CanvasFactory canvasFactory = getInstance();
		if (!canvasFactory.canvasById.containsKey(id))
		{
			return null;
		}
		return canvasFactory.canvasById.get(id);
	}

	public static Canvas getCanvasFromFile(String path)
	{
		if (path != null)
		{
			if (!projectPath.equals("") && !path.startsWith(projectPath))
			{
				path = projectPath + path;
			}
			CanvasFactory canvasFactory = getInstance();
			if (!canvasFactory.canvasByPath.containsKey(path))
			{
				CanvasFactory.createCanvas(new File(path));
			}
			return canvasFactory.canvasByPath.get(path);
		}
		return null;
	}

	/**
	 * Gets the path of the file that holds a canvas
	 * 
	 * @param canvas
	 *            the canvas associated with the path
	 * @return the absolute file path
	 */
	public static String getCanvasPath(Canvas canvas)
	{
		CanvasFactory cf = getInstance();
		if (cf.pathByCanvas.containsKey(canvas))
		{
			return cf.pathByCanvas.get(canvas);
		}
		return null;
	}

	/**
	 * Returns a static state manager associated with a canvas holding the
	 * specified id
	 * 
	 * @param id
	 *            the id for the canvas
	 * @return a static state manager, or null when the id is not valid
	 */
	public static StaticStateManager getStaticStateManager(String id)
	{
		if (!getInstance().listStaticStateManager.containsKey(id))
		{
			return null;
		}
		return getInstance().listStaticStateManager.get(id);
	}

	/**
	 * Returns a volatile state manager associated with a canvas holding the
	 * specified id
	 * 
	 * @param id
	 *            the id for the canvas
	 * @return a volatile state manager, or null when the id is not valid
	 */
	public static VolatileStateManager getVolatileStateManager(String id)
	{
		if (!getInstance().listVolatileStateManager.containsKey(id))
		{
			return null;
		}
		return getInstance().listVolatileStateManager.get(id);
	}

	public static void setProjectPath(String path)
	{
		CanvasFactory.projectPath = path;
	}

	private void resetActions()
	{
		WidgetActionRepositoryFactory.createRepository();
		actions = WidgetActionRepositoryFactory.getDefaultRepository();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource() instanceof VolatileStateManager && event.getPropertyName().equals("object_state"))
		{
			if (event.getNewValue() instanceof CanvasState)
			{
				CanvasState state = (CanvasState) event.getNewValue();
				CanvasState oldState = states.get(state.getID());
				((VolatileStateManager) event.getSource()).getMonitor().removePropertyChangeListener(oldState);
				((VolatileStateManager) event.getSource()).getMonitor().addPropertyChangeListener("writing", state);
				states.put(state.getID(), state);
				listStaticStateManager.get(state.getID()).setObject(state);
				getCanvas(state.getID()).updateState(state);
			}
		}

	}
}
