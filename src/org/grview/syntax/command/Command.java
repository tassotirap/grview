package org.grview.syntax.command;

public abstract class Command
{
	private CommandHistory history = new CommandHistory();

	public abstract String getDescription();

	public boolean undo()
	{
		return history.hasNext();
	}
}
