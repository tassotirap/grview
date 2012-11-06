package org.grview.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.grview.file.GrammarFile;
import org.grview.file.LexicalFile;
import org.grview.file.MetaFile;
import org.grview.file.PropertiesFile;
import org.grview.file.SemanticFile;
import org.grview.model.FileNames;
import org.grview.project.interfaces.IProject;
import org.grview.semantics.SemanticRoutinesIvoker;
import org.grview.ui.ThemeManager.Theme;
import org.grview.util.Log;

/**
 * This class represents a projects and deals with the management of a project.
 * 
 * @author Gustavo H. Braga
 * @author Tasso Tirapani Silva Pinto
 */
public class Project implements Serializable, IProject
{
	private static final long serialVersionUID = -6812190878328950994L;

	private GrammarFile grammarFile;
	private LexicalFile lexicalFile;
	private SemanticFile semanticFile;
	
	private MetaFile metadataFile;
	private String name;
	private List<File> openedFiles;

	private File projectDir;
	private Properties properties;
	private PropertiesFile propertiesFile;
	
	private Theme theme = Theme.ShapedGradientDockingTheme;

	private File yyLexFile;

	public Project(String projectsRootPath, ArrayList<File> openedFiles)
	{
		this.projectDir = new File(projectsRootPath);
		try
		{
			if (openedFiles == null)
			{
				this.openedFiles = new ArrayList<File>();
			}
			else
			{
				this.openedFiles = openedFiles;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Project(String projectsRootPath)
	{
		this(projectsRootPath, null);
	}

	@Override
	public void init()
	{
		try
		{
			new SemanticRoutinesIvoker(this.getSemanticFile());
		}
		catch (MalformedURLException e)
		{
			Log.log(Log.ERROR, this, "Could not find path to semantic file!", e);
		}
	}

	@Override
	public GrammarFile getGrammarFile()
	{
		return grammarFile;
	}

	@Override
	public LexicalFile getLexicalFile()
	{
		return lexicalFile;
	}

	@Override
	public MetaFile getMetadataFile()
	{
		return metadataFile;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public List<File> getOpenedFiles()
	{
		return openedFiles;
	}

	@Override
	public File getProjectDir()
	{
		return projectDir;
	}

	@Override
	public String getProjectsRootPath()
	{
		return projectDir.getAbsolutePath();
	}

	@Override
	public File getPropertiesFile()
	{
		return propertiesFile;
	}

	@Override
	public String getProperty(String propertyName)
	{
		return getProperties().getProperty(propertyName);
	}

	@Override
	public File getSemanticFile()
	{
		return semanticFile;
	}

	@Override
	public Theme getTheme()
	{
		return this.theme;
	}

	@Override
	public File getYyLexFile()
	{
		return yyLexFile;
	}

	@Override
	public void putPropertiesToFile(String propertiesFile)
	{
		final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n" + "<properties>\n" + "\t<comment>Self generated properties</comment>\n";
		final String tail = "</properties>";
		String body = "";
		try
		{
			for (Object key : getProperties().keySet())
			{
				body += "\t<entry key=\"" + key.toString() + "\">" + getProperties().get(key).toString() + "</entry>\n";
			}
			File pf = new File(propertiesFile);
			FileWriter fw = new FileWriter(pf);
			fw.write(header + body + tail);
			fw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setGrammarFile(GrammarFile grammarFile)
	{
		this.grammarFile = grammarFile;
	}

	@Override
	public void setLexicalFile(LexicalFile lexFile)
	{
		this.lexicalFile = lexFile;
	}

	@Override
	public void setMetadataFile(MetaFile metadataFile)
	{
		this.metadataFile = metadataFile;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public void setOpenedFiles(List<File> openedFiles)
	{
		this.openedFiles = openedFiles;
	}

	@Override
	public void setPropertiesFile(PropertiesFile propertiesFile)
	{
		this.propertiesFile = propertiesFile;
	}

	@Override
	public void setSemamticFile(SemanticFile semFile)
	{
		this.semanticFile = semFile;
	}

	@Override
	public void setTheme(Theme theme)
	{
		this.theme = theme;
	}

	@Override
	public void setYyLexFile(File yyLexFile)
	{
		this.yyLexFile = yyLexFile;
	}

	@Override
	public Properties getProperties()
	{
		return properties;
	}

	@Override
	public void setProperties(Properties properties)
	{
		this.properties = properties;
	}
	
	@Override
	public void renameFile(String oldName, String newName)
	{
		if (oldName.endsWith(FileNames.GRAM_EXTENSION))
		{
			setGrammarFile(new GrammarFile(newName));
		}
		else if (oldName.endsWith(FileNames.SEM_EXTENSION))
		{
			setSemamticFile(new SemanticFile(newName));
		}
		else if (oldName.endsWith(FileNames.LEX_EXTENSION))
		{
			setLexicalFile(new LexicalFile(newName));
		}
		save();
	}
	
	@Override
	public boolean save()
	{
		try
		{
			FileOutputStream fileOutputStream = new FileOutputStream(getMetadataFile());
			new ObjectOutputStream(fileOutputStream).writeObject(this);
			fileOutputStream.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
