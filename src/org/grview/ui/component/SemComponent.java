package org.grview.ui.component;

public class SemComponent extends AdvancedTextAreaComponent
{

	public SemComponent()
	{
		super("groovy");
	}

	@Override
	public void saveFile()
	{
		super.saveFile();
		// update the available actions
	}
}
