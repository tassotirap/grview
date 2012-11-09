package org.grview.project.interfaces;

import java.io.IOException;
import java.util.ArrayList;

import org.grview.model.FileNames;
import org.grview.ui.DynamicView;

public interface IFileManager
{
	public abstract void closeFile(String fileName);

	public abstract void createFile(String name, FileNames extension) throws IOException;

	public abstract boolean isFileOpen(String fileName);

	public abstract void openFile(String path);
	
	public abstract void saveAllFiles(ArrayList<DynamicView> files);
	
	public abstract void saveFileObject(Object object);

}