package org.grview.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;

import org.grview.file.GrammarFile;
import org.grview.file.LexicalFile;
import org.grview.file.MetaFile;
import org.grview.file.PropertiesFile;
import org.grview.file.SemanticFile;
import org.grview.lexical.YyFactory;
import org.grview.model.FileNames;
import org.grview.semantics.SemanticRoutinesIvoker;
import org.grview.ui.ThemeManager.Theme;
import org.grview.util.IOUtilities;
import org.grview.util.Log;

/**
 * This class represents a projects and deals with the management of a project.
 * 
 * @author Gustavo H. Braga
 * @author Tasso Tirapani Silva Pinto
 */
public class Project implements Serializable
{

	private static final long serialVersionUID = -6812190878328950994L;
	public final static String DEFAULT_DESCRIPTION = "New project";

	public final static String DEFAULT_NAME = "Untitled";

	/**
	 * Current GrammarFile
	 */
	private GrammarFile grammarFile;
	private LexicalFile lexicalFile;
	private MetaFile metadataFile;
	/** this project's name **/
	private String name;
	private ArrayList<File> openedFiles;

	private File projectDir;
	/** this project's properties **/
	private Properties properties;
	private PropertiesFile propertiesFile;
	private SemanticFile semanticFile;

	/**
	 * Current Theme
	 */
	private Theme theme = Theme.ShapedGradientDockingTheme;

	private File yyLexFile;

	/**
	 * the AsinEditor instance, that holds a representation of the current
	 * grammar
	 **/

	private Project(String projectsRootPath, ArrayList<File> openedFiles)
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

	/**
	 * Creates a new project
	 * 
	 * @param baseDir
	 *            the absolute path to the new project
	 * @return an instance of a new project
	 */
	public static Project createProject(File baseDir, String name, String description) throws IOException
	{
		final Properties properties = new Properties();
		if (name == null)
		{
			name = DEFAULT_NAME;
		}
		if (description == null)
		{
			description = DEFAULT_DESCRIPTION;
		}
		properties.put("name", name);
		properties.put("description", description);
		properties.put("baseDir", baseDir);
		GrammarFile gramFile = new GrammarFile(baseDir.getAbsoluteFile() + "/" + name + FileNames.GRAM_EXTENSION);
		SemanticFile semFile = new SemanticFile(baseDir.getAbsoluteFile() + "/" + name + FileNames.SEM_EXTENSION);
		LexicalFile lexFile = new LexicalFile(baseDir.getAbsoluteFile() + "/" + name + FileNames.LEX_EXTENSION);
		PropertiesFile propertiesFile = new PropertiesFile(baseDir.getAbsoluteFile() + "/" + FileNames.PROPERTIES_FILENAME);
		MetaFile metadataFile = new MetaFile(baseDir.getAbsoluteFile() + "/" + FileNames.METADATA_FILENAME);
		if ((!gramFile.exists() && !gramFile.createNewFile()) || (!semFile.exists() && !semFile.createNewFile()) || (!lexFile.exists() && !lexFile.createNewFile()) || (!propertiesFile.exists() && !propertiesFile.createNewFile()) || (!metadataFile.exists() && !metadataFile.createNewFile()))
		{
			throw new IOException("Could not create files");
		}
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/empty_grammar"), gramFile);
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/empty_semmantic"), semFile);
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/empty_lex"), lexFile);
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/default_properties.xml"), propertiesFile);
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/new_metadata"), metadataFile);

		Project project = new Project(baseDir.getAbsolutePath(), null);
		project.grammarFile = gramFile;
		project.semanticFile = semFile;
		project.lexicalFile = lexFile;
		project.propertiesFile = propertiesFile;
		project.metadataFile = metadataFile;
		project.properties = properties;
		project.openedFiles.add(gramFile);
		project.putPropertiesToFile(propertiesFile.getAbsolutePath());
		YyFactory.createYylex(baseDir.getAbsolutePath(), "generated_code", lexFile.getAbsolutePath());
		project.setYyLexFile(new File(baseDir.getAbsoluteFile() + "/generated_code", "Yylex.java"));
		project.init();
		return project;
	}

