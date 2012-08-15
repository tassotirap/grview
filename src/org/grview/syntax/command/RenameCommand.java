package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class RenameCommand extends Command
{

	@Override
	public boolean addObject(Object target, Object context)
	{
		return false;
	}

	@Override
	public boolean addObject(Object target, Object source, Object connect, Object context)
	{
		return false;
	}

	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.RenameCommand_Description + " " + getContext().toString();
	}

	@Override
	public String getID()
	{
		return SyntaxDefinitions.RenameCommand;
	}

}
