package org.grview.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.grview.canvas.Canvas;
import org.grview.canvas.state.StaticStateManager;
import org.grview.editor.StandaloneTextArea;
import org.grview.model.FileNames;
import org.grview.project.interfaces.IFileManager;
import org.grview.project.interfaces.IProject;
import org.grview.project.interfaces.IViewManager;
import org.grview.ui.MainWindow;
import org.grview.ui.component.GrammarComponent;
import org.grview.ui.dynamicview.DynamicView;
import org.grview.ui.interfaces.IMainWindow;
import org.grview.util.ComponentPrinter;
import org.grview.util.Log;
import org.grview.util.TextPrinter;

public final class GrviewManager
{
	private MainWindow mainWindow;
	private IProject project;
	private IFileManager fileManager;
	private IViewManager unViewManager;
	private Canvas activeScene;

	private static GrviewManager instance = null;
	public static GrviewManager getInstance()
	{
		if (instance == null)
		{
			System.exit(0);
		}
		return instance;
	}

	public static void initGrView(MainWindow window, String projectPath)
	{
		new GrviewManager(window, projectPath);
	}

	public List<File> getOpenedFiles()
	{
		List<File> filesOpened = project.getOpenedFiles();
		if (filesOpened.size() == 0)
		{
			project.getOpenedFiles().add(project.getGrammarFile());
		}
		return filesOpened;
	}

	public void exit()
	{
		ArrayList<DynamicView> unsavedViews = getUnsavedViews();

		for (DynamicView dynamicView : unsavedViews)
		{
			int option = JOptionPane.showConfirmDialog(getMainWindow().getFrame(), "Would you like to save '" + dynamicView.getTitle().replace(IMainWindow.UNSAVED_PREFIX, "") + "' before exiting?");
			if (option == JOptionPane.CANCEL_OPTION)
				return;
			if (option == JOptionPane.YES_OPTION && dynamicView.getComponentModel() instanceof GrammarComponent)
			{
				StaticStateManager StaticStateManager = activeScene.getStaticStateManager();
				try
				{
					StaticStateManager.write();
					String path = StaticStateManager.getParentDirectory();
					saveFile(path);
				}
				catch (IOException e)
				{
					Log.log(Log.ERROR, getMainWindow(), "Could not save file", e);
				}
			}
			else if (option == JOptionPane.YES_OPTION)
			{
				saveFile(dynamicView.getComponentModel());
			}
		}
		saveProject();
		System.exit(0);
	}

	public ArrayList<DynamicView> getUnsavedViews()
	{
		return unViewManager.getUnsavedViews();
	}

	public MainWindow getMainWindow()
	{
		return mainWindow;
	}

	public IProject getProject()
	{
		return project;
	}

	private GrviewManager(MainWindow window, String projectPath)
	{
		GrviewManager.instance = this;
		this.mainWindow = window;
		this.project = ProjectHelper.openProject(projectPath);
		this.unViewManager = new UnsavedViewManager();
		this.fileManager = new FileManager();
	}

	public void print(Object object)
	{
		if (object instanceof StandaloneTextArea)
		{
			TextPrinter.printText(((StandaloneTextArea) object).getText());
		}
		else if (object instanceof Canvas)
		{
			ComponentPrinter.printWidget((Canvas) object);
		}
	}

	public void saveAllFiles()
	{
		fileManager.saveAllFiles(getUnsavedViews());
	}

	public void saveFile(Object object)
	{
		fileManager.saveFileObject(object);
	}

	public boolean saveProject()
	{
		return project.save();
	}

	public void renameFile(String oldName, String newName)
	{
		project.renameFile(oldName, newName);
	}

	public void openFile(String path)
	{
		fileManager.openFile(path);
	}

	public void closeFile(String fileName)
	{
		fileManager.closeFile(fileName);
	}

	public void createFile(String name, FileNames extension) throws IOException
	{
		fileManager.createFile(name, extension);
	}

	public boolean isFileOpen(String absolutePath)
	{
		return fileManager.isFileOpen(absolutePath);
	}

	public boolean hasUnsavedView(DynamicView dynamicView)
	{
		return unViewManager.hasUnsavedView(dynamicView);
	}

	public void removeUnsavedView(String path)
	{
		unViewManager.removeUnsavedView(path);
	}

	public boolean hasUnsavedView(String file)
	{
		return unViewManager.hasUnsavedView(file);
	}

	public void setUnsavedView(String path, DynamicView view)
	{
		unViewManager.setUnsavedView(path, view);
	}

	public Canvas getActiveScene()
	{
		return activeScene;
	}

	public void setActiveScene(Canvas activeScene)
	{
		this.activeScene = activeScene;
	}

	public IViewManager getUnsavedViewManager()
	{
		return unViewManager;
	}
}
