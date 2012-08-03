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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

import org.grview.lexical.YyFactory;
import org.grview.model.FileExtension;
import org.grview.semantics.SemanticRoutinesIvoker;
import org.grview.semantics.SemanticRoutinesRepo;
import org.grview.syntax.command.AsinEditor;
import org.grview.util.IOUtilities;
import org.grview.util.Log;

/**
 * This class represents a projects and deals with the management of a project.
 * 
 * @author Gustavo H. Braga
 */
public class Project implements Serializable
{

	private static final long serialVersionUID = -6812190878328950994L;
	private ArrayList<File> openedFiles;
	public final String projectsRootPath;
	public final File baseDir;

	private HashMap<Version, File> grammarFile = new HashMap<Version, File>();
	private HashMap<Version, File> lexFile = new HashMap<Version, File>();
	private HashMap<Version, File> semFile = new HashMap<Version, File>();
	private File yyLexFile;
	private File propertiesFile;
	private File metadataFile;
	private HashMap<String, String> codeByRoutine = new HashMap<String, String>();

	public static final String GRAM_EXT = FileExtension.GRAM_FILE;
	public static final String SEM_EXT = FileExtension.SEM_FILE;
	public static final String XML_EXT = FileExtension.XML_FILE;
	public static final String LEX_EXT = FileExtension.LEX_FILE;

	public final static String METADATA_FILENAME = ".METADATA";
	public final static String PROPERTIES_FILENAME = "properties.xml";

	/** this project's name **/
	private String name;
	public final static String DEFAULT_NAME = "Untitled";
	public final static String DEFAULT_DESCRIPTION = "New project";
	public final static String DEFAULT_SEMANTIC_ROUTINE_CLASS = "org.grview.semantics.SemanticRoutines";

	/** this project's version **/
	private Version version;

	/** this project's properties **/
	private Properties properties;

	/**
	 * the AsinEditor instance, that holds a representation of the current
	 * grammar
	 **/
	private AsinEditor asinEditor;

	private transient static HashMap<String, Project> projectByRootPath = new HashMap<String, Project>();

	public Project(String projectsRootPath)
	{
		this(projectsRootPath, null);
	}

