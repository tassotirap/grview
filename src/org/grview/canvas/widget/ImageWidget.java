package org.grview.canvas.widget;

import java.awt.Image;

import org.netbeans.api.visual.widget.Scene;

public class ImageWidget extends org.netbeans.api.visual.widget.ImageWidget implements TypedWidget
{

	private String type;

	public ImageWidget(Scene scene)
	{
		super(scene);
	}

	public ImageWidget(Scene scene, Image image)
	{
		super(scene, image);
	}

	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public void setType(String type)
	{
		this.type = type;
	}

}
