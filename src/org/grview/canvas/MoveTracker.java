package org.grview.canvas;

import org.grview.canvas.action.WidgetActionRepository;
import org.grview.canvas.strategy.MoveStrategy;
import org.grview.canvas.widget.LabelWidgetExt;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

class MoveTracker extends MoveStrategy
{
	private Canvas canvas;

	/**
	 * Creates a new MoveTracker, this class is supposed to look after changes
	 * in the policy of movement, and make the proper modifications on existing
	 * nodes on canvas, and also notify the active action repository.
	 * 
	 * @param canvas
	 */
	public MoveTracker(Canvas canvas, WidgetActionRepository arepo)
	{
		this.canvas = canvas;
		addObserver(arepo);
	}

	@Override
	public void notifyObservers(Object obj)
	{
		setChanged();
		super.notifyObservers(obj);
		WidgetAction activeMovement = canvas.actions.getAction("Move", canvas);
		for (String nd : canvas.getNodes())
		{
			Object obecjtWidget = canvas.findWidget(nd);
			if (obecjtWidget != null)
			{
				Widget widget = (Widget) obecjtWidget;
				if (widget instanceof LabelWidgetExt)
				{
					widget.getActions(CanvasData.SELECT).removeAction(activeMovement);
					widget.getActions(CanvasData.SELECT).addAction(activeMovement);
				}
			}
		}
	}
}