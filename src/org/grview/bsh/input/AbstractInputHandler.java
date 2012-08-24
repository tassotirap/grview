/*
 * AbstractInputHandler.java - Manages key bindings and executes actions
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2006 Matthieu Casanova
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.grview.bsh.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.grview.actions.AbstractEditAction;
import org.grview.actions.Debug;
import org.grview.editor.gui.KeyEventTranslator;
import org.grview.editor.gui.ShortcutPrefixActiveEvent;
import org.grview.util.Log;

/**
 * The abstract input handler manage the keyboard handling. The entry point is
 * {@link #processKeyEvent(java.awt.event.KeyEvent, int, boolean)}
 * 
 * @author Matthieu Casanova
 * @version $Id: FoldHandler.java 5568 2006-07-10 20:52:23Z kpouer $
 */
public abstract class AbstractInputHandler<E extends AbstractEditAction>
{
	protected static final int REPEAT_COUNT_THRESHOLD = 20;
	// Stores prefix name in bindings hashtable
	public static Object PREFIX_STR = "PREFIX_STR";
	protected Hashtable bindings;
	protected Hashtable currentBindings;
	/** This listener will receive keyboard events if it is not null. */
	protected KeyListener keyEventInterceptor;
	protected E lastAction;

	protected int lastActionCount;

	protected MouseListener mouseEventInterceptor;

	protected String readNextChar;

	protected int repeatCount;

	protected boolean shortcutOn = false;

	// }}}

	// {{{ AbstractInputHandler constructor
	public AbstractInputHandler()
	{
		repeatCount = 1;
	} // }}}

	// {{{ toString() method
	/**
	 * Return a String representation of the keyboard event for debugging
	 * purpose.
	 * 
	 * @param evt
	 *            the keyboard event
	 * @return a String representation for this keyboard event
	 * @since jEdit 4.3pre15
	 */
	public static String toString(KeyEvent evt)
	{
		String id;
		switch (evt.getID())
		{
			case KeyEvent.KEY_PRESSED:
				id = "KEY_PRESSED";
				break;
			case KeyEvent.KEY_RELEASED:
				id = "KEY_RELEASED";
				break;
			case KeyEvent.KEY_TYPED:
				id = "KEY_TYPED";
				break;
			default:
				id = "unknown type";
				break;
		}

		StringBuilder b = new StringBuilder(50);

		b.append(id);
		b.append(",keyCode=0x").append(Integer.toString(evt.getKeyCode(), 16));
		b.append(",keyChar=0x").append(Integer.toString(evt.getKeyChar(), 16));
		b.append(",modifiers=0x").append(Integer.toString(evt.getModifiers(), 16));

		b.append(",consumed=");
		b.append(evt.isConsumed() ? '1' : '0');

		return b.toString();
	} // }}}

	/**
	 * 
	 * @param evt
	 *            the keyboard event
	 * @param from
	 *            the source, it can be {@link org.grview.actions.View#VIEW},
	 *            {@link org.grview.actions.View#ACTION_BAR} or
	 *            {@link org.grview.actions.View#TEXT_AREA}
	 * @param mode
	 *            the mode is "press" or "type" and is used for debug only
	 * @param global
	 *            tell if the event comes from the DefaultKeyboardFocusManager
	 *            or not
	 */
	protected void processKeyEventKeyStrokeHandling(KeyEvent evt, int from, String mode, boolean global)
	{
		KeyEventTranslator.Key keyStroke = KeyEventTranslator.translateKeyEvent2(evt);

		if (keyStroke != null)
		{
			keyStroke.setIsFromGlobalContext(global);
			if (Debug.DUMP_KEY_EVENTS)
			{
				Log.log(Log.DEBUG, this, "Translated (key " + mode + "): " + keyStroke + " from " + from);
			}
			boolean consumed = false;
			if (handleKey(keyStroke, keyStroke.isPhantom()))
			{
				evt.consume();

				consumed = true;
			}
			if (Debug.DUMP_KEY_EVENTS)
			{
				Log.log(Log.DEBUG, this, "Translated (key " + mode + "): " + keyStroke + " from " + from + ": consumed=" + consumed + '.');
			}
		}
	} // }}}

	// {{{ handleKey() methodprotected void sendShortcutPrefixOff()
	/**
	 * If
	 */
	protected void sendShortcutPrefixOff()
	{
		if (shortcutOn)
		{
			ShortcutPrefixActiveEvent.firePrefixStateChange(null, false);
			shortcutOn = false;
		}
	} // }}}

