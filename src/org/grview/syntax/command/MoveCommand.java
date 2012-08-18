package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class MoveCommand extends Command
{

	public final static String id = SyntaxDefinitions.MoveCommand;

	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.MoveCommand_Description + (((String) getContext() == null) ? "" : " " + (String) getContext());
	}

	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public boolean undo()
	{
		return true;
	}

}
