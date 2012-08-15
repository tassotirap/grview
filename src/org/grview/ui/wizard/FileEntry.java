package org.grview.ui.wizard;

import javax.swing.ImageIcon;

import org.grview.model.FileNames;

public class FileEntry
{
	private final String title;
	private final String imagePath;
	private final FileNames extension;
	private ImageIcon image;

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