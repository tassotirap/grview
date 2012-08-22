package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class AddRoutineCommand extends Command
{
	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.AddRoutineCommand_Description;
	}
}
