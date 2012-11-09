package org.grview.project.interfaces;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.grview.file.GrammarFile;
import org.grview.file.LexicalFile;
import org.grview.file.MetaFile;
import org.grview.file.PropertiesFile;
import org.grview.file.SemanticFile;
import org.grview.ui.ThemeManager.Theme;

public interface IProject
{

	/**
	 * Here goes stuff that needs to be done anyway, no matter if you have a
	 * project serialized or a new one
	 */
	public abstract void init();

	public abstract GrammarFile getGrammarFile();

	public abstract LexicalFile getLexicalFile();

	public abstract MetaFile getMetadataFile();

	public abstract String getName();

	public abstract List<File> getOpenedFiles();

	public abstract File getProjectDir();

	public abstract String getProjectsRootPath();

	public abstract File getPropertiesFile();

	public abstract String getProperty(String propertyName);

	public abstract File getSemanticFile();

	public abstract Theme getTheme();

	public abstract File getYyLexFile();

	public abstract void putPropertiesToFile(String propertiesFile);

	public abstract void setGrammarFile(GrammarFile grammarFile);

	public abstract void setLexicalFile(LexicalFile lexFile);

	public abstract void setMetadataFile(MetaFile metadataFile);

	public abstract void setName(String name);

	public abstract void setOpenedFiles(List<File> openedFiles);

	public abstract void setPropertiesFile(PropertiesFile propertiesFile);

	public abstract void setSemamticFile(SemanticFile semFile);

	public abstract void setTheme(Theme theme);

	public abstract void setYyLexFile(File yyLexFile);

	public abstract Properties getProperties();

	public abstract void setProperties(Properties properties);
	
	public abstract void renameFile(String oldName, String newName);
	
	public abstract boolean save();

}