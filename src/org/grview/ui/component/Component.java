package org.grview.ui.component;

import java.util.ArrayList;

import javax.swing.JComponent;

public abstract class Component {

	protected JComponent jComponent;
	protected ArrayList<ComponentListener> listeners = new ArrayList<ComponentListener>();
	
	public abstract JComponent create(Object param) throws BadParameterException;
	
	public void addComponentListener(ComponentListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeComponentListener(ComponentListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
	
	public abstract void fireContentChanged();
	
	/**
	 * Gets the current component, It can be NULL!!, even if this component was created already
	 * @return the current component
	 */
	public JComponent getJComponent() {
		return jComponent;
	}
}
