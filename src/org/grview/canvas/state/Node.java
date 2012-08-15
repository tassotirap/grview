package org.grview.canvas.state;

import java.awt.Point;

public class Node extends CanvasSerializableElement
{

	private static final long serialVersionUID = -5146510630189874864L;
	private String name;
	private String title;
	private Point location;
	private String type;
	private String mark;

	public Point getLocation()
	{
		return location;
	}

	public String getMark()
	{
		return mark;
	}

	public String getName()
	{
		return name;
	}

	public String getTitle()
	{
		return title;
	}

	public String getType()
	{
		return type;
	}

	public void setLocation(Point location)
	{
		this.location = location;
	}

	public void setMark(String mark)
	{
		this.mark = mark;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setType(String type)
	{
		this.type = type;
	}

}