package org.grview.ui.component;

public interface ComponentListener {

	public abstract void ContentChanged(Component source, 
										Object oldValue, Object newValue);
}
