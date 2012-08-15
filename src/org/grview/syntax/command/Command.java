package org.grview.syntax.command;

import java.util.Vector;

public abstract class Command
{

	private Object context;
	private Object target;
	private Object source;
	private Object connection;
	private boolean consumed = false;

	// nested commands
	private Vector<Command> children = new Vector<Command>();
	private AsinEditor asinEditor = AsinEditor.getInstance();

	private CommandHistory history = new CommandHistory();

	public Command()
	{
	}

	public Command(Object target, Object context)
	{
		this(target, null, null, context);
	}

	public Command(Object target, Object source, Object context)
	{
		this(target, source, null, context);
	}

	public Command(Object target, Object source, Object connection, Object context)
	{
		this.target = target;
		this.source = source;
		this.connection = connection;
		this.context = context;
	}

	public void addChild(Command command)
	{
		assert command != null;
		children.add(command);
	}

	public void addChildAt(Command command, int index)
	{
		assert command != null;
		children.add(index, command);
	}

	public boolean addObject(Object target, Object context)
	{
		if (!consumed)
		{
			this.context = context;
			this.target = target;
			return true;
		}
		return false;
	}

	public boolean addObject(Object target, Object source, Object context)
	{
		if (!consumed)
		{
			this.context = context;
			this.target = target;
			this.source = source;
			return true;
		}
		return false;
	}

	public boolean addObject(Object target, Object source, Object connector, Object context)
	{
		if (!consumed)
		{
			this.context = context;
			this.target = target;
			this.source = source;
			this.connection = connector;
			return true;
		}
		return false;
	}

	public void clear()
	{
		this.consumed = false;
		context = null;
		target = null;
		source = null;
		connection = null;
	}

	public boolean execute()
	{
		boolean resp = asinEditor.consumeCommand(this);
		history.addToHistory(this);
		return resp;
	}

	public Vector<Command> getChildren()
	{
		return children;
	}

	public Object getConnection()
	{
		return connection;
	}

	public Object getContext()
	{
		return context;
	}

	public abstract String getDescription();

	public abstract String getID();

	public Object getSource()
	{
		return source;
	}

	public Object getTarget()
	{
		return target;
	}

	public boolean isConsumed()
	{
		return consumed;
	}

	public void setConsumed(boolean consumed)
	{
		this.consumed = consumed;
	}

	public boolean undo()
	{
		return history.hasNext() && asinEditor.undoCommand((Command) history.next());
	}
}
