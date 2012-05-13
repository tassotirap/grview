package org.grview.syntax.command;


public class CommandFactory {
	
	private static AddCommand addCommand = new AddCommand();
	private static ConnectCommand connCommand = new ConnectCommand();
	private static DelCommand delCommand = new DelCommand();
	private static DisconnectCommand dConnCommand = new DisconnectCommand();
	private static RenameCommand renameCommand = new RenameCommand();
	private static MoveCommand moveCommand = new MoveCommand();
	private static AddRoutineCommand addRoutineCommand = new AddRoutineCommand();
	private static RemoveRoutineCommand removeRoutineCommand = new RemoveRoutineCommand();
	
	public static AddCommand createAddCommand() {
		addCommand.clear();
		return addCommand;
	}
	
	public static ConnectCommand createConnectionCommand() {
		connCommand.clear();
		return connCommand;
	}
	
	public static DelCommand createDelCommand() {
		return new DelCommand();
	}
	
	public static DisconnectCommand createDisconnectionCommand() {
		dConnCommand.clear();
		return dConnCommand;
	}
	
	public static RenameCommand createRenameCommand() {
		renameCommand.clear();
		return renameCommand;
	}
	
	public static RemoveRoutineCommand createRemoveRoutineCommand() {
		removeRoutineCommand.clear();
		return removeRoutineCommand;
	}
	
	public static MoveCommand createMoveCommand() {
		moveCommand.clear();
		return moveCommand;
	}
	
	public static AddRoutineCommand createAddRoutineCommand() {
		addRoutineCommand.clear();
		return addRoutineCommand;
	}
}
