package org.grview.ui.component;

import java.util.HashMap;

import org.grview.canvas.Canvas;


public abstract class GrammarRepo {

	private static HashMap<Canvas, AbstractComponent>  compByCanvas = new HashMap<Canvas, AbstractComponent>();

	public static AbstractComponent getCompByCanvas(Canvas canvas) {
		return compByCanvas.get(canvas);
	}
	
	public static void addGramComponent(Canvas canvas, AbstractComponent comp) {
		compByCanvas.put(canvas, comp);
	}
	
	
}
