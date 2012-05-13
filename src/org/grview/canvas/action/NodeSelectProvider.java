package org.grview.canvas.action;

import java.awt.Color;
import java.awt.Point;
import java.util.Collections;

import org.grview.canvas.Canvas;
import org.grview.canvas.CanvasFactory;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;



public class NodeSelectProvider extends CanvasSelectProvider {

	private String canvasID;
	
	public NodeSelectProvider(Canvas canvas) {
		super(canvas);
		canvasID = canvas.getID();
	}
	
    public boolean isAimingAllowed (Widget widget, Point localLocation, boolean invertSelection) {
        return false;
    }

    public boolean isSelectionAllowed (Widget widget, Point localLocation, boolean invertSelection) {
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
        return canvas.findObject (widget) != null;
    }

    @Override
	public void select (Widget widget, Point localLocation, boolean invertSelection) {
		Canvas canvas = CanvasFactory.getCanvas(canvasID);
    	super.select(widget, localLocation, invertSelection);
        Object object = canvas.findObject (widget);
        canvas.setFocusedObject (object);
        if (object != null) {
            if (! invertSelection  &&  canvas.getSelectedObjects ().contains (object)) {
                return;
            }
            canvas.userSelectionSuggested (Collections.singleton (object), invertSelection);
            for (Object o: canvas.getLabels()) {
            	LabelWidget lw = ((LabelWidget)canvas.findWidget(o));
            	lw.setBorder(BorderFactory.createEmptyBorder());
            }
            for (Object o : canvas.getNodes()) {
            	Widget lw = canvas.findWidget(o);
            	if (lw instanceof LabelWidget) {
            		lw.setBackground(Color.WHITE);
    				lw.setForeground(Color.BLACK);
            	}
            }
            for (Object o : canvas.getSelectedObjects()) {
            	if (canvas.isNode(o) || canvas.isLabel(o)) {
                	Widget lw = canvas.findWidget(o);
                	if (canvas.isLabel(o)) {
            			lw.setForeground(Color.BLUE);
                		((LabelWidget)canvas.findWidget(o)).setBorder(BorderFactory.createLineBorder(1, Color.BLUE));
                	}
                	else if (lw instanceof LabelWidget) {
                		lw.setBackground(Color.BLUE);
        				lw.setForeground(Color.WHITE);
                	}
            	}
            }
        } else {
            canvas.userSelectionSuggested (Collections.emptySet (), invertSelection);
        }
    }
}