package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class ConnectCommand extends Command
{
	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.ConnectionCommand_Description;
	}

}
