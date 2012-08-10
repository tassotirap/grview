package org.grview.ui.ToolBar;
import javax.swing.JToolBar;

public abstract class BaseToolBar<E> extends JToolBar
{
	protected final String imgPath = "/org/grview/images/";

	private static final long serialVersionUID = 1L;
	
	protected E context;

	protected abstract void initActions();

	protected abstract void initLayout();
	
	protected abstract void initComponets();
	
	public BaseToolBar(E context)
	{
		this.context = context; 
		initComponets();
		initActions();
		initLayout();
	}
}
