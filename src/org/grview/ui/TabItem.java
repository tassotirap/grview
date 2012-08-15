package org.grview.ui;

import javax.swing.Icon;
import javax.swing.JComponent;

public class TabItem
{
	private String title;
	private Icon viewIcon;
	private JComponent component;
	private int layoutOrder;

	public TabItem(String title, JComponent component, int layoutOrder, Icon viewIcon)
	{
		this.title = title;
		this.component = component;
		this.layoutOrder = layoutOrder;
		this.viewIcon = viewIcon;
	}

	public JComponent getComponent()
	{
		return component;
	}

	public int getLayoutOrder()
	{
		return layoutOrder;
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

	public void setLayoutOrder(int layoutOrder)
	{
		this.layoutOrder = layoutOrder;
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
