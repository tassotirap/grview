package org.grview.ui.dynamicview;

import java.awt.Component;

import javax.swing.Icon;

import net.infonode.docking.View;

import org.grview.ui.component.AbstractComponent;

public class DynamicView extends View
{
	private static final long serialVersionUID = 1L;
	private AbstractComponent componentModel;
	private String fileName;
	private int id;

	public DynamicView(String title, Icon icon, Component component, AbstractComponent componentModel, String fileName, int id)
	{
		super(title, icon, component);
		this.id = id;
		this.componentModel = componentModel;
		this.fileName = fileName;
	}

	public AbstractComponent getComponentModel()
	{
		return componentModel;
	}

	public String getFileName()
	{
		return fileName;
	}

	public int getId()
	{
		return id;
	}
}
