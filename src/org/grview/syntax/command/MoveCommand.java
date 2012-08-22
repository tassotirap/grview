package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class MoveCommand extends Command
{
	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.MoveCommand_Description;
	}

	@Override
	public boolean undo()
	{
		return true;
	}

}