	public static boolean isProject(File dir)
	{
		boolean hasGrammarFile = false;
		boolean hasSemanticFile = false;
		boolean hasLexicalFile = false;
		boolean hasPropertiesFile = false;
		boolean hasMetaFile = false;

		for (File file : dir.listFiles())
		{
			if (file.getName().endsWith(FileNames.GRAM_EXTENSION))
			{
				hasGrammarFile = true;
			}
			else if (file.getName().endsWith(FileNames.SEM_EXTENSION))
			{
				hasSemanticFile = true;
			}
			else if (file.getName().endsWith(FileNames.LEX_EXTENSION))
			{
				hasLexicalFile = true;
			}
			else if (file.getName().equals(FileNames.PROPERTIES_FILENAME))
			{
				hasPropertiesFile = true;
			}
			else if (file.getName().equals(FileNames.METADATA_FILENAME))
			{
				hasMetaFile = true;
			}
		}
		return hasGrammarFile && hasSemanticFile && hasLexicalFile && hasPropertiesFile && hasMetaFile;
	}

	/**
	 * Restores a project stored in a METADATA file
	 * 
	 * @param projectRootPath
	 *            the root path of the project
	 * @return true if a serialized project was found
	 */
	public static Project restoreProject(String projectRootPath)
	{
		try
		{
			if (!(projectRootPath.endsWith("/") || projectRootPath.endsWith("\\")))
			{
				projectRootPath += "/";
			}
			File metaFile = new File(projectRootPath + FileNames.METADATA_FILENAME);
			FileInputStream fileInputStream = new FileInputStream(metaFile);
			if (metaFile.length() > 0)
			{
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				Object object = objectInputStream.readObject();
				if (object instanceof Project)
				{
					Project result = (Project) object;
					result.init();
					objectInputStream.close();
					return result;
				}
				objectInputStream.close();
			}
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Here goes stuff that needs to be done anyway, no matter if you have a
	 * project serialized or a new one
	 */
	private void init()
	{
		try
		{
			new SemanticRoutinesIvoker(this.getSemFile());
		}
		catch (MalformedURLException e)
		{
			Log.log(Log.ERROR, this, "Could not find path to semantic file!", e);
		}
	}

	public File getGrammarFile()
	{
		return grammarFile;
	}

	public File getLexFile()
	{
		return lexicalFile;
	}

	public File getMetadataFile()
	{
		return metadataFile;
	}

	public String getName()
	{
		return name;
	}

	public ArrayList<File> getOpenedFiles()
	{
		return openedFiles;
	}

	public File getProjectDir()
	{
		return projectDir;
	}

	public String getProjectsRootPath()
	{
		return projectDir.getAbsolutePath();
	}

	public File getPropertiesFile()
	{
		return propertiesFile;
	}

	public String getProperty(String propertyName)
	{
		return properties.getProperty(propertyName);
	}

	public File getSemFile()
	{
		return semanticFile;
	}

	public Theme getTheme()
	{
		return this.theme;
	}

	public File getYyLexFile()
	{
		return yyLexFile;
	}

	/**
	 * Puts all properties to a file.
	 * 
	 * @param propertiesFile
	 */
	public void putPropertiesToFile(String propertiesFile)
	{
		final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n" + "<properties>\n" + "\t<comment>Self generated properties</comment>\n";
		final String tail = "</properties>";
		String body = "";
		try
		{
			for (Object key : properties.keySet())
			{
				body += "\t<entry key=\"" + key.toString() + "\">" + properties.get(key).toString() + "</entry>\n";
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

	public void setGrammarFile(GrammarFile grammarFile)
	{
		this.grammarFile = grammarFile;
	}

	public void setLexFile(LexicalFile lexFile)
	{
		this.lexicalFile = lexFile;
	}

	public void setMetadataFile(MetaFile metadataFile)
	{
		this.metadataFile = metadataFile;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setOpenedFiles(ArrayList<File> openedFiles)
	{
		this.openedFiles = openedFiles;
	}

	public void setPropertiesFile(PropertiesFile propertiesFile)
	{
		this.propertiesFile = propertiesFile;
	}

	public void setSemFile(SemanticFile semFile)
	{
		this.semanticFile = semFile;
	}

	public void setTheme(Theme theme)
	{
		this.theme = theme;
	}

	public void setYyLexFile(File yyLexFile)
	{
		this.yyLexFile = yyLexFile;
	}

	/**
	 * Saves this project to disk. This project instance will be serialized and
	 * all its info will be saved on METADATA_FILENAME;
	 * 
	 * @return true if successfully saved the project, false otherwise
	 */
	public boolean writeProject()
	{
		try
		{
			FileOutputStream fileOutputStream = new FileOutputStream(metadataFile);
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
