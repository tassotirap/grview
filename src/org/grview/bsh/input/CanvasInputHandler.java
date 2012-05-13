package org.grview.bsh.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.grview.canvas.Canvas;
import org.grview.canvas.action.CanvasBeanShellAction;


//TODO not yet fully integrated and customizable
public abstract class CanvasInputHandler extends BasicInputHandler<CanvasBeanShellAction> {
    

    private Canvas canvas;
    
    public CanvasInputHandler(Canvas canvas) {
    	this.canvas = canvas;
    	
    	setKeyEventInterceptor(new KeyHandler());
    	setMouseEventInterceptor(new MouseHandler());
    }
    
	@Override
	public void invokeAction(CanvasBeanShellAction action) {
		action.invoke(canvas);
	}
	
	protected class KeyHandler implements KeyListener {

		public void keyPressed(KeyEvent evt) {
			//posso chamar quaisquer ações (externas) do canvas por aqui
/*			switch (evt.getKeyCode()) {
			case KeyEvent.VK_B:
				getAction("build").invoke(canvas);
			}*/
			
		}

		public void keyReleased(KeyEvent arg0) {	
		}

		public void keyTyped(KeyEvent arg0) {	
		}
		
	}
	
	protected class MouseHandler implements MouseListener {

		public void mouseClicked(MouseEvent e) {			
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {	
		}

		public void mousePressed(MouseEvent event) {
	    	canvas.setFocused();
		}

		public void mouseReleased(MouseEvent event) {
		}
		
	}
}