	// {{{ addKeyBinding() method
	/**
	 * Adds a key binding to this input handler. The key binding is a list of
	 * white space separated key strokes of the form <i>[modifiers+]key</i>
	 * where modifier is C for Control, A for Alt, or S for Shift, and key is
	 * either a character (a-z) or a field name in the KeyEvent class prefixed
	 * with VK_ (e.g., BACK_SPACE)
	 * 
	 * @param keyBinding
	 *            The key binding
	 * @param action
	 *            The action
	 */
	public void addKeyBinding(String keyBinding, E action)
	{
		addKeyBinding(keyBinding, (Object) action);
	} // }}}

	// {{{ addKeyBinding() method
	/**
	 * Adds a key binding to this input handler. The key binding is a list of
	 * white space separated key strokes of the form <i>[modifiers+]key</i>
	 * where modifier is C for Control, A for Alt, or S for Shift, and key is
	 * either a character (a-z) or a field name in the KeyEvent class prefixed
	 * with VK_ (e.g., BACK_SPACE)
	 * 
	 * @param keyBinding
	 *            The key binding
	 * @param action
	 *            The action
	 * @since jEdit 4.3pre1
	 */
	public void addKeyBinding(String keyBinding, Object action)
	{
		Hashtable current = bindings;

		String prefixStr = null;

		StringTokenizer st = new StringTokenizer(keyBinding);
		while (st.hasMoreTokens())
		{
			String keyCodeStr = st.nextToken();
			if (prefixStr == null)
				prefixStr = keyCodeStr;
			else
				prefixStr = prefixStr + " " + keyCodeStr;

			KeyEventTranslator.Key keyStroke = KeyEventTranslator.parseKey(keyCodeStr);
			if (keyStroke == null)
				return;

			if (st.hasMoreTokens())
			{
				Object o = current.get(keyStroke);
				if (o instanceof Hashtable)
					current = (Hashtable) o;
				else
				{
					Hashtable hash = new Hashtable();
					hash.put(PREFIX_STR, prefixStr);
					o = hash;
					current.put(keyStroke, o);
					current = (Hashtable) o;
				}
			}
			else
				current.put(keyStroke, action);
		}
	} // }}}

	// {{{ addKeyBinding() method
	/**
	 * Adds a key binding to this input handler. The key binding is a list of
	 * white space separated key strokes of the form <i>[modifiers+]key</i>
	 * where modifier is C for Control, A for Alt, or S for Shift, and key is
	 * either a character (a-z) or a field name in the KeyEvent class prefixed
	 * with VK_ (e.g., BACK_SPACE)
	 * 
	 * @param keyBinding
	 *            The key binding
	 * @param action
	 *            The action
	 * @since jEdit 4.2pre1
	 */
	public void addKeyBinding(String keyBinding, String action)
	{
		addKeyBinding(keyBinding, (Object) action);
	} // }}}

	// {{{ getKeyBinding() method
	/**
	 * Returns either an edit action, or a hashtable if the specified key is a
	 * prefix.
	 * 
	 * @param keyBinding
	 *            The key binding
	 * @since jEdit 3.2pre5
	 */
	public Object getKeyBinding(String keyBinding)
	{
		Hashtable current = bindings;
		StringTokenizer st = new StringTokenizer(keyBinding);

		while (st.hasMoreTokens())
		{
			KeyEventTranslator.Key keyStroke = KeyEventTranslator.parseKey(st.nextToken());
			if (keyStroke == null)
				return null;

			if (st.hasMoreTokens())
			{
				Object o = current.get(keyStroke);
				if (o instanceof Hashtable)
				{
					if (!st.hasMoreTokens())
						return o;
					else
						current = (Hashtable) o;
				}
				else
					return o;
			}
			else
			{
				return current.get(keyStroke);
			}
		}

		return null;
	} // }}}

	// {{{ getKeyEventInterceptor() method
	public KeyListener getKeyEventInterceptor()
	{
		return keyEventInterceptor;
	} // }}}
		// {{{ getLastActionCount() method

	/**
	 * Returns the number of times the last action was executed. It can be used
	 * with smartHome and smartEnd
	 * 
	 * @return the number of times the last action was executed
	 * @since jEdit 2.5pre5
	 */
	public int getLastActionCount()
	{
		return lastActionCount;
	} // }}}

	public MouseListener getMouseEventInterceptor()
	{
		return mouseEventInterceptor;
	}

