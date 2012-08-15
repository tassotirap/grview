package org.grview.ui.toolbar;

import javax.swing.JToolBar;

public abstract class BaseToolBar<E> extends JToolBar
{
	protected final String imgPath = "/org/grview/images/";

	private static final long serialVersionUID = 1L;

	protected E context;

	public BaseToolBar(E context)
	{
		this.context = context;
		initComponets();
		initActions();
		initLayout();
	}

	protected abstract void initActions();

	protected abstract void initComponets();

	protected abstract void initLayout();
}
