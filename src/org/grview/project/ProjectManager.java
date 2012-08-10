package org.grview.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.grview.actions.ActionContextHolder;
import org.grview.actions.AsinActionContext;
import org.grview.actions.AsinActionSet;
import org.grview.bsh.input.AbstractInputHandler;
import org.grview.bsh.input.DefaultInputHandlerProvider;
import org.grview.bsh.input.InputHandlerProvider;
import org.grview.bsh.input.ProjectManagerInputHandler;
import org.grview.canvas.Canvas;
import org.grview.editor.StandaloneTextArea;
import org.grview.editor.TextArea;
import org.grview.model.FileExtension;
import org.grview.model.ui.IconRepository;
import org.grview.project.tree.FileTree;
import org.grview.ui.DynamicView;
import org.grview.ui.MainWindow;
import org.grview.ui.Window;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.AdvancedTextAreaComponent;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GramComponent;
import org.grview.ui.component.GrammarRepo;
import org.grview.ui.component.InputAdapterComponent;
import org.grview.ui.component.JavaComponent;
import org.grview.ui.component.LexComponent;
import org.grview.ui.component.SemComponent;
import org.grview.ui.component.TextAreaRepo;
import org.grview.ui.component.XMLComponent;
import org.grview.util.ComponentPrinter;
import org.grview.util.Log;
import org.grview.util.TextPrinter;

public class ProjectManager
{

	private Window window;
	private static Project project;
	private static ProjectManager projectManager;

	private static HashMap<String, DynamicView> unsavedViews = new HashMap<String, DynamicView>();

	public static ArrayList<DynamicView> getUnsavedViews()
	{
		return new ArrayList<DynamicView>(unsavedViews.values());
	}

	public static void setUnsavedView(String key, DynamicView value)
	{
		if (!unsavedViews.containsKey(key))
			unsavedViews.put(key, value);
	}

	public static boolean hasUnsavedView(String key)
	{
		return unsavedViews.containsKey(key);
	}

	public static boolean hasUnsavedView(DynamicView value)
	{
		return unsavedViews.containsValue(value);
	}

	public static void removeUnsavedView(String key)
	{
		unsavedViews.remove(key);
	}

	/* unsaved files (path) */

	public ProjectManager(Window window, String projectPath)
	{
		this.window = window;
		Project project = Project.restoreProject(projectPath);		
		ProjectManager.setProject(project);
		ProjectManager.setProjectManager(this);
	}

	
	public static void print(Object object)
	{
		if(object instanceof Canvas)
			ComponentPrinter.printWidget((Canvas)object);
		else if(object instanceof StandaloneTextArea)
			TextPrinter.printText(((StandaloneTextArea)object).getText());
			
	}


	public static void saveAllFiles()
	{
		if (getProject() != null)
		{
			MainWindow mainWindow = MainWindow.getInstance(getProject().getProjectDir().getAbsolutePath());
			if (mainWindow != null)
			{
				for (DynamicView dynamicView : ProjectManager.getUnsavedViews())
				{
					AbstractComponent abstractComponent = dynamicView.getComponentModel();
					if (abstractComponent instanceof AdvancedTextAreaComponent)
					{
						AdvancedTextAreaComponent advancedTextAreaComponent = (AdvancedTextAreaComponent) abstractComponent;
						saveFile(TextAreaRepo.getComponent(advancedTextAreaComponent.getTextArea()));
					}
					if (abstractComponent instanceof GramComponent)
					{
						saveFile(abstractComponent);
					}

				}
			}
		}
	}

	public static void saveFile(Object... params)
	{
		String path = null;
		boolean componentSaved = false;
		if (params.length >= 1)
		{
			Object object = params[0];
			if (object instanceof TextArea || object instanceof FileComponent)
			{
				path = saveTextAreaOrFileComponent(object);
				componentSaved = true;
			}
			else if (object instanceof Canvas)
			{
				path = saveGram(object);
				componentSaved = true;
			}
			else if (object instanceof GramComponent)
			{
				GramComponent gram = (GramComponent) object;
				gram.saveFile();
				path = gram.getPath();
			}
			else if (object instanceof String)
			{
				path = (String) object;
			}
		}

		if (getProject() != null)
		{
			MainWindow mainWindow = MainWindow.getInstance(getProject().getProjectDir().getAbsolutePath());
			if (mainWindow != null && !componentSaved)
			{
				for (DynamicView dynamicView : ProjectManager.getUnsavedViews())
				{
					AbstractComponent comp = dynamicView.getComponentModel();
					if (comp instanceof FileComponent && ((FileComponent) comp).getPath().equals(path))
					{
						((FileComponent) comp).saveFile();
					}
				}
			}
			if (mainWindow != null)
			{
				mainWindow.setSaved(path);
			}
			getProject().writeProject();

			FileTree.reload(getProject().getProjectsRootPath());
		}
	}

	private static String saveGram(Object object)
	{
		GramComponent gramComponent = (GramComponent) GrammarRepo.getCompByCanvas((Canvas) object);
		gramComponent.saveFile();
		return gramComponent.getPath();
	}

	private static String saveTextAreaOrFileComponent(Object object)
	{
		FileComponent fileComponent;
		if (object instanceof TextArea)
		{
			fileComponent = TextAreaRepo.getComponent((TextArea) object);
		}
		else
		{
			fileComponent = (FileComponent) object;
		}

		if (fileComponent != null)
		{
			fileComponent.saveFile();
			return fileComponent.getPath();
		}

		return null;
	}

