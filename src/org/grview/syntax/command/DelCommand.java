package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class DelCommand extends Command
{

	private String targetType;

	@Override
	public boolean addObject(Object target, Object source, Object context)
	{
		return false;
	}

	@Override
	public String getDescription()
	{
		return SyntaxDefinitions.DeleteCommand_Description;
	}

	@Override
	public String getID()
	{
		return SyntaxDefinitions.DeleteCommand;
	}

	public String getTargetType()
	{
		return targetType;
	}

	public void setTargetType(String type)
	{
		targetType = type;
	}

}
