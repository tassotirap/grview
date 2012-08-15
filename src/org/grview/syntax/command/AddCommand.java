package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class AddCommand extends Command
{

	public final static String id = SyntaxDefinitions.AddCommand;

	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.AddCommand_Description + (((String) getContext() == null) ? "" : " " + (String) getContext());
	}

	@Override
	public String getID()
	{
		return id;
	}
}
