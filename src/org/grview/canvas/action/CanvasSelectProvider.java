package org.grview.canvas.action;

import java.awt.Point;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.widget.Widget;

public abstract class CanvasSelectProvider implements SelectProvider
{
	public CanvasSelectProvider(Canvas canvas)
	{
	}

	@Override
	public void select(Widget arg0, Point arg1, boolean arg2)
	{
		CanvasFactory.getCanvas().setFocused(); // caso ainda n�o tenha
												// sido focado
	}

}
