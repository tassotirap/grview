package org.grview.ui.component;


public abstract class GrammarRepo
{
	private static GrammarComponent gramComponent;

	public static GrammarComponent getCompByCanvas()
	{
		return gramComponent;
	}

	public static void addGramComponent(GrammarComponent gramComponent)
	{
		GrammarRepo.gramComponent = gramComponent;
	}

}
