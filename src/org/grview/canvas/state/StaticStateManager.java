package org.grview.canvas.state;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class StaticStateManager {
	private static final long serialVersionUID = 1L;
	private Object object;
	
	private PropertyChangeSupport monitor;
	
	private File file;
	
	public StaticStateManager() {
		monitor = new PropertyChangeSupport(this);
	}
	
	public Object read() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		if (file.length() > 0) {
			ObjectInputStream ois = new ObjectInputStream(fis);
			object = ois.readObject();
			return object;
		}
		return null;
	}
	
	public void write() throws IOException{
		monitor.firePropertyChange("writing", null, object);
		FileOutputStream fos = new FileOutputStream(file);
		new ObjectOutputStream(fos).writeObject(object);
	}
	
	public String getParentDirectory() {
		return file.getParent();
	}
	
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	
	public void setObject(Serializable object) {
		this.object = object;
	}
	
	public Object getObject() {
		return this.object;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return this.file;
	}
}
