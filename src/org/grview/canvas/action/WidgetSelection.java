package org.grview.canvas.action;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import org.grview.canvas.Canvas;
import org.grview.canvas.state.CanvasSerializableElement;
import org.grview.canvas.state.Connection;
import org.grview.canvas.state.Node;
import org.grview.canvas.widget.LabelWidgetExt;
import org.grview.canvas.widget.MarkedWidget;
import org.grview.canvas.widget.TypedWidget;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;


public class WidgetSelection implements ClipboardOwner, Transferable {

	ArrayList<CanvasSerializableElement> elements = new ArrayList<CanvasSerializableElement>();
	private Canvas canvas;
	
	
	public WidgetSelection(Canvas canvas){
		this.canvas = canvas;
	}
	
	public WidgetSelection(Widget[] widgets, Canvas canvas) {
		this.canvas = canvas;
		for (Widget w : widgets) {
			addSelection(w);
		}
	}
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return elements;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {new DataFlavor(java.util.ArrayList.class, "Node")};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.isMimeTypeEqual(DataFlavor.javaSerializedObjectMimeType);
	}

	public void addSelection(Widget w) {
		if (w instanceof LabelWidget) {
			Node node = new Node();
			Object object = canvas.findObject(w);
			if (object != null) {
				node.setName((String)object);
			}
			else {
				node.setName(((LabelWidget)w).getLabel());
			}
			node.setTitle(((LabelWidget) w).getLabel());
			node.setLocation(w.getPreferredLocation());
			if (w instanceof TypedWidget) {
				node.setType(((LabelWidgetExt) w).getType());
			}
			if (w instanceof MarkedWidget) {
				node.setMark(((MarkedWidget)w).getMark());
			}
			elements.add(node);
		}
		else if (w instanceof ConnectionWidget) {
			Connection c = new Connection();
			Object object = canvas.findObject(w);
			if (object != null) {
				c.setName((String)object);
				c.setSource(canvas.getEdgeSource((String)object));
				c.setTarget(canvas.getEdgeTarget((String)object));
				if (canvas.isAlternative((String)object)) {
					c.setType(Canvas.ALTERNATIVE);
				}
				else if (canvas.isSuccessor((String)object)) {
					c.setType(Canvas.SUCCESSOR);
				}
				elements.add(c);
			} 
		}
	}
}