	public void createFile(String name, FileExtension extension) throws IOException
	{
		if (extension.getExtension().equals(FileExtension.GRAM_FILE))
		{
			if (getProject().getGrammarFile().get(getProject().getVersion()) != null)
			{
				JOptionPane.showMessageDialog(null, "Only one grammar file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileExtension.SEM_FILE))
		{
			if (getProject().getSemFile().get(getProject().getVersion()) != null)
			{
				JOptionPane.showMessageDialog(null, "Only one semantic routines file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileExtension.LEX_FILE))
		{
			if (getProject().getLexFile().get(getProject().getVersion()) != null)
			{
				JOptionPane.showMessageDialog(null, "Only one lexical scanner file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileExtension.OUT_FILE))
		{
			if (getProject().getLexFile().get(getProject().getVersion()) != null)
			{
				JOptionPane.showMessageDialog(null, "This kind of file is not supported yet", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else
		{
			File baseDir = getProject().getProjectDir();
			File newFile = null;
			if (extension != null)
			{
				if (!name.endsWith(extension.getExtension()))
				{
					name += extension.getExtension();
				}
			}
			if (!name.startsWith(baseDir.getAbsolutePath()))
			{
				newFile = new File(baseDir, name);
			}
			else
			{
				newFile = new File(name);
			}
			try
			{
				newFile.createNewFile();
				openFile(newFile.getAbsolutePath());
			}
			catch (SecurityException e)
			{
				JOptionPane.showMessageDialog(null, "Could not create file. Probably you do not have permission to write on disk.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
				Log.log(Log.WARNING, this, "A security exception was thrown while trying to create a new file.", e);
			}
			FileTree.reload(baseDir.getAbsolutePath());
		}
	}

	/**
	 * Open a file
	 * 
	 * @param path
	 *            the absolute path of the file.
	 */
	public void openFile(String path)
	{
		File file = new File(path);
		if (!isFileOpen(path))
		{
			try
			{
				if (file.getName().toLowerCase().endsWith(FileExtension.LEX_FILE.toLowerCase()))
				{
					LexComponent lc = new LexComponent();
					window.addComponent(lc.create(path), lc, file.getName(), path, IconRepository.getInstance().LEX_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.SEM_FILE.toLowerCase()))
				{
					SemComponent sc = new SemComponent();
					window.addComponent(sc.create(path), sc, file.getName(), path, IconRepository.getInstance().SEM_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.GRAM_FILE.toLowerCase()))
				{
					GramComponent gc = new GramComponent();
					window.addComponent(gc.create(path), gc, file.getName(), path, IconRepository.getInstance().GRAM_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.XML_FILE.toLowerCase()))
				{
					XMLComponent xc = new XMLComponent();
					window.addComponent(xc.create(path), xc, file.getName(), path, IconRepository.getInstance().XML_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.JAVA_FILE.toLowerCase()))
				{
					JavaComponent jc = new JavaComponent();
					window.addComponent(jc.create(path), jc, file.getName(), path, IconRepository.getInstance().JAVA_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileExtension.IN_FILE.toLowerCase()))
				{
					InputAdapterComponent iac = new InputAdapterComponent();
					window.addComponent(iac.create(path), iac, file.getName(), path, IconRepository.getInstance().IN_ICON, Window.CENTER_TABS);
				}
				else
				{
					AdvancedTextAreaComponent atac = new AdvancedTextAreaComponent(null);
					window.addComponent(atac.create(path), atac, file.getName(), path, IconRepository.getInstance().TXT_ICON, Window.CENTER_TABS);
				}
				getProject().getOpenedFiles().add(new File(path));
				getProject().writeProject();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Close an opened file from the perspective of the current project
	 * 
	 * @param fileName
	 *            , the absolute file name of the file being closed.
	 */
	public void closeFile(String fileName)
	{
		for (File file : getProject().getOpenedFiles())
		{
			if (file.getAbsolutePath().equals(fileName))
			{
				getProject().getOpenedFiles().remove(file);
				break;
			}
		}
		getProject().writeProject();
	}

	/**
	 * Informs whether a file is currently opened or not.
	 * 
	 * @param fileName
	 *            the full path of the file
	 * @return true if the file is open
	 */
	public boolean isFileOpen(String fileName)
	{
		for (File f : getProject().getOpenedFiles())
		{
			if (f.getAbsolutePath().equals(fileName))
				return true;
		}
		return false;
	}

	public void renameFile(String oldName, String newName)
	{
		File fr = null;
		for (File f : getProject().getOpenedFiles())
		{
			if (f.getAbsolutePath().equals(oldName))
			{
				fr = f;
				break;
			}
		}
		if (fr != null && getProject().getOpenedFiles().contains(fr))
		{
			getProject().getOpenedFiles().remove(fr);
			getProject().getOpenedFiles().add(new File(newName));
		}
		if (oldName.endsWith(FileExtension.GRAM_FILE))
		{
			getProject().getGrammarFile().put(getProject().getVersion(), new File(newName));
		}
		else if (oldName.endsWith(FileExtension.SEM_FILE))
		{
			getProject().getSemFile().put(getProject().getVersion(), new File(newName));
		}
		else if (oldName.endsWith(FileExtension.LEX_FILE))
		{
			getProject().getLexFile().put(getProject().getVersion(), new File(newName));
		}
		getProject().writeProject();
	}


	public static Project getProject()
	{
		return project;
	}

	public static void setProject(Project project)
	{
		ProjectManager.project = project;
	}

	public static ProjectManager getProjectManager()
	{
		return projectManager;
	}

	public static void setProjectManager(ProjectManager projectManager)
	{
		ProjectManager.projectManager = projectManager;
	}
}
