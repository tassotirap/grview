package org.grview.ui.component;

public interface ComponentListener {

	public abstract void ContentChanged(AbstractComponent source, 
										Object oldValue, Object newValue);
}
