package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class ConnectCommand extends Command
{

	@Override
	public boolean addObject(Object target, Object context)
	{
		return false;
	}

	@Override
	public boolean addObject(Object target, Object source, Object context)
	{
		return false;
	}

	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.ConnectionCommand_Description + (((String) getContext() == null) ? "" : " " + (String) getContext());
	}

	@Override
	public String getID()
	{
		return SyntaxDefinitions.ConnectionCommand;
	}

}
