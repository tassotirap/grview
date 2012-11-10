package org.grview.ui.component;

import org.grview.model.FileNames;

public class ComponetFactory
{
	public static AbstractComponent createFileComponent(String type)
	{
		if (type.equalsIgnoreCase(FileNames.GRAM_EXTENSION))
			return new GrammarComponent();
		if (type.equalsIgnoreCase(FileNames.LEX_EXTENSION))
			return new LexComponent();
		if (type.equalsIgnoreCase(FileNames.SEM_EXTENSION))
			return new SemComponent();
		if (type.equalsIgnoreCase(FileNames.XML_EXTENSION))
			return new XMLComponent();
		if (type.equalsIgnoreCase(FileNames.TXT_EXTENSION))
			return new SimpleTextAreaComponent();
		if (type.equalsIgnoreCase(FileNames.JAVA_EXTENSION))
			return new JavaComponent();
		if (type.equalsIgnoreCase(FileNames.IN_EXTENSION))
			return new InputAdapterComponent();
		return null;
	}
}
