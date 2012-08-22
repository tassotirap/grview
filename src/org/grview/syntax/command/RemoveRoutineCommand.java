package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class RemoveRoutineCommand extends Command
{

	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.RemoveRoutineCommand_Description;
	}
}
