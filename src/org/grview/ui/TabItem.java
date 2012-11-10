package org.grview.ui;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.grview.ui.TabWindowList.TabPlace;

public class TabItem
{
	private JComponent component;
	private TabPlace tabPlace;
	private String title;
	private Icon viewIcon;

	public TabItem(String title, JComponent component, TabPlace tabPlace, Icon viewIcon)
	{
		this.title = title;
		this.component = component;
		this.tabPlace = tabPlace;
		this.viewIcon = viewIcon;
	}

	public JComponent getComponent()
	{
		return component;
	}

	public TabPlace getLayout()
	{
		return tabPlace;
	}
	
	public int getLayoutOrder()
	{
		return tabPlace.ordinal();
	}

	public String getTitle()
	{
		return title;
	}

	public Icon getViewIcon()
	{
		return viewIcon;
	}

	public void setComponent(JComponent component)
	{
		this.component = component;
	}

	public void setLayoutOrder(TabPlace tabPlace)
	{
		this.tabPlace = tabPlace;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setViewIcon(Icon viewIcon)
	{
		this.viewIcon = viewIcon;
	}

}
