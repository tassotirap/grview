package org.grview.canvas.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.grview.canvas.Canvas;
import org.netbeans.api.visual.widget.Widget;

public class GridWidget extends Widget
{

	public final static int GRID_SIZE = 16;
	public final static Color LINE_COLOR = new Color(221, 221, 221);
	private Canvas canvas;

	private int height;
	private int width;

	public GridWidget(Canvas canvas)
	{
		super(canvas);
		this.canvas = canvas;
	}

	@Override
	protected void paintWidget()
	{
		width = (canvas.getBounds() == null) ? canvas.getView().getParent().getWidth() : canvas.getBounds().width;
		height = (canvas.getBounds() == null) ? canvas.getView().getParent().getHeight() : canvas.getBounds().height;
		Graphics2D g = getGraphics();
		g.setStroke(new BasicStroke());
		g.setColor(LINE_COLOR);
		int grid_pos = GRID_SIZE;
		while (grid_pos < width)
		{
			g.drawLine(grid_pos, 0, grid_pos, height);
			grid_pos += GRID_SIZE;
		}
		grid_pos = GRID_SIZE;
		while (grid_pos < height)
		{
			g.drawLine(0, grid_pos, width, grid_pos);
			grid_pos += GRID_SIZE;
		}
	}

	@Override
	public Rectangle calculateClientArea()
	{
		return new Rectangle(width, height);
	}

	@Override
	public void paintBorder()
	{
	}
}