	// {{{ handleKey() method
	/**
	 * Handles a keystroke.
	 * 
	 * @param keyStroke
	 *            The key stroke.
	 * @param dryRun
	 *            only calculate the return value, do not have any other effect
	 * @return true if the input could be handled.
	 * @since jEdit 4.3pre7
	 */
	public abstract boolean handleKey(KeyEventTranslator.Key keyStroke, boolean dryRun);

	// }}}

	public abstract void invokeAction(E action);

	// {{{ processKeyEvent() method

	public abstract void invokeAction(String action);

	// {{{ isPrefixActive() method
	/**
	 * Returns if a prefix key has been pressed.
	 */
	public boolean isPrefixActive()
	{
		return readNextChar != null;
	} // }}}

	// {{{ processKeyEventKeyStrokeHandling() method

	/**
	 * Process a keyboard event. This is the entry point of the keyboard
	 * handling
	 * 
	 * @param evt
	 *            the keyboard event
	 * @param from
	 *            the source, it can be {@link org.grview.actions.View#VIEW},
	 *            {@link org.grview.actions.View#ACTION_BAR} or
	 *            {@link org.grview.actions.View#TEXT_AREA}
	 * @param global
	 *            tell if the event comes from the DefaultKeyboardFocusManager
	 *            or not
	 */
	public abstract void processKeyEvent(KeyEvent evt, int from, boolean global);

	// }}}

	/**
	 * Process a mouse event This is the entry point of the mouse handling
	 * 
	 * @param evt
	 *            the mouse event
	 * @param from
	 *            the source, it can be (@link org.grview.actions.View#View),
	 *            (@ling asin.util.action.View#ACTION_BAR) or (@link
	 *            org.grview.actions.View#TEXT_AREA)
	 * @param global
	 *            tell if the event comes from the DefaultKeyboardFocusManager
	 *            or not
	 */
	public abstract void processMouseEvent(MouseEvent evt, int from, boolean global);

	// {{{ removeAllKeyBindings() method
	/**
	 * Removes all key bindings from this input handler.
	 */
	public void removeAllKeyBindings()
	{
		bindings.clear();
	} // }}}

	// {{{ removeKeyBinding() method
	/**
	 * Removes a key binding from this input handler. This is not yet
	 * implemented.
	 * 
	 * @param keyBinding
	 *            The key binding
	 */
	public void removeKeyBinding(String keyBinding)
	{
		Hashtable current = bindings;

		StringTokenizer st = new StringTokenizer(keyBinding);
		while (st.hasMoreTokens())
		{
			String keyCodeStr = st.nextToken();
			KeyEventTranslator.Key keyStroke = KeyEventTranslator.parseKey(keyCodeStr);
			if (keyStroke == null)
				return;

			if (st.hasMoreTokens())
			{
				Object o = current.get(keyStroke);
				if (o instanceof Hashtable)
					current = ((Hashtable) o);
				else if (o != null)
				{
					// we have binding foo
					// but user asks to remove foo bar?
					current.remove(keyStroke);
					return;
				}
				else
				{
					// user asks to remove non-existent
					return;
				}
			}
			else
				current.remove(keyStroke);
		}
	} // }}}

	// {{{ resetLastActionCount() method
	/**
	 * Resets the last action count. This should be called when an editing
	 * operation that is not an action is invoked, for example a mouse click.
	 * 
	 * @since jEdit 4.0pre1
	 */
	public void resetLastActionCount()
	{
		lastActionCount = 0;
	} // }}}

	// {{{ Private members

	// {{{ setBindings() method
	/**
	 * Replace the set of key bindings.
	 * 
	 * @since jEdit 4.3pre1
	 */
	public void setBindings(Hashtable bindings)
	{
		this.bindings = this.currentBindings = bindings;
	} // }}}
		// {{{ setCurrentBindings() method

	public void setCurrentBindings(Hashtable bindings)
	{
		currentBindings = bindings;
	} // }}}

	// {{{ setKeyEventInterceptor() method
	/**
	 * Sets the listener that will handle all key events in this view. For
	 * example, the complete word command uses this so that all key events are
	 * passed to the word list popup while it is visible.
	 * 
	 * @param keyEventInterceptor
	 *            the KeyListener that will receive the events
	 */
	public void setKeyEventInterceptor(KeyListener keyEventInterceptor)
	{
		this.keyEventInterceptor = keyEventInterceptor;
	} // }}}

	public void setMouseEventInterceptor(MouseListener mouseEventInterceptor)
	{
		this.mouseEventInterceptor = mouseEventInterceptor;
	}
}
