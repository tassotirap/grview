package org.grview.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.grview.canvas.Canvas;
import org.grview.canvas.state.StaticStateManager;
import org.grview.editor.StandaloneTextArea;
import org.grview.editor.TextArea;
import org.grview.file.GrammarFile;
import org.grview.file.LexicalFile;
import org.grview.file.SemanticFile;
import org.grview.model.FileNames;
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

public final class ProjectManager
{

	private static MainWindow window;
	private static Project project;

	private static HashMap<String, DynamicView> unsavedViews = new HashMap<String, DynamicView>();
	
	private static String saveGrammar(Object object)
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

	/**
	 * Close an opened file from the perspective of the current project
	 * 
	 * @param fileName
	 *            , the absolute file name of the file being closed.
	 */
	public static void closeFile(String fileName)
	{
		for (File file : project.getOpenedFiles())
		{
			if (file.getAbsolutePath().equals(fileName))
			{
				project.getOpenedFiles().remove(file);
				break;
			}
		}
		project.writeProject();
	}

	public static void createFile(String name, FileNames extension) throws IOException
	{
		if (extension.getExtension().equals(FileNames.GRAM_EXTENSION))
		{
			if (project.getGrammarFile() != null)
			{
				JOptionPane.showMessageDialog(null, "Only one grammar file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileNames.SEM_EXTENSION))
		{
			if (project.getSemFile() != null)
			{
				JOptionPane.showMessageDialog(null, "Only one semantic routines file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileNames.LEX_EXTENSION))
		{
			if (project.getLexFile() != null)
			{
				JOptionPane.showMessageDialog(null, "Only one lexical scanner file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileNames.OUT_EXTENSION))
		{
			if (project.getLexFile() != null)
			{
				JOptionPane.showMessageDialog(null, "This kind of file is not supported yet", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else
		{
			File baseDir = project.getProjectDir();
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
				Log.log(Log.WARNING, getWindow(), "A security exception was thrown while trying to create a new file.", e);
			}
			FileTree.reload(baseDir.getAbsolutePath());
		}
	}

	public static void exit()
	{
		ArrayList<DynamicView> unsavedViews = getUnsavedViews();

		for (DynamicView dynamicView : unsavedViews)
		{
			int option = JOptionPane.showConfirmDialog(getWindow().getFrame(), "Would you like to save '" + dynamicView.getTitle().replace(Window.UNSAVED_PREFIX, "") + "' before exiting?");
			if (option == JOptionPane.CANCEL_OPTION)
				return;
			if (option == JOptionPane.YES_OPTION && dynamicView.getComponentModel() instanceof GramComponent)
			{
				StaticStateManager StaticStateManager = getWindow().getActiveScene().getStaticStateManager();
				try
				{
					StaticStateManager.write();
					String path = StaticStateManager.getParentDirectory();
					saveFile(path);
				}
				catch (IOException e)
				{
					Log.log(Log.ERROR, getWindow(), "Could not save file", e);
				}
			}
			else if (option == JOptionPane.YES_OPTION)
			{
				saveFile(dynamicView.getComponentModel());
			}
		}
		getProject().writeProject();
		System.exit(0);
	}

	public static Project getProject()
	{
		return project;
	}

	public static ArrayList<DynamicView> getUnsavedViews()
	{
		return new ArrayList<DynamicView>(unsavedViews.values());
	}

	public static MainWindow getWindow()
	{
		return window;
	}

	public static boolean hasUnsavedView(DynamicView value)
	{
		return unsavedViews.containsValue(value);
	}

	public static boolean hasUnsavedView(String key)
	{
		return unsavedViews.containsKey(key);
	}

	public static void init(MainWindow window, String projectPath)
	{
		ProjectManager.window = window;
		ProjectManager.project = Project.restoreProject(projectPath);
	}

	/**
	 * Informs whether a file is currently opened or not.
	 * 
	 * @param fileName
	 *            the full path of the file
	 * @return true if the file is open
	 */
	public static boolean isFileOpen(String fileName)
	{
		for (File file : project.getOpenedFiles())
		{
			if (file.getAbsolutePath().equals(fileName))
				return true;
		}
		return false;
	}

	/**
	 * Open a file
	 * 
	 * @param path
	 *            the absolute path of the file.
	 */
	public static void openFile(String path)
	{
		File file = new File(path);
		if (!isFileOpen(path))
		{
			try
			{
				if (file.getName().toLowerCase().endsWith(FileNames.LEX_EXTENSION.toLowerCase()))
				{
					LexComponent lc = new LexComponent();
					getWindow().addComponent(lc.create(path), lc, file.getName(), path, IconRepository.getInstance().LEX_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.SEM_EXTENSION.toLowerCase()))
				{
					SemComponent sc = new SemComponent();
					getWindow().addComponent(sc.create(path), sc, file.getName(), path, IconRepository.getInstance().SEM_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.GRAM_EXTENSION.toLowerCase()))
				{
					GramComponent gc = new GramComponent();
					getWindow().addComponent(gc.create(path), gc, file.getName(), path, IconRepository.getInstance().GRAM_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.XML_EXTENSION.toLowerCase()))
				{
					XMLComponent xc = new XMLComponent();
					getWindow().addComponent(xc.create(path), xc, file.getName(), path, IconRepository.getInstance().XML_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.JAVA_EXTENSION.toLowerCase()))
				{
					JavaComponent jc = new JavaComponent();
					getWindow().addComponent(jc.create(path), jc, file.getName(), path, IconRepository.getInstance().JAVA_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.IN_EXTENSION.toLowerCase()))
				{
					InputAdapterComponent iac = new InputAdapterComponent();
					getWindow().addComponent(iac.create(path), iac, file.getName(), path, IconRepository.getInstance().IN_ICON, Window.CENTER_TABS);
				}
				else
				{
					AdvancedTextAreaComponent atac = new AdvancedTextAreaComponent(null);
					getWindow().addComponent(atac.create(path), atac, file.getName(), path, IconRepository.getInstance().TXT_ICON, Window.CENTER_TABS);
				}
				project.getOpenedFiles().add(new File(path));
				project.writeProject();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public static void print(Object object)
	{
		if (object instanceof Canvas)
		{
			ComponentPrinter.printWidget((Canvas) object);
		}
		else if (object instanceof StandaloneTextArea)
		{
			TextPrinter.printText(((StandaloneTextArea) object).getText());
		}

	}

	public static void removeUnsavedView(String key)
	{
		unsavedViews.remove(key);
	}

	public static void renameFile(String oldName, String newName)
	{
		File fileRename = null;
		for (File file : getProject().getOpenedFiles())
		{
			if (file.getAbsolutePath().equals(oldName))
			{
				fileRename = file;
				break;
			}
		}
		if (fileRename != null && project.getOpenedFiles().contains(fileRename))
		{
			project.getOpenedFiles().remove(fileRename);
			project.getOpenedFiles().add(new File(newName));
		}
		if (oldName.endsWith(FileNames.GRAM_EXTENSION))
		{
			project.setGrammarFile(new GrammarFile(newName));
		}
		else if (oldName.endsWith(FileNames.SEM_EXTENSION))
		{
			project.setSemFile(new SemanticFile(newName));
		}
		else if (oldName.endsWith(FileNames.LEX_EXTENSION))
		{
			project.setLexFile(new LexicalFile(newName));
		}
		project.writeProject();
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

	public static void saveFile(Object object)
	{
		String path = null;
		boolean componentSaved = false;
		if (object != null)
		{
			if (object instanceof TextArea || object instanceof FileComponent)
			{
				path = saveTextAreaOrFileComponent(object);
				componentSaved = true;
			}
			else if (object instanceof Canvas)
			{
				path = saveGrammar(object);
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

	public static void setUnsavedView(String key, DynamicView value)
	{
		if (!unsavedViews.containsKey(key))
		{
			unsavedViews.put(key, value);
		}
	}
}
