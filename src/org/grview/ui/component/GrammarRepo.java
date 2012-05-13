package org.grview.ui.component;

import java.util.HashMap;

import org.grview.canvas.Canvas;


public abstract class GrammarRepo {

	private static HashMap<Canvas, Component>  compByCanvas = new HashMap<Canvas, Component>();

	public static Component getCompByCanvas(Canvas canvas) {
		return compByCanvas.get(canvas);
	}
	
	public static void addGramComponent(Canvas canvas, Component comp) {
		compByCanvas.put(canvas, comp);
	}
	
	
}
