package org.grview.ui.ToolBar;

public class ToolBarManager
{
	private static ToolBarManager instance = null;
	
	public static ToolBarManager getInstance()
	{
		if(instance == null)
			instance = new ToolBarManager();
		return instance;
	}

}
