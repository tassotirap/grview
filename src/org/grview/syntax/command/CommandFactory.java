package org.grview.syntax.command;

public class CommandFactory
{

	private static AddCommand addCommand = new AddCommand();
	private static ConnectCommand connCommand = new ConnectCommand();
	private static DisconnectCommand dConnCommand = new DisconnectCommand();
	private static RenameCommand renameCommand = new RenameCommand();
	private static MoveCommand moveCommand = new MoveCommand();
	private static AddRoutineCommand addRoutineCommand = new AddRoutineCommand();
	private static RemoveRoutineCommand removeRoutineCommand = new RemoveRoutineCommand();

	public static AddCommand createAddCommand()
	{
		return addCommand;
	}

	public static AddRoutineCommand createAddRoutineCommand()
	{
		return addRoutineCommand;
	}

	public static ConnectCommand createConnectionCommand()
	{
		return connCommand;
	}

	public static DelCommand createDelCommand()
	{
		return new DelCommand();
	}

	public static DisconnectCommand createDisconnectionCommand()
	{
		return dConnCommand;
	}

	public static MoveCommand createMoveCommand()
	{
		return moveCommand;
	}

	public static RemoveRoutineCommand createRemoveRoutineCommand()
	{
		return removeRoutineCommand;
	}

	public static RenameCommand createRenameCommand()
	{
		return renameCommand;
	}
}
