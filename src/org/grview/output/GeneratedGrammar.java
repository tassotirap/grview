package org.grview.output;

public class GeneratedGrammar extends Output
{

	private static GeneratedGrammar instance;

	private GeneratedGrammar()
	{
		super();
	}

	public static GeneratedGrammar getInstance()
	{
		if (instance == null)
		{
			instance = new GeneratedGrammar();
		}
		return instance;
	}
}
