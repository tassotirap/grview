package org.grview.canvas.action;

import java.awt.Color;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;

public class LabelHoverProvider implements TwoStateHoverProvider
{

	private String canvasID;

	public LabelHoverProvider(Canvas canvas)
	{
		this.canvasID = canvas.getID();
	}

	@Override
	public void setHovering(Widget widget)
	{
		if (widget != null)
		{
			((LabelWidget) widget).setBorder(BorderFactory.createLineBorder(1, Color.BLUE));
		}

	}

	@Override
	public void unsetHovering(Widget widget)
	{
		if (widget != null)
		{
			if (!CanvasFactory.getCanvas(canvasID).getSelectedObjects().contains(CanvasFactory.getCanvas(canvasID).findObject(widget)))
				((LabelWidget) widget).setBorder(BorderFactory.createEmptyBorder());
		}

	}

}
