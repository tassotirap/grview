package org.grview.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Properties;

import org.grview.file.GrammarFile;
import org.grview.file.LexicalFile;
import org.grview.file.MetaFile;
import org.grview.file.PropertiesFile;
import org.grview.file.SemanticFile;
import org.grview.lexical.YyFactory;
import org.grview.model.FileNames;
import org.grview.project.interfaces.IProject;

public class ProjectHelper
{
	public final static String DEFAULT_DESCRIPTION = "New project";
	public final static String DEFAULT_NAME = "Untitled";

	public static void createNewProject(File projectRoot) throws IOException
	{

		String name = DEFAULT_NAME;
		String description = DEFAULT_DESCRIPTION;

		final Properties properties = new Properties();
		properties.put("name", name);
		properties.put("description", description);
		properties.put("baseDir", projectRoot);

		String basePath = projectRoot.getAbsoluteFile() + "/";

		GrammarFile gramFile = new GrammarFile(basePath + name + FileNames.GRAM_EXTENSION);
		gramFile.create();
		
		
		SemanticFile semFile = new SemanticFile(basePath + name + FileNames.SEM_EXTENSION);
		LexicalFile lexFile = new LexicalFile(basePath + name + FileNames.LEX_EXTENSION);
		PropertiesFile propertiesFile = new PropertiesFile(basePath + FileNames.PROPERTIES_FILENAME);
		MetaFile metadataFile = new MetaFile(basePath + FileNames.METADATA_FILENAME);

		
		semFile.create();
		lexFile.create();
		propertiesFile.create();
		metadataFile.create();

		IProject project = new Project(projectRoot.getAbsolutePath());
		project.setGrammarFile(gramFile);
		project.setSemamticFile(semFile);
		project.setLexicalFile(lexFile);
		project.setPropertiesFile(propertiesFile);
		project.setMetadataFile(metadataFile);
		project.setProperties(properties);
		project.getOpenedFiles().add(gramFile);
		project.putPropertiesToFile(propertiesFile.getAbsolutePath());

		YyFactory.createYylex(projectRoot.getAbsolutePath(), "generated_code", lexFile.getAbsolutePath());
		project.setYyLexFile(new File(projectRoot.getAbsoluteFile() + "/generated_code", "Yylex.java"));

		project.save();
	}

	public static IProject openProject(String projectRootPath)
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
				if (object instanceof IProject)
				{
					IProject result = (IProject) object;
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

	public static boolean isProject(File projectRoot)
	{
		boolean hasGrammarFile = false;
		boolean hasSemanticFile = false;
		boolean hasLexicalFile = false;
		boolean hasPropertiesFile = false;
		boolean hasMetaFile = false;

		for (File file : projectRoot.listFiles())
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
}
