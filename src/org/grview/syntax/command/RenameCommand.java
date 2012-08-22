package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class RenameCommand extends Command
{		
	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.RenameCommand_Description;
	}
}
