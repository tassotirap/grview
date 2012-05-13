package org.grview.canvas;

import java.awt.Color;
import java.awt.Point;

import org.grview.canvas.UnidirectionalAnchor.UnidirectionalAnchorKind;
import org.netbeans.api.visual.action.ConnectDecorator;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;


public class AlternativeConnectorDecorator implements ConnectDecorator {
	
	private ConnectionWidget thisConnection;
	private GraphScene scene;
	
    public ConnectionWidget createConnectionWidget (Scene scene) {
        ConnectionWidget widget = new ConnectionWidget (scene);
        widget.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
        widget.setLineColor(new Color(255, 100, 100));
        thisConnection = widget;
        this.scene = (scene instanceof GraphScene)? (GraphScene) scene : null;
        return widget;
    }
    public Anchor createSourceAnchor (Widget sourceWidget) {
    	String edge = (String) scene.findObject(thisConnection);
    	return new UnidirectionalAnchor(sourceWidget, edge, true, UnidirectionalAnchorKind.BOTTOM);
    }
    public Anchor createTargetAnchor (Widget targetWidget) {
    	String edge = (String) scene.findObject(thisConnection);
    	return new UnidirectionalAnchor(targetWidget, edge, true, UnidirectionalAnchorKind.TOP);
    }
    public Anchor createFloatAnchor (Point location) {
        return AnchorFactory.createFixedAnchor (location);
    }
}
