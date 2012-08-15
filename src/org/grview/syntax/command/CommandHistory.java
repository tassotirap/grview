package org.grview.syntax.command;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

public class CommandHistory implements Iterator
{

	private Stack<Command> history = new Stack<Command>();

	public void addToHistory(Command comm)
	{
		history.push(comm);
	}

	@Override
	public boolean hasNext()
	{
		return !history.empty();
	}

	@Override
	public Object next()
	{
		try
		{
			return history.peek();
		}
		catch (EmptyStackException e)
		{
			return null;
		}
	}

	@Override
	public void remove()
	{
		history.pop();
	}

}
