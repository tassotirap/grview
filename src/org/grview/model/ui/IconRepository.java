package org.grview.model.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.grview.model.FileNames;

public class IconRepository
{

	private static IconRepository instance;

	public final String ICONS_PATH = "/org/grview/images/";
	
	public final Icon ACTIVE_OUTPUT_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "active-output.png"));
	public final Icon CONSOLE_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "console.png"));
	public final Icon DIR_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "project-dir.png"));
	public final Icon GRAM_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "grammar-file.png"));
	public final Icon GRAMMAR_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "grammar.png"));
	
	public final Icon IN_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "in-file.png"));
	public final Icon JAVA_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "java-file.png"));
	public final Icon LEX_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "lex-file.png"));
	public final Icon OUT_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "out-file.png"));
	public final Icon OVERVIEW_CON = new ImageIcon(getClass().getResource(ICONS_PATH + "overview.png"));
	public final Icon PARSER_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "parser.png"));
	public final Icon PROJECT_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "project.png"));
	public final Icon PROPERTIES_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "properties.png"));
	public final Icon SEM_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "sem-file.png"));
	public final Icon SEMANTIC_STACK_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "semantic-stack.png"));
	public final Icon SYNTACTIC_STACK_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "syntax-stack.png"));
	public final Icon TXT_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "txt-file.png"));

	public final Icon XML_ICON = new ImageIcon(getClass().getResource(ICONS_PATH + "xml-file.png"));

	private IconRepository()
	{
	}

	public static Icon getIconByFileName(String fileName)
	{
		if (fileName.toLowerCase().endsWith(FileNames.GRAM_EXTENSION.toLowerCase()))
		{
			return getInstance().GRAM_ICON;
		}
		else if (fileName.toLowerCase().endsWith(FileNames.LEX_EXTENSION.toLowerCase()))
		{
			return getInstance().LEX_ICON;
		}
		else if (fileName.toLowerCase().endsWith(FileNames.SEM_EXTENSION.toLowerCase()))
		{
			return getInstance().SEM_ICON;
		}
		else if (fileName.toLowerCase().endsWith(FileNames.TXT_EXTENSION.toLowerCase()))
		{
			return getInstance().TXT_ICON;
		}
		else if (fileName.toLowerCase().endsWith(FileNames.XML_EXTENSION.toLowerCase()))
		{
			return getInstance().XML_ICON;
		}
		else if (fileName.toLowerCase().endsWith(FileNames.JAVA_EXTENSION.toLowerCase()))
		{
			return getInstance().JAVA_ICON;
		}
		else if (fileName.toLowerCase().endsWith(FileNames.IN_EXTENSION.toLowerCase()))
		{
			return getInstance().IN_ICON;
		}
		else if (fileName.toLowerCase().endsWith(FileNames.OUT_EXTENSION.toLowerCase()))
		{
			return getInstance().OUT_ICON;
		}
		return new IconView();
	}

	public static IconRepository getInstance()
	{
		if (instance == null)
		{
			instance = new IconRepository();
		}
		return instance;
	}
}
