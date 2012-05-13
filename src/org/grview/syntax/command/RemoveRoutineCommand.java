package org.grview.syntax.command;

import org.grview.syntax.grammar.model.SyntaxDefinitions;

public class RemoveRoutineCommand extends Command {

	private static String id = SyntaxDefinitions.RemoveRoutineCommand;
	@Override
	public String getDescription() {
		return SyntaxDefinitions.RemoveRoutineCommand_Description + (((String)getContext() == null)?"":" "+(String)getContext());
	}

	@Override
	public String getID() {
		return id;
	}

}
