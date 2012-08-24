package org.grview.canvas.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.beans.PropertyChangeSupport;

import org.grview.canvas.Canvas;
import org.netbeans.api.visual.widget.Widget;

public class LineWidget extends Widget
{

	public final static int DEFAULT_HEIGHT = 34;
	public final static Color LINE_COLOR = new Color(221, 221, 221);
	public final static int LINE_NUMBER_SPACE = 5;
	public final static String LINE_PAINTED_EVENT = "Line Painted";

	public final static Color NUMBER_COLOR = new Color(100, 100, 100);

	public final static Font NUMBER_FONT = new Font("Arial", Font.PLAIN, 12);
	private Canvas canvas;
	private PropertyChangeSupport monitor;
	private Integer number;
	protected int height;
	protected int width;

	public LineWidget(Canvas canvas)
	{
		super(canvas);
		this.canvas = canvas;
		monitor = new PropertyChangeSupport(this);
	}

	@Override
	protected void paintWidget()
	{
		width = (canvas.getBounds() == null) ? canvas.getView().getParent().getWidth() : canvas.getBounds().width;
		height = DEFAULT_HEIGHT;
		Font f = NUMBER_FONT;
		String s = new String(number.toString());
		Graphics2D g = getGraphics();
		g.setStroke(new BasicStroke());
		g.setColor(LINE_COLOR);
		g.drawLine(0, 0, width, 0);
		FontRenderContext frc = g.getFontRenderContext();
		TextLayout textlayout = new TextLayout(s, f, frc);
		g.setColor(NUMBER_COLOR);
		textlayout.draw(g, LINE_NUMBER_SPACE, (height / 2) + (NUMBER_FONT.getSize() / 2));
		g.setColor(LINE_COLOR);
		g.drawLine(0, height, width, height);
		monitor.firePropertyChange(LINE_PAINTED_EVENT, null, number);
	}

	@Override
	public Rectangle calculateClientArea()
	{
		return new Rectangle(width, height);
	}

	public PropertyChangeSupport getMonitor()
	{
		return monitor;
	}

	/**
	 * @return the number of this line
	 */
	public int getNumber()
	{
		return number;
	}

	@Override
	public void paintBorder()
	{
	}

	/**
	 * Set the number of this line
	 * 
	 * @param number
	 *            the number o this line
	 */
	public void setNumber(int number)
	{
		this.number = number;
	}
}
