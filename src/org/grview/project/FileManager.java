package org.grview.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.grview.canvas.Canvas;
import org.grview.editor.TextArea;
import org.grview.model.FileNames;
import org.grview.model.ui.IconRepository;
import org.grview.project.interfaces.IFileManager;
import org.grview.project.interfaces.IProject;
import org.grview.project.interfaces.IViewManager;
import org.grview.project.tree.FileTree;
import org.grview.ui.DynamicView;
import org.grview.ui.MainWindow;
import org.grview.ui.Window;
import org.grview.ui.component.AbstractComponent;
import org.grview.ui.component.AdvancedTextAreaComponent;
import org.grview.ui.component.FileComponent;
import org.grview.ui.component.GrammarComponent;
import org.grview.ui.component.GrammarFactory;
import org.grview.ui.component.InputAdapterComponent;
import org.grview.ui.component.JavaComponent;
import org.grview.ui.component.LexComponent;
import org.grview.ui.component.SemComponent;
import org.grview.ui.component.TextAreaRepo;
import org.grview.ui.component.XMLComponent;
import org.grview.util.Log;

public class FileManager implements IFileManager
{
	private IProject project;
	private MainWindow mainWindow;
	private IViewManager viewManager;

	public FileManager(IProject project, MainWindow mainWindow, IViewManager viewManager)
	{
		this.project = project;
		this.mainWindow = mainWindow;
		this.viewManager = viewManager;
	}

	@Override
	public void closeFile(String fileName)
	{
		for (File file : project.getOpenedFiles())
		{
			if (file.getAbsolutePath().equals(fileName))
			{
				project.getOpenedFiles().remove(file);
				break;
			}
		}
		project.save();
	}

	@Override
	public void createFile(String name, FileNames extension) throws IOException
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
			if (project.getSemanticFile() != null)
			{
				JOptionPane.showMessageDialog(null, "Only one semantic routines file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileNames.LEX_EXTENSION))
		{
			if (project.getLexicalFile() != null)
			{
				JOptionPane.showMessageDialog(null, "Only one lexical scanner file is allowed by project.", "Could not create file", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (extension.getExtension().equals(FileNames.OUT_EXTENSION))
		{
			if (project.getLexicalFile() != null)
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
				Log.log(Log.WARNING, mainWindow, "A security exception was thrown while trying to create a new file.", e);
			}
			FileTree.reload(baseDir.getAbsolutePath());
		}
	}

	@Override
	public boolean isFileOpen(String fileName)
	{
		for (File file : project.getOpenedFiles())
		{
			if (file.getAbsolutePath().equals(fileName))
				return true;
		}
		return false;
	}

	@Override
	public void openFile(String path)
	{
		File file = new File(path);
		if (!isFileOpen(path))
		{
			try
			{
				if (file.getName().toLowerCase().endsWith(FileNames.LEX_EXTENSION.toLowerCase()))
				{
					LexComponent lexComponent = new LexComponent();
					mainWindow.addComponent(lexComponent.create(path), lexComponent, file.getName(), path, IconRepository.getInstance().LEX_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.SEM_EXTENSION.toLowerCase()))
				{
					SemComponent semComponent = new SemComponent();
					mainWindow.addComponent(semComponent.create(path), semComponent, file.getName(), path, IconRepository.getInstance().SEM_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.GRAM_EXTENSION.toLowerCase()))
				{
					GrammarComponent gramComponent = new GrammarComponent();
					mainWindow.addComponent(gramComponent.create(path), gramComponent, file.getName(), path, IconRepository.getInstance().GRAM_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.XML_EXTENSION.toLowerCase()))
				{
					XMLComponent xMLComponent = new XMLComponent();
					mainWindow.addComponent(xMLComponent.create(path), xMLComponent, file.getName(), path, IconRepository.getInstance().XML_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.JAVA_EXTENSION.toLowerCase()))
				{
					JavaComponent javaComponent = new JavaComponent();
					mainWindow.addComponent(javaComponent.create(path), javaComponent, file.getName(), path, IconRepository.getInstance().JAVA_ICON, Window.CENTER_TABS);
				}
				else if (file.getName().toLowerCase().endsWith(FileNames.IN_EXTENSION.toLowerCase()))
				{
					InputAdapterComponent inputAdapterComponent = new InputAdapterComponent();
					mainWindow.addComponent(inputAdapterComponent.create(path), inputAdapterComponent, file.getName(), path, IconRepository.getInstance().IN_ICON, Window.CENTER_TABS);
				}
				else
				{
					AdvancedTextAreaComponent advancedTextAreaComponent = new AdvancedTextAreaComponent(null);
					mainWindow.addComponent(advancedTextAreaComponent.create(path), advancedTextAreaComponent, file.getName(), path, IconRepository.getInstance().TXT_ICON, Window.CENTER_TABS);
				}
				project.getOpenedFiles().add(new File(path));

				project.save();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void saveAllFiles(ArrayList<DynamicView> views)
	{
		for (DynamicView dynamicView : views)
		{
			AbstractComponent abstractComponent = dynamicView.getComponentModel();
			if (abstractComponent instanceof AdvancedTextAreaComponent)
			{
				AdvancedTextAreaComponent advancedTextAreaComponent = (AdvancedTextAreaComponent) abstractComponent;
				saveFileObject(TextAreaRepo.getComponent(advancedTextAreaComponent.getTextArea()));
			}
			if (abstractComponent instanceof GrammarComponent)
			{
				saveFileObject(abstractComponent);
			}
		}
	}

	@Override
	public void saveFileObject(Object object)
	{
		String path = null;
		boolean componentSaved = false;
		if (object != null)
		{
			if (object instanceof TextArea)
			{
				path = saveFile((TextArea) object);
				componentSaved = true;
			}
			else if (object instanceof FileComponent)
			{
				path = saveFile((FileComponent) object);
				componentSaved = true;
			}
			else if (object instanceof Canvas)
			{
				path = saveFile((Canvas) object);
				componentSaved = true;
			}
			else if (object instanceof GrammarComponent)
			{
				path = saveFile((GrammarComponent) object);
			}
			else if (object instanceof String)
			{
				path = (String) object;
			}
		}

		if (path == null)
			return;

		if (project != null)
		{
			if (mainWindow != null && !componentSaved)
			{
				for (DynamicView dynamicView : viewManager.getUnsavedViews())
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
			project.save();
			FileTree.reload(project.getProjectsRootPath());
		}
	}

	private String saveFile(TextArea object)
	{
		FileComponent fileComponent = TextAreaRepo.getComponent((TextArea) object);
		saveFile(fileComponent);
		return fileComponent.getPath();
	}

	private String saveFile(FileComponent fileComponent)
	{
		fileComponent.saveFile();
		return fileComponent.getPath();
	}

	private String saveFile(Canvas canvas)
	{
		GrammarComponent grammarComponent = GrammarFactory.getGrammarComponent();
		if (viewManager.hasUnsavedView(grammarComponent.getPath()))
		{
			grammarComponent.saveFile();
			return grammarComponent.getPath();
		}
		return null;
	}

	private String saveFile(GrammarComponent grammarComponent)
	{
		grammarComponent.saveFile();
		return grammarComponent.getPath();
	}

}
