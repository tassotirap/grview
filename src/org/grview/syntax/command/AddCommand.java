package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class AddCommand extends Command
{

	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.AddCommand_Description;
	}

}
