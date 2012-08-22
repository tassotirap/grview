package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class DisconnectCommand extends Command
{
	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.DisconnectionCommand_Description;
	}
}
