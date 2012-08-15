package org.grview.syntax.grammar.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Properties;

/**Any syntax element that has a state, and properties, of course**/
/** @author Gustavo H. Braga **/
abstract public class SyntaxElement extends Properties implements Serializable, Cloneable
{

	static final long serialVersionUID = 1;
	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	/*
	 * the types of probable syntax elements, used primarily for identify
	 * property changes *
	 */
	public final static String INPUTS = "Inputs";
	public final static String OUTPUTS = "Outputs";
	public final static String CHILDREN = "Children";

	/* for Serializable support */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		listeners = new PropertyChangeSupport(this);
	}

	protected void firePropertyChange(String prop, Object old, Object newValue)
	{
		listeners.firePropertyChange(prop, old, newValue);
	}

	protected void fireStructureChange(String prop, Object child)
	{
		listeners.firePropertyChange(prop, null, child);
	}

	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		listeners.addPropertyChangeListener(l);
	}

	/* ******************************************************************** */

	public abstract String getID();

	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		listeners.removePropertyChangeListener(l);
	}

	public abstract void setID(String s);

	/**
	 * You may want to override this method for creating update the state of an
	 * element after some change.
	 */
	public void update()
	{
	}

}
