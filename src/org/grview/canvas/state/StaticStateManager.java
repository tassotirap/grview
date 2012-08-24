package org.grview.canvas.state;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StaticStateManager
{
	private File file;

	private PropertyChangeSupport monitor;

	private Object object;

	public StaticStateManager()
	{
		monitor = new PropertyChangeSupport(this);
	}

	public String getAbsolutePath()
	{
		return file.getAbsolutePath();
	}

	public File getFile()
	{
		return this.file;
	}

	public Object getObject()
	{
		return this.object;
	}

	public String getParentDirectory()
	{
		return file.getParent();
	}

	public Object read() throws IOException, ClassNotFoundException
	{
		FileInputStream fileInputStream = new FileInputStream(file);
		if (file.length() > 0)
		{
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			object = objectInputStream.readObject();
			objectInputStream.close();
			fileInputStream.close();
			return object;
		}
		fileInputStream.close();
		return null;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public void setObject(Serializable object)
	{
		this.object = object;
	}

	public void write() throws IOException
	{
		monitor.firePropertyChange("writing", null, object);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.close();
		fileOutputStream.close();
	}
}
