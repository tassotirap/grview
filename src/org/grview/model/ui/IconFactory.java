package org.grview.model.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.grview.model.FileNames;

public class IconFactory
{
	public enum IconType
	{
		ACTIVE_OUTPUT_ICON,
		DIR_ICON,
		GRAM_ICON,
		GRAMMAR_ICON,
		IN_ICON,
		JAVA_ICON,
		LEX_ICON,
		OUT_ICON,
		OVERVIEW_CON,
		PARSER_ICON,
		PROJECT_ICON,
		PROPERTIES_ICON,
		SEM_ICON,
		SEMANTIC_STACK_ICON,
		SYNTACTIC_STACK_ICON,
		TXT_ICON,
		XML_ICON
	}
	
	public final String ICONS_PATH = "/org/grview/images/";

	public Icon getIcon(String fileName)
	{
		if (fileName.toLowerCase().endsWith(FileNames.GRAM_EXTENSION.toLowerCase()))
		{
			return getIcon(IconType.GRAM_ICON);
		}
		else if (fileName.toLowerCase().endsWith(FileNames.LEX_EXTENSION.toLowerCase()))
		{
			return getIcon(IconType.LEX_ICON);
		}
		else if (fileName.toLowerCase().endsWith(FileNames.SEM_EXTENSION.toLowerCase()))
		{
			return getIcon(IconType.SEM_ICON);
		}
		else if (fileName.toLowerCase().endsWith(FileNames.TXT_EXTENSION.toLowerCase()))
		{
			return getIcon(IconType.TXT_ICON);
		}
		else if (fileName.toLowerCase().endsWith(FileNames.XML_EXTENSION.toLowerCase()))
		{
			return getIcon(IconType.XML_ICON);
		}
		else if (fileName.toLowerCase().endsWith(FileNames.JAVA_EXTENSION.toLowerCase()))
		{
			return getIcon(IconType.JAVA_ICON);
		}
		else if (fileName.toLowerCase().endsWith(FileNames.IN_EXTENSION.toLowerCase()))
		{
			return getIcon(IconType.IN_ICON);
		}
		else if (fileName.toLowerCase().endsWith(FileNames.OUT_EXTENSION.toLowerCase()))
		{
			return getIcon(IconType.OUT_ICON);
		}
		return new IconView();
	}

	public Icon getIcon(IconType type)
	{
		switch (type)
		{
			case ACTIVE_OUTPUT_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "active-output.png"));
			case DIR_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "project-dir.png"));
			case GRAM_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "grammar-file.png"));
			case GRAMMAR_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "grammar.png"));
			case IN_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "in-file.png"));
			case JAVA_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "java-file.png"));
			case LEX_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "lex-file.png"));
			case OUT_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "out-file.png"));
			case OVERVIEW_CON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "overview.png"));
			case PARSER_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "parser.png"));
			case PROJECT_ICON:
				return  new ImageIcon(getClass().getResource(ICONS_PATH + "project.png"));
			case PROPERTIES_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "properties.png"));
			case SEM_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "sem-file.png"));				
			case SEMANTIC_STACK_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "semantic-stack.png"));
			case SYNTACTIC_STACK_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "syntax-stack.png"));
			case TXT_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "txt-file.png"));
			case XML_ICON:
				return new ImageIcon(getClass().getResource(ICONS_PATH + "xml-file.png"));
			default:
				return null;
		}
	}
}
