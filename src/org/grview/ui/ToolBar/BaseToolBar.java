package org.grview.ui.toolbar;

import javax.swing.JToolBar;

public abstract class BaseToolBar<E> extends JToolBar
{
	private static final long serialVersionUID = 1L;

	protected E context;

	protected final String imgPath = "/org/grview/images/";

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
