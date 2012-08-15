package org.grview.canvas.strategy;

import java.awt.Point;

import org.grview.canvas.action.LineProvider;
import org.grview.canvas.widget.LineWidget;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.widget.Widget;

public class SnapToLineMoveStrategy implements MoveStrategy
{

	private LineProvider lp;

	public SnapToLineMoveStrategy(LineProvider lp)
	{
		this.lp = lp;
	}

	@Override
	public Point locationSuggested(Widget widget, Point originalLocation, Point suggestedLocation)
	{
		int borderIncTop = (widget.getBorder() == null) ? 0 : widget.getBorder().getInsets().top;
		int borderIncLeft = (widget.getBorder() == null) ? 0 : widget.getBorder().getInsets().left;
		// int sizeInc = (LineWidget.DEFAULT_HEIGHT >
		// widget.getPreferredBounds().height)?(LineWidget.DEFAULT_HEIGHT -
		// widget.getPreferredBounds().height)/2:0;
		if (suggestedLocation.x < 0)
		{
			suggestedLocation.x = 0 + borderIncLeft;
		}
		if (suggestedLocation.y < 0)
		{
			suggestedLocation.y = 0;
		}
		LineWidget closestLine = lp.getLine(suggestedLocation.y);
		Point p = new Point(suggestedLocation.x, closestLine.getPreferredLocation().y + LineProvider.LINE_OFFSET - (borderIncTop / 2));// +
																																		// sizeInc);
		return p;
	}

}
