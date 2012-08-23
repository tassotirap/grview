package org.grview.canvas;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

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
	private Canvas canvas;
	private String path;
	private VolatileStateManager volatileStateManager;
	private StaticStateManager staticStateManager;

	private CanvasState state;

	private static int defaultBufferCapacity = 20;

	private static CanvasFactory instance;

	private CanvasFactory()
	{
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
		Canvas canvas = null;
		StaticStateManager staticStateManager = new StaticStateManager();
		canvasFactory.staticStateManager = staticStateManager;
		staticStateManager.setFile(file);
		try
		{
			canvas = new CanvasTemplate(defaultCursor, connStrategy, moveStrategy, canvasFactory.actions, canvasFactory.decorator);
			canvasFactory.canvas = canvas;
			Object state = staticStateManager.read();
			if (state == null || !(state instanceof CanvasState))
			{
				canvasFactory.state = new CanvasState();
			}
			else
			{
				canvasFactory.state = (CanvasState) state;
			}
			VolatileStateManager volatileStateManager = new VolatileStateManager(canvasFactory.state, defaultBufferCapacity);
			volatileStateManager.init();
			canvasFactory.volatileStateManager = volatileStateManager;
			volatileStateManager.getMonitor().addPropertyChangeListener("object_state", canvas);
			volatileStateManager.getMonitor().addPropertyChangeListener("writing", canvas);
			volatileStateManager.getMonitor().addPropertyChangeListener("undoable", canvas);
			canvas.getMonitor().addPropertyChangeListener("object_state", canvasFactory);
			canvas.getMonitor().addPropertyChangeListener("writing", canvasFactory.state);
			canvas.init(canvasFactory.state);
			canvas.updateState(canvasFactory.state);
			staticStateManager.setObject(canvasFactory.state);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		canvasFactory.path = file.getAbsolutePath();
		canvasFactory.canvas = canvas;
		return canvas;
	}

	/**
	 * Returns an existing canvas
	 * 
	 * @param id
	 *            identifies the existing canvas
	 * @return the existing canvas with id, or null if there isn't one
	 */
	public static Canvas getCanvas()
	{
		CanvasFactory canvasFactory = getInstance();
		return canvasFactory.canvas;
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
			if (!canvasFactory.path.equals(path))
			{
				CanvasFactory.createCanvas(new File(path));
			}
			return canvasFactory.canvas;
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
	public static String getCanvasPath()
	{
		return getInstance().path;
	}

	/**
	 * Returns a static state manager associated with a canvas holding the
	 * specified id
	 * 
	 * @param id
	 *            the id for the canvas
	 * @return a static state manager, or null when the id is not valid
	 */
	public static StaticStateManager getStaticStateManager()
	{
		return getInstance().staticStateManager;
	}

	/**
	 * Returns a volatile state manager associated with a canvas holding the
	 * specified id
	 * 
	 * @param id
	 *            the id for the canvas
	 * @return a volatile state manager, or null when the id is not valid
	 */
	public static VolatileStateManager getVolatileStateManager()
	{
		return getInstance().volatileStateManager;
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
				CanvasState oldState = state;
				((VolatileStateManager) event.getSource()).getMonitor().removePropertyChangeListener(oldState);
				((VolatileStateManager) event.getSource()).getMonitor().addPropertyChangeListener("writing", state);
				this.state = state;
				staticStateManager.setObject(state);
				getCanvas().updateState(state);
			}
		}

	}
}
