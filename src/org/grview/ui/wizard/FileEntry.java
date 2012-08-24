package org.grview.ui.wizard;

import javax.swing.ImageIcon;

import org.grview.model.FileNames;

public class FileEntry
{
	private final FileNames extension;
	private ImageIcon image;
	private final String imagePath;
	private final String title;

	public FileEntry(String title, String imagePath, FileNames extension)
	{
		this.title = title;
		this.imagePath = imagePath;
		this.extension = extension;
	}

	public FileNames getExtension()
	{
		return extension;
	}

	public ImageIcon getImage()
	{
		if (image == null)
		{
			image = new ImageIcon(getClass().getResource(imagePath));
		}
		return image;
	}

	public String getTitle()
	{
		return title;
	}
}