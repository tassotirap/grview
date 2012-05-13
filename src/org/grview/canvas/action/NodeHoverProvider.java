package org.grview.canvas.action;

import java.awt.Color;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.grview.canvas.widget.MarkedWidget;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.widget.Widget;



public class NodeHoverProvider implements TwoStateHoverProvider {

	public String canvasID;
	
	public NodeHoverProvider(Canvas canvas) {
		canvasID = canvas.getID();
	}
    public void unsetHovering(Widget widget) {
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
    	Object object = canvas.findObject (widget);
    	
    	if (object != null & widget != null && !canvas.getSelectedObjects().contains(object)) {
    		if (widget instanceof MarkedWidget) {
    			((MarkedWidget) widget).setMarkBackground(MarkedWidget.DEFAULT_MARK_BACKGROUND);
    			((MarkedWidget) widget).setMarkForeground(MarkedWidget.DEFAULT_MARK_FOREGROUND);
    		}
            widget.setBackground (Color.WHITE);
            widget.setForeground (Color.BLACK);
        }
    }

    public void setHovering(Widget widget) {
        if (widget != null) {
            widget.setBackground (Color.BLUE);
            widget.setForeground (Color.WHITE);
        }
    }

}