	private Project(String projectsRootPath, ArrayList<File> openedFiles)
	{
		this.projectsRootPath = projectsRootPath;
		baseDir = new File(projectsRootPath);
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
			projectByRootPath.put(projectsRootPath, this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Here goes stuff that needs to be done anyway, no matter if you have a
	 * project serialized or a new one
	 */
	private void init()
	{
		try
		{
			new SemanticRoutinesIvoker(this);
			SemanticRoutinesRepo.setRoutineCode(codeByRoutine);
		}
		catch (MalformedURLException e)
		{
			Log.log(Log.ERROR, this, "Could not find path to semantic file!", e);
		}
	}

	/**
	 * Restores a project stored in a METADATA file
	 * 
	 * @param projectsRootPath
	 *            the root path of the project
	 * @return true if a serialized project was found
	 */
	public static Project restoreProject(String projectsRootPath)
	{
		try
		{
			if (!(projectsRootPath.endsWith("/") || projectsRootPath.endsWith("\\")))
			{
				projectsRootPath += "/";
			}
			File file = new File(projectsRootPath + METADATA_FILENAME);
			FileInputStream fileInputStream = new FileInputStream(file);
			if (file.length() > 0)
			{
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				Object object = objectInputStream.readObject();
				if (object instanceof Project)
				{
					Project result = (Project) object;
					AsinEditor.setInstance(result.asinEditor);
					projectByRootPath.put(projectsRootPath, result);
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
	 * Saves this project to disk. This project instance will be serialized and
	 * all its info will be saved on METADATA_FILENAME;
	 * 
	 * @return true if successfully saved the project, false otherwise
	 */
	public boolean writeProject()
	{
		try
		{
			this.codeByRoutine = SemanticRoutinesRepo.getRoutineCode();
			this.asinEditor = AsinEditor.getInstance();
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

	/**
	 * Creates a new project
	 * 
	 * @param baseDir
	 *            the absolute path to the new project
	 * @return an instance of a new project
	 */
	public static Project createProject(File baseDir, String name, String description) throws IOException
	{
		Project project = null;
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
		properties.put("semanticRoutineClass", DEFAULT_SEMANTIC_ROUTINE_CLASS);
		File gramFile = new File(baseDir.getAbsoluteFile() + "/" + name + GRAM_EXT);
		File semFile = new File(baseDir.getAbsoluteFile() + "/" + name + SEM_EXT);
		File lexFile = new File(baseDir.getAbsoluteFile() + "/" + name + LEX_EXT);
		File propertiesFile = new File(baseDir.getAbsoluteFile() + "/" + PROPERTIES_FILENAME);
		File metadataFile = new File(baseDir.getAbsoluteFile() + "/" + METADATA_FILENAME);
		if ((!gramFile.exists() && !gramFile.createNewFile()) || (!semFile.exists() && !semFile.createNewFile()) || (!lexFile.exists() && !lexFile.createNewFile()) || (!propertiesFile.exists() && !propertiesFile.createNewFile()) || (!metadataFile.exists() && !metadataFile.createNewFile()))
		{
			throw new IOException("Could not create files");
		}
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/empty_grammar"), gramFile);
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/empty_semmantic"), semFile);
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/empty_lex"), lexFile);
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/default_properties.xml"), propertiesFile);
		IOUtilities.copyFileFromInputSteam(Project.class.getResourceAsStream("/org/grview/project/new_metadata"), metadataFile);
		// make sure I have the correct routines.dtd
		// props.putAll(loadProperties(pf.getAbsolutePath(), true));
		project = new Project(baseDir.getAbsolutePath(), null);
		Version nv = new Version();
		nv.setCreationDate(Calendar.getInstance().getTime());
		nv.setModDate(Calendar.getInstance().getTime());
		nv.setVersionName("1");
		nv.setDescription("new project -" + name);
		project.setVersion(nv);
		project.grammarFile.put(nv, gramFile);
		project.semFile.put(nv, semFile);
		project.lexFile.put(nv, lexFile);
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
		boolean gramfile = false;
		boolean semfile = false;
		boolean lexfile = false;
		boolean propertiesfile = false;
		boolean metadatafile = false;

		for (File f : dir.listFiles())
		{
			if (f.getName().endsWith(GRAM_EXT))
			{
				gramfile = true;
			}
			else if (f.getName().endsWith(SEM_EXT))
			{
				semfile = true;
			}
			else if (f.getName().endsWith(LEX_EXT))
			{
				lexfile = true;
			}
			else if (f.getName().equals(PROPERTIES_FILENAME))
			{
				propertiesfile = true;
			}
			else if (f.getName().equals(METADATA_FILENAME))
			{
				metadatafile = true;
			}
		}
		return gramfile && semfile && lexfile && propertiesfile && metadatafile;
	}

	public static Project getProjectByPath(String rootPath)
	{
		if (rootPath == null)
		{
			return null;
		}
		if (projectByRootPath.containsKey(rootPath))
		{
			return projectByRootPath.get(rootPath);
		}
		else if (projectByRootPath.containsKey(rootPath.replace("\\", "/")))
		{
			return projectByRootPath.get(rootPath.replace("\\", "/"));
		}
		else if (rootPath.endsWith("/") || rootPath.endsWith("\\"))
		{
			if (projectByRootPath.containsKey(rootPath.substring(0, rootPath.length() - 1)))
				return projectByRootPath.get(rootPath.substring(0, rootPath.length() - 1));
		}
		else if (projectByRootPath.containsKey(rootPath + "/"))
		{
			return projectByRootPath.get(rootPath + "/");
		}
		else if (projectByRootPath.containsKey(rootPath + "\\"))
		{
			return projectByRootPath.get(rootPath + "\\");
		}
		File parent = new File(rootPath).getParentFile();
		if (parent != null)
		{
			return getProjectByPath(parent.getAbsolutePath());
		}
		return null;
	}

	public ArrayList<File> getOpenedFiles()
	{
		return openedFiles;
	}

	public String getProjectsRootPath()
	{
		return projectsRootPath;
	}

	public File getBaseDir()
	{
		return baseDir;
	}

	public HashMap<Version, File> getGrammarFile()
	{
		return grammarFile;
	}

	public void setGrammarFile(HashMap<Version, File> grammarFile)
	{
		this.grammarFile = grammarFile;
	}

	public HashMap<Version, File> getLexFile()
	{
		return lexFile;
	}

	public void setLexFile(HashMap<Version, File> lexFile)
	{
		this.lexFile = lexFile;
	}

	public HashMap<Version, File> getSemFile()
	{
		return semFile;
	}

	public void setSemFile(HashMap<Version, File> semFile)
	{
		this.semFile = semFile;
	}

	public File getPropertiesFile()
	{
		return propertiesFile;
	}

	public void setPropertiesFile(File propertiesFile)
	{
		this.propertiesFile = propertiesFile;
	}

	public File getMetadataFile()
	{
		return metadataFile;
	}

	public void setMetadataFile(File metadataFile)
	{
		this.metadataFile = metadataFile;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Version getVersion()
	{
		return version;
	}

	public void setVersion(Version version)
	{
		this.version = version;
	}

	public void setOpenedFiles(ArrayList<File> openedFiles)
	{
		this.openedFiles = openedFiles;
	}

	public String getProperty(String propertyName)
	{
		return properties.getProperty(propertyName);
	}

	public void setYyLexFile(File yyLexFile)
	{
		this.yyLexFile = yyLexFile;
	}

	public File getYyLexFile()
	{
		return yyLexFile;
	}
}
