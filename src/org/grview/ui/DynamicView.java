package org.grview.ui;

import java.awt.Component;

import javax.swing.Icon;

import net.infonode.docking.View;

import org.grview.ui.component.AbstractComponent;

/**
 * A dynamically created view containing an id.
 */
public class DynamicView extends View
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private AbstractComponent componentModel;
	private String fileName;

	/**
	 * Constructor.
	 * 
	 * @param title
	 *            the view title
	 * @param icon
	 *            the view icon
	 * @param component
	 *            the view component
	 * @param id
	 *            the view id
	 */
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

	/**
	 * Returns the view id.
	 * 
	 * @return the view id
	 */
	public int getId()
	{
		return id;
	}
}
