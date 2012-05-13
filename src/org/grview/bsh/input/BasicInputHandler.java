package org.grview.bsh.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import org.grview.actions.AbstractEditAction;
import org.grview.editor.gui.KeyEventTranslator;
import org.grview.editor.gui.ShortcutPrefixActiveEvent;


public abstract class BasicInputHandler<T extends AbstractEditAction> extends AbstractInputHandler<T> {
    
	
    protected abstract T getAction(String action);
   
	/**
	 * Handles the given keystroke.
	 * @param keyStroke The key stroke
	 * @param dryRun only calculate the return value, do not have any other effect
	 * @since jEdit 4.2pre5
	 */
	@Override
	public boolean handleKey(KeyEventTranslator.Key keyStroke,boolean dryRun)
	{
		Object o = currentBindings.get(keyStroke);
		if(o == null)
		{
			
		}
		else if(o instanceof Hashtable)
		{

			setCurrentBindings((Hashtable)o);
			ShortcutPrefixActiveEvent.firePrefixStateChange(currentBindings, true);
			shortcutOn = true;
			return true;
		}
		else if(o instanceof String)
		{
			setCurrentBindings(bindings);
			sendShortcutPrefixOff();
			invokeAction((String)o);
			return true;
		}
		else if(o instanceof AbstractEditAction<?>)
		{
			setCurrentBindings(bindings);
			sendShortcutPrefixOff();
			invokeAction((T)o);
			return true;
		}
		sendShortcutPrefixOff();
		return false;
	}
	
	@Override
	public void invokeAction(String action)
	{
		invokeAction(getAction(action));
	} 
	
	/**
	 * Forwards key events directly to the input handler.
	 * This is slightly faster than using a KeyListener
	 * because some Swing overhead is avoided.
	 * @param evt the keyboard event
	 * @param from the source of the event. Since this is the input handler of the canvas, it should always be 1
	 * @param global it is only true if the event comes from the DefaultKeyboardFocusManager
	 */
	@Override
	public void processKeyEvent(KeyEvent evt, int from, boolean global)
	{
		if(evt.isConsumed())
			return;

		switch(evt.getID())
		{
		case KeyEvent.KEY_TYPED:
			if(keyEventInterceptor != null)
				keyEventInterceptor.keyTyped(evt);
			break;
		case KeyEvent.KEY_PRESSED:
			if(keyEventInterceptor != null)
				keyEventInterceptor.keyPressed(evt);
			break;
		case KeyEvent.KEY_RELEASED:
			if(keyEventInterceptor != null)
				keyEventInterceptor.keyReleased(evt);
			break;
		}
	}
	
	/**
	 * Forwards key events directly to the input handler.
	 * This is slightly faster than using a KeyListener
	 * because some Swing overhead is avoided.
	 * @param evt the keyboard event
	 * @param from the source of the event. Since this is the input handler of the canvas, it should always be 1
	 * @param global it is only true if the event comes from the DefaultMouseFocusManager
	 */
	@Override
	public void processMouseEvent(MouseEvent evt, int from, boolean global) {
		if (evt.isConsumed()) {
			return;
		}
		
		switch (evt.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			if(mouseEventInterceptor != null)
				mouseEventInterceptor.mousePressed(evt);
			break;
		case MouseEvent.MOUSE_RELEASED:
			if (mouseEventInterceptor != null)
				mouseEventInterceptor.mouseReleased(evt);
			break;
		}
	}
}
