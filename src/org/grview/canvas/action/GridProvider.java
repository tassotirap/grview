package org.grview.canvas.action;

import java.util.HashMap;

import org.grview.canvas.Canvas;
import org.grview.canvas.widget.GridWidget;

public class GridProvider
{

	private GridWidget grid;
	private Canvas canvas;
	private static HashMap<Canvas, GridProvider> gridProviders = new HashMap<Canvas, GridProvider>();

	private GridProvider(Canvas canvas)
	{
		this.canvas = canvas;
	}

	public static GridProvider getInstance(Canvas canvas)
	{
		if (!gridProviders.containsKey(canvas))
		{
			gridProviders.put(canvas, new GridProvider(canvas));
		}
		return gridProviders.get(canvas);
	}

	public boolean isVisible()
	{
		if (grid == null)
		{
			return false;
		}
		return grid.isVisible();
	}

	public void setVisible(boolean visible)
	{
		if (grid != null)
		{
			grid.setVisible(visible);
			canvas.setShowingGrid(visible);
		}
		else if (visible)
		{
			grid = (GridWidget) canvas.addNode(GridWidget.class.getCanonicalName());
		}
		canvas.repaint();
	}
}
