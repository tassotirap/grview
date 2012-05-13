package org.grview.canvas.action;

import java.awt.event.KeyEvent;

import org.netbeans.api.visual.action.WidgetAction.Adapter;
import org.netbeans.api.visual.widget.Widget;

public class WidgetCopyPasteAction extends Adapter {

	WidgetCopyPasteProvider wcpp;
	
	public WidgetCopyPasteAction(WidgetCopyPasteProvider wcpp) {
		this.wcpp = wcpp;
	}
	
	@Override
	public State keyPressed(Widget widget, WidgetKeyEvent event) {
		if (event.isControlDown()) {
			if (event.getKeyCode() == KeyEvent.VK_C) {
				wcpp.copySelected();
			}
			else if (event.getKeyCode() == KeyEvent.VK_X) {
				wcpp.cutSelected(null);
			}
			else if (event.getKeyCode() == KeyEvent.VK_V) {
				wcpp.paste(null);
			}
		}
		return State.REJECTED;
	}
}
