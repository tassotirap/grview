package org.grview.canvas.action;

import java.awt.event.KeyEvent;

import org.netbeans.api.visual.action.WidgetAction.Adapter;
import org.netbeans.api.visual.widget.Widget;

public class WidgetDeleteAction extends Adapter
{

	WidgetDeleteProvider wdp;

	public WidgetDeleteAction(WidgetDeleteProvider wdp)
	{
		this.wdp = wdp;
	}

	@Override
	public State keyReleased(Widget widget, WidgetKeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.VK_DELETE)
		{
			if (wdp.isDeletionAllowed())
			{
				wdp.deleteSelected();
			}
		}
		return State.REJECTED;
	}

}
