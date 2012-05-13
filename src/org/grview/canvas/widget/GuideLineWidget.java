package org.grview.canvas.widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.grview.canvas.Canvas;


public class GuideLineWidget extends LineWidget {

	private Canvas canvas;
	public static final int DEFAULT_WIDTH = 1;
	public static final Color GUIDE_LINE_COLOR = new Color(240, 100, 100);
	public static final int DEFAULT_X_POS = 120;
	
	public GuideLineWidget(Canvas canvas) {
		super(canvas);
		this.canvas = canvas;
	}

	@Override
	public Rectangle calculateClientArea() {
		if (width == 0 || height == 0) {
			height = (canvas.getBounds() == null)?
					canvas.getView().getParent().getHeight():
					canvas.getBounds().height;
					width = DEFAULT_WIDTH;
		}
		return new Rectangle(width, height);
	}
	
	@Override
    protected void paintWidget () {
		height = (canvas.getBounds() == null)?
					canvas.getView().getParent().getHeight():
					canvas.getBounds().height;
		width = DEFAULT_WIDTH;
		Graphics2D g = getGraphics ();
		g.setStroke(new BasicStroke(width));
		g.setColor(GUIDE_LINE_COLOR);
		g.drawLine(0, 0, 0, height);
	}
}
