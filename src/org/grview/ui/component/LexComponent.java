package org.grview.ui.component;

import org.grview.lexical.YyFactory;

public class LexComponent extends AdvancedTextAreaComponent
{

	public LexComponent()
	{
		super("java");
	}

	@Override
	public void saveFile()
	{
		super.saveFile();
		YyFactory.createYylex(rootPath, "generated_code", path);
	}
}
