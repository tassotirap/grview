package org.grview.ui;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.grview.ui.component.Component;

public class TabItem {
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
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Icon getViewIcon() {
		return viewIcon;
	}
	public void setViewIcon(Icon viewIcon) {
		this.viewIcon = viewIcon;
	}
	public JComponent getComponent() {
		return component;
	}
	public void setComponent(JComponent component) {
		this.component = component;
	}
	public int getLayoutOrder() {
		return layoutOrder;
	}
	public void setLayoutOrder(int layoutOrder) {
		this.layoutOrder = layoutOrder;
	}
	
	
	

}
