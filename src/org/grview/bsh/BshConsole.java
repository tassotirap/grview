package org.grview.bsh;

import org.grview.canvas.Canvas;

import bsh.Interpreter;
import bsh.UtilEvalError;
import bsh.util.JConsole;

public class BshConsole extends JConsole{

	Interpreter interpreter;
	
	public BshConsole() {
		interpreter = new Interpreter(this);
		interpreter.getNameSpace().importClass("org.grview.canvas.Canvas");
		interpreter.getNameSpace().importClass("org.grview.canvas.CanvasFactory");
	}
	
	public void setCanvas(Canvas canvas) {
		try {
			interpreter.getNameSpace().setVariable("canvas", canvas, true);
		} catch (UtilEvalError e) {
			e.printStackTrace();
		}
	}
	
	public Interpreter getInterpreter() {
		return interpreter;
	}
}